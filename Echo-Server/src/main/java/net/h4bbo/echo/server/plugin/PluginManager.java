package net.h4bbo.echo.server.plugin;

import net.h4bbo.echo.api.IAdvancedScheduler;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.plugin.*;
import org.oldskooler.inject4j.ServiceCollection;
import org.oldskooler.inject4j.ServiceProvider;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager implements IPluginManager {
    private static final SimpleLog log = SimpleLog.of(PluginManager.class);

    private final String pluginDirectory;
    private final Map<String, PluginMetadata> loadedPlugins = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> dependencyGraph = new ConcurrentHashMap<>();
    private final List<String> pendingToEnable = new CopyOnWriteArrayList<>();
    private final List<String> enabledPlugins = new CopyOnWriteArrayList<>();

    private final IEventManager eventManager;
    private final IAdvancedScheduler advancedScheduler;

    public PluginManager(String pluginDirectory, IEventManager eventManager, IAdvancedScheduler advancedScheduler) {
        this.pluginDirectory = pluginDirectory;
        this.eventManager = eventManager;
        this.advancedScheduler = advancedScheduler;
    }

    /**
     * Load all plugins from the plugin directory.
     * This will *scan + create* all plugin metadata/instances first, then enable them at the end.
     */
    public void loadAllPlugins(ServiceCollection serviceCollection) {
        File dir = new File(pluginDirectory);
        boolean dirExists = dir.exists();

        if (!dir.exists()) {
            dirExists = dir.mkdir();
        }

        if (!dirExists || !dir.isDirectory()) {
            log.warn("plugin directory does not exist: " + pluginDirectory);
            return;
        }

        File[] jarFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles == null) {
            log.info("No JAR files found in plugin directory");
            return;
        }

        // First pass: collect all plugin metadata (scan + instantiate plugin objects and register them)
        Map<String, PluginCandidate> candidates = new LinkedHashMap<>();
        for (File jarFile : jarFiles) {
            try {
                PluginCandidate candidate = scanPlugin(jarFile);
                if (candidate != null) {
                    candidates.put(candidate.name, candidate);
                }
            } catch (Exception e) {
                log.error("Error scanning plugin: " + jarFile.getName(), e);
            }
        }

        // Create metadata/instances for all candidates without enabling them.
        for (PluginCandidate candidate : candidates.values()) {
            try {
                registerPluginCandidate(candidate, serviceCollection);
            } catch (Exception e) {
                log.error("Error registering plugin candidate: " + candidate.name, e);
            }
        }
    }

    /**
     * Load a specific plugin by JAR file path.
     *
     * NOTE: This registers the plugin (creates metadata and instance) and adds it to pendingToEnable,
     * but does NOT enable it immediately. Call enablePendingPlugins() after loading multiple plugins,
     * or call it manually to attempt enabling now.
     */
    public boolean loadPlugin(String jarPath, ServiceCollection serviceCollection) {
        try {
            File jarFile = new File(jarPath);
            PluginCandidate candidate = scanPlugin(jarFile);
            if (candidate == null) {
                return false;
            }

            // Register candidate (creates instance/metadata and marks pending) but do not enable here.
            return registerPluginCandidate(candidate, serviceCollection);
        } catch (Exception e) {
            log.error("Error loading plugin: " + jarPath, e);
            return false;
        }
    }

    /**
     * Unload a specific plugin
     */
    public boolean unloadPlugin(String pluginName) {
        PluginMetadata metadata = loadedPlugins.get(pluginName);
        if (metadata == null) {
            log.error("plugin not loaded due to bad metadata: " + pluginName);
            return false;
        }

        // Check for dependents
        Set<String> dependents = findDependents(pluginName);
        if (!dependents.isEmpty()) {
            log.error("Cannot unload plugin " + pluginName +
                    " - other plugins depend on it: " + dependents);
            return false;
        }

        try {
            // If not enabled, just remove
            if (enabledPlugins.contains(pluginName)) {
                metadata.getInstance().unload();
                enabledPlugins.remove(pluginName);
            }

            loadedPlugins.remove(pluginName);
            dependencyGraph.remove(pluginName);
            pendingToEnable.remove(pluginName);

            // Close class loader if possible
            if (metadata.getClassLoader() instanceof PluginClassLoader) {
                try {
                    ((PluginClassLoader) metadata.getClassLoader()).close();
                } catch (IOException e) {
                    log.error("Error closing class loader", e);
                }
            }

            log.info("Unloaded plugin: " + pluginName);
            return true;
        } catch (Exception e) {
            log.error("Error unloading plugin: " + pluginName, e);
            return false;
        }
    }

    /**
     * Reload a specific plugin
     */
    public boolean reloadPlugin(String pluginName) {
        PluginMetadata metadata = loadedPlugins.get(pluginName);
        if (metadata == null) {
            log.error("plugin not loaded: " + pluginName);
            return false;
        }

        String jarPath = metadata.getJarPath();

        // Unload first
        if (!unloadPlugin(pluginName)) {
            return false;
        }

        // Then register again (but do not enable automatically)
        return loadPlugin(jarPath, null);
    }

    /**
     * Reload multiple plugins
     */
    public Map<String, Boolean> reloadPlugins(String... pluginNames) {
        Map<String, Boolean> results = new HashMap<>();
        for (String pluginName : pluginNames) {
            results.put(pluginName, reloadPlugin(pluginName));
        }
        return results;
    }

    /**
     * Get loaded plugin by name
     */
    public JavaPlugin getPlugin(String name) {
        PluginMetadata metadata = loadedPlugins.get(name);
        return metadata != null ? metadata.getInstance() : null;
    }

    /**
     * Get all loaded plugins
     */
    public Map<String, JavaPlugin> getAllPlugins() {
        Map<String, JavaPlugin> plugins = new HashMap<>();
        for (Map.Entry<String, PluginMetadata> entry : loadedPlugins.entrySet()) {
            plugins.put(entry.getKey(), entry.getValue().getInstance());
        }
        return plugins;
    }

    /**
     * Check if plugin is loaded (registered)
     */
    public boolean isPluginLoaded(String name) {
        return loadedPlugins.containsKey(name);
    }

    /**
     * Check if plugin is enabled
     */
    public boolean isPluginEnabled(String name) {
        return enabledPlugins.contains(name);
    }

    /**
     * Get plugin metadata
     */
    public PluginMetadata getPluginMetadata(String name) {
        return loadedPlugins.get(name);
    }

    /**
     * Unload all plugins
     */
    public void unloadAllPlugins() {
        // Unload in reverse dependency order of currently enabled plugins
        List<String> unloadOrder = new ArrayList<>(enabledPlugins);
        Collections.reverse(unloadOrder);

        for (String pluginName : unloadOrder) {
            try {
                unloadPlugin(pluginName);
            } catch (Exception e) {
                log.error("Error unloading plugin: " + pluginName, e);
            }
        }

        // Remove any remaining registered but not enabled plugins
        for (String pluginName : new ArrayList<>(loadedPlugins.keySet())) {
            if (!enabledPlugins.contains(pluginName)) {
                unloadPlugin(pluginName);
            }
        }
    }

    // Private helper methods

    private static class PluginCandidate {
        String name;
        String version;
        String[] dependencies;
        String jarPath;
        Class<? extends JavaPlugin> pluginClass;
    }

    private PluginCandidate scanPlugin(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            URL jarUrl = jarFile.toURI().toURL();
            PluginClassLoader classLoader = new PluginClassLoader(
                    new URL[]{jarUrl}, getClass().getClassLoader(), jarFile.getName());

            // Find plugin classes
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace('/', '.')
                            .replace(".class", "");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (JavaPlugin.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                            @SuppressWarnings("unchecked")
                            Class<? extends JavaPlugin> pluginClass = (Class<? extends JavaPlugin>) clazz;

                            PluginCandidate candidate = new PluginCandidate();
                            candidate.pluginClass = pluginClass;
                            candidate.jarPath = jarFile.getAbsolutePath();

                            // Get plugin instance to read metadata
                            JavaPlugin tempInstance = pluginClass.getDeclaredConstructor().newInstance();
                            candidate.name = tempInstance.getName();
                            candidate.version = tempInstance.getVersion();

                            // Check for dependencies
                            DependsOn dependsOn = pluginClass.getAnnotation(DependsOn.class);
                            candidate.dependencies = dependsOn != null ? dependsOn.value() : new String[0];

                            return candidate;
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // Skip classes that can't be loaded
                    }
                }
            }
        }
        return null;
    }

    /**
     * Register a candidate (create classloader, instantiate plugin instance, add metadata and dependency graph),
     * but do NOT enable it yet. Add to pendingToEnable.
     */
    private boolean registerPluginCandidate(PluginCandidate candidate, ServiceCollection serviceCollection) throws Exception {
        if (candidate == null) return false;
        if (loadedPlugins.containsKey(candidate.name)) {
            log.error("plugin already registered: " + candidate.name);
            return false;
        }

        // Create class loader and instantiate plugin
        File jarFile = new File(candidate.jarPath);
        URL jarUrl = jarFile.toURI().toURL();
        PluginClassLoader classLoader = new PluginClassLoader(
                new URL[]{jarUrl}, getClass().getClassLoader(), candidate.name);

        Class<? extends JavaPlugin> pluginClass = classLoader.loadClass(candidate.pluginClass.getName())
                .asSubclass(JavaPlugin.class);
        JavaPlugin plugin = pluginClass.getDeclaredConstructor().newInstance();

        // Create metadata
        PluginMetadata metadata = new PluginMetadata(
                candidate.name, candidate.version, candidate.dependencies,
                plugin, classLoader, candidate.jarPath);

        if (serviceCollection != null) {
            registerServices(serviceCollection, plugin);
        }

        // Register metadata and dependency graph, but don't call enablePlugin() yet.
        loadedPlugins.put(candidate.name, metadata);
        dependencyGraph.put(candidate.name, new HashSet<>(Arrays.asList(candidate.dependencies)));
        pendingToEnable.add(candidate.name);

        log.info("Registered plugin: " + candidate.name + " (pending enable)");
        return true;
    }

    @SuppressWarnings("unchecked")
    private static void registerServices(ServiceCollection serviceCollection, JavaPlugin plugin) {
        // Assign own plugin instance so anything can get it and also have it injected in constructors
        Class<JavaPlugin> clazz = (Class<JavaPlugin>) plugin.getClass();
        serviceCollection.addSingleton(clazz, () -> plugin);

        // Allow our own services to be assigned and shared
        plugin.assignServices(serviceCollection);
    }

    /**
     * Add a plugin instance directly.
     * Useful for testing or dynamically created plugins.
     *
     * This will register the plugin and add it to pendingToEnable, but will NOT enable it.
     * Call enablePendingPlugins() after registering instances to enable them in correct order.
     */
    public boolean loadPluginInstance(JavaPlugin pluginInstance, ServiceCollection serviceCollection) {
        if (pluginInstance == null) return false;

        String name = pluginInstance.getName();
        if (loadedPlugins.containsKey(name)) {
            log.error("plugin already registered: " + name);
            return false;
        }

        // Handle dependencies if present
        DependsOn dependsOn = pluginInstance.getClass().getAnnotation(DependsOn.class);
        String[] dependencies = dependsOn != null ? dependsOn.value() : new String[0];

        // Create PluginMetadata (no JAR)
        PluginMetadata metadata = new PluginMetadata(
                name,
                pluginInstance.getVersion(),
                dependencies,
                pluginInstance,
                pluginInstance.getClass().getClassLoader(),
                null // no jar path
        );

        registerServices(serviceCollection, pluginInstance);

        loadedPlugins.put(name, metadata);
        dependencyGraph.put(name, new HashSet<>(Arrays.asList(dependencies)));
        pendingToEnable.add(name);

        // log.info("Found plugin: " + name);

        return true;
    }

    /**
     * Enable all pending registered plugins in a dependency-respecting order.
     * - It will perform a topological sort on the current dependencyGraph (only for registered plugins).
     * - If a plugin has a missing dependency (not registered), it will be skipped and a log entry will be created.
     * - If a circular dependency is detected it will be logged and those plugins will not be enabled.
     *
     * This method can be called after doing several loadPlugin(...) and loadPluginInstance(...) calls.
     */
    @Override
    public void enablePendingPlugins(ServiceProvider serviceProvider) {
        // Build a map of pluginName -> dependencies filtered to registered plugins
        Map<String, Set<String>> graph = new HashMap<>();
        for (String pluginName : loadedPlugins.keySet()) {
            Set<String> deps = dependencyGraph.getOrDefault(pluginName, Collections.emptySet());
            // keep only dependencies that are registered (we cannot enable against unregistered plugins)
            Set<String> filtered = new HashSet<>();
            for (String d : deps) {
                if (loadedPlugins.containsKey(d)) {
                    filtered.add(d);
                } else {
                    log.warn("Plugin " + pluginName + " depends on unregistered plugin " + d + ", will skip until registered.");
                }
            }
            graph.put(pluginName, filtered);
        }

        List<String> order;
        try {
            order = topoSort(graph);
        } catch (IllegalStateException ise) {
            log.error("Cannot enable pending plugins due to circular dependency: " + ise.getMessage());
            return;
        }

        // Enable in topological order, but only if pending
        for (String pluginName : order) {
            if (!pendingToEnable.contains(pluginName)) continue; // already enabled or not pending
            // ensure all dependencies are enabled before enabling this one
            Set<String> deps = graph.getOrDefault(pluginName, Collections.emptySet());
            boolean depsEnabled = true;
            for (String d : deps) {
                if (!enabledPlugins.contains(d)) {
                    depsEnabled = false;
                    log.warn("Skipping enable of " + pluginName + " because dependency not yet enabled: " + d);
                    break;
                }
            }
            if (!depsEnabled) continue;

            PluginMetadata metadata = loadedPlugins.get(pluginName);
            if (metadata == null) {
                log.error("Pending plugin metadata missing for: " + pluginName);
                pendingToEnable.remove(pluginName);
                continue;
            }

            try {
                enablePlugin(metadata.getInstance(), serviceProvider);
                enabledPlugins.add(pluginName);
                pendingToEnable.remove(pluginName);
                // log.info("Enabled plugin: " + pluginName);
            } catch (Exception e) {
                log.error("Error enabling plugin: " + pluginName, e);
                // do not remove from pending; maybe a later attempt will succeed
            }
        }

        // If there are still pending plugins that couldn't be enabled because their dependencies were not enabled,
        // attempt another pass until no progress is made (handles multi-level dependency chains).
        boolean progress = true;
        while (progress && !pendingToEnable.isEmpty()) {
            progress = false;
            for (String pluginName : new ArrayList<>(pendingToEnable)) {
                Set<String> deps = graph.getOrDefault(pluginName, Collections.emptySet());
                boolean depsEnabledNow = true;
                for (String d : deps) {
                    if (!enabledPlugins.contains(d)) {
                        depsEnabledNow = false;
                        break;
                    }
                }
                if (!depsEnabledNow) continue;

                PluginMetadata metadata = loadedPlugins.get(pluginName);
                if (metadata == null) {
                    pendingToEnable.remove(pluginName);
                    progress = true;
                    continue;
                }

                try {
                    enablePlugin(metadata.getInstance(), serviceProvider);
                    enabledPlugins.add(pluginName);
                    pendingToEnable.remove(pluginName);
                    progress = true;

                } catch (Exception e) {
                    log.error("Error enabling plugin on later pass: " + pluginName, e);
                    // leave pending
                }
            }
        }

        if (!pendingToEnable.isEmpty()) {
            log.warn("Some plugins remain pending and could not be enabled (missing dependencies or errors): " + pendingToEnable);
        }
    }

    /**
     * Topological sort for the dependency graph.
     * Throws IllegalStateException on cycles.
     */
    private List<String> topoSort(Map<String, Set<String>> graph) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfsTopo(node, graph, visited, visiting, result);
            }
        }
        return result;
    }

    private void dfsTopo(String node, Map<String, Set<String>> graph,
                         Set<String> visited, Set<String> visiting, List<String> result) {
        if (visiting.contains(node)) {
            throw new IllegalStateException("Circular dependency involving: " + node);
        }
        if (visited.contains(node)) return;

        visiting.add(node);
        for (String dep : graph.getOrDefault(node, Collections.emptySet())) {
            dfsTopo(dep, graph, visited, visiting, result);
        }
        visiting.remove(node);
        visited.add(node);
        result.add(node);
    }

    private void enablePlugin(JavaPlugin pluginInstance, ServiceProvider serviceProvider) {
        log.info("Loading plugin: {} {}", pluginInstance.getName(), pluginInstance.getVersion());

        try {
            pluginInstance.inject(this.eventManager, this, this.advancedScheduler, serviceProvider);
        } catch (Exception ignored) { }

        pluginInstance.load();
    }

    private Set<String> findDependents(String pluginName) {
        Set<String> dependents = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : dependencyGraph.entrySet()) {
            if (entry.getValue().contains(pluginName)) {
                dependents.add(entry.getKey());
            }
        }
        return dependents;
    }
}
