package net.h4bbo.echo.server.plugin;

import net.h4bbo.echo.api.plugin.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginManager implements IPluginManager {
    private static final Logger LOGGER = Logger.getLogger(PluginManager.class.getName());

    private final String pluginDirectory;
    private final Map<String, PluginMetadata> loadedPlugins = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> dependencyGraph = new ConcurrentHashMap<>();

    public PluginManager(String pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    /**
     * Load all plugins from the plugin directory
     */
    public void loadAllPlugins() {
        File dir = new File(pluginDirectory);
        boolean dirExists = dir.exists();

        if (!dir.exists()) {
            dirExists = dir.mkdir();
        }

        if (!dirExists || !dir.isDirectory()) {
            LOGGER.warning("plugin directory does not exist: " + pluginDirectory);
            return;
        }

        File[] jarFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles == null) {
            LOGGER.info("No JAR files found in plugin directory");
            return;
        }

        // First pass: collect all plugin metadata
        Map<String, PluginCandidate> candidates = new HashMap<>();
        for (File jarFile : jarFiles) {
            try {
                PluginCandidate candidate = scanPlugin(jarFile);
                if (candidate != null) {
                    candidates.put(candidate.name, candidate);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error scanning plugin: " + jarFile.getName(), e);
            }
        }

        // Second pass: resolve dependencies and load in order
        List<String> loadOrder = resolveDependencyOrder(candidates);
        for (String pluginName : loadOrder) {
            PluginCandidate candidate = candidates.get(pluginName);
            if (candidate != null) {
                try {
                    loadPlugin(candidate);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error loading plugin: " + pluginName, e);
                }
            }
        }
    }

    /**
     * Load a specific plugin by JAR file path
     */
    public boolean loadPlugin(String jarPath) {
        try {
            File jarFile = new File(jarPath);
            PluginCandidate candidate = scanPlugin(jarFile);
            if (candidate == null) {
                return false;
            }

            // Check dependencies
            for (String dep : candidate.dependencies) {
                if (!loadedPlugins.containsKey(dep)) {
                    LOGGER.severe("Dependency not loaded: " + dep + " for plugin: " + candidate.name);
                    return false;
                }
            }

            return loadPlugin(candidate);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading plugin: " + jarPath, e);
            return false;
        }
    }

    /**
     * Unload a specific plugin
     */
    public boolean unloadPlugin(String pluginName) {
        PluginMetadata metadata = loadedPlugins.get(pluginName);
        if (metadata == null) {
            LOGGER.warning("plugin not loaded: " + pluginName);
            return false;
        }

        // Check for dependents
        Set<String> dependents = findDependents(pluginName);
        if (!dependents.isEmpty()) {
            LOGGER.severe("Cannot unload plugin " + pluginName +
                    " - other plugins depend on it: " + dependents);
            return false;
        }

        try {
            metadata.getInstance().unload();
            loadedPlugins.remove(pluginName);
            dependencyGraph.remove(pluginName);

            // Close class loader if possible
            if (metadata.getClassLoader() instanceof PluginClassLoader) {
                try {
                    ((PluginClassLoader) metadata.getClassLoader()).close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing class loader", e);
                }
            }

            LOGGER.info("Unloaded plugin: " + pluginName);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error unloading plugin: " + pluginName, e);
            return false;
        }
    }

    /**
     * Reload a specific plugin
     */
    public boolean reloadPlugin(String pluginName) {
        PluginMetadata metadata = loadedPlugins.get(pluginName);
        if (metadata == null) {
            LOGGER.warning("plugin not loaded: " + pluginName);
            return false;
        }

        String jarPath = metadata.getJarPath();

        // Unload first
        if (!unloadPlugin(pluginName)) {
            return false;
        }

        // Then load again
        return loadPlugin(jarPath);
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
    public IPlugin getPlugin(String name) {
        PluginMetadata metadata = loadedPlugins.get(name);
        return metadata != null ? metadata.getInstance() : null;
    }

    /**
     * Get all loaded plugins
     */
    public Map<String, IPlugin> getAllPlugins() {
        Map<String, IPlugin> plugins = new HashMap<>();
        for (Map.Entry<String, PluginMetadata> entry : loadedPlugins.entrySet()) {
            plugins.put(entry.getKey(), entry.getValue().getInstance());
        }
        return plugins;
    }

    /**
     * Check if plugin is loaded
     */
    public boolean isPluginLoaded(String name) {
        return loadedPlugins.containsKey(name);
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
        // Unload in reverse dependency order
        List<String> unloadOrder = new ArrayList<>(loadedPlugins.keySet());
        Collections.reverse(unloadOrder);

        for (String pluginName : unloadOrder) {
            try {
                unloadPlugin(pluginName);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error unloading plugin: " + pluginName, e);
            }
        }
    }

    // Private helper methods

    private static class PluginCandidate {
        String name;
        String version;
        String[] dependencies;
        String jarPath;
        Class<? extends IPlugin> pluginClass;
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
                        if (IPlugin.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                            @SuppressWarnings("unchecked")
                            Class<? extends IPlugin> pluginClass = (Class<? extends IPlugin>) clazz;

                            PluginCandidate candidate = new PluginCandidate();
                            candidate.pluginClass = pluginClass;
                            candidate.jarPath = jarFile.getAbsolutePath();

                            // Get plugin instance to read metadata
                            IPlugin tempInstance = pluginClass.getDeclaredConstructor().newInstance();
                            candidate.name = tempInstance.getName();
                            candidate.version = tempInstance.getVersion();

                            // Check for dependencies
                            DependsOnAttribute dependsOn = pluginClass.getAnnotation(DependsOnAttribute.class);
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

    private boolean loadPlugin(PluginCandidate candidate) throws Exception {
        // Check if already loaded
        if (loadedPlugins.containsKey(candidate.name)) {
            LOGGER.warning("plugin already loaded: " + candidate.name);
            return false;
        }

        // Create class loader
        File jarFile = new File(candidate.jarPath);
        URL jarUrl = jarFile.toURI().toURL();
        PluginClassLoader classLoader = new PluginClassLoader(
                new URL[]{jarUrl}, getClass().getClassLoader(), candidate.name);

        // Load and instantiate plugin
        Class<? extends IPlugin> pluginClass = classLoader.loadClass(candidate.pluginClass.getName())
                .asSubclass(IPlugin.class);
        IPlugin plugin = pluginClass.getDeclaredConstructor().newInstance();

        // Create metadata
        PluginMetadata metadata = new PluginMetadata(
                candidate.name, candidate.version, candidate.dependencies,
                plugin, classLoader, candidate.jarPath);

        // Store metadata
        loadedPlugins.put(candidate.name, metadata);
        dependencyGraph.put(candidate.name, new HashSet<>(Arrays.asList(candidate.dependencies)));

        // Load the plugin
        plugin.load();

        LOGGER.info("Loaded plugin: " + candidate.name + " v" + candidate.version);
        return true;
    }

    private List<String> resolveDependencyOrder(Map<String, PluginCandidate> candidates) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String pluginName : candidates.keySet()) {
            if (!visited.contains(pluginName)) {
                resolveDependencyOrder(pluginName, candidates, result, visited, visiting);
            }
        }

        return result;
    }

    private void resolveDependencyOrder(String pluginName, Map<String, PluginCandidate> candidates,
                                        List<String> result, Set<String> visited, Set<String> visiting) {
        if (visiting.contains(pluginName)) {
            throw new IllegalStateException("Circular dependency detected involving: " + pluginName);
        }

        if (visited.contains(pluginName)) {
            return;
        }

        visiting.add(pluginName);

        PluginCandidate candidate = candidates.get(pluginName);
        if (candidate != null) {
            for (String dependency : candidate.dependencies) {
                if (candidates.containsKey(dependency)) {
                    resolveDependencyOrder(dependency, candidates, result, visited, visiting);
                } else if (!loadedPlugins.containsKey(dependency)) {
                    LOGGER.warning("Missing dependency: " + dependency + " for plugin: " + pluginName);
                }
            }
        }

        visiting.remove(pluginName);
        visited.add(pluginName);
        result.add(pluginName);
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