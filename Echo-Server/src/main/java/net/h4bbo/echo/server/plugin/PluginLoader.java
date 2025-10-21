package net.h4bbo.echo.server.plugin;

import net.h4bbo.echo.api.IAdvancedScheduler;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.server.scheduler.AdvancedScheduler;
import org.oldskooler.inject4j.ServiceCollection;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private final IEventManager eventManager;
    private final IPluginManager pluginManager;
    private final IAdvancedScheduler advancedScheduler;

    public PluginLoader(IEventManager eventManager, IPluginManager pluginManager, AdvancedScheduler advancedScheduler) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
        this.advancedScheduler = advancedScheduler;
    }

    public ArrayList<JavaPlugin> findAndLoadAllJavaPlugins(ServiceCollection serviceCollection) {
        var baseClass = JavaPlugin.class;
        var instantiated = new ArrayList<JavaPlugin>();

        // Get classpath entries
        String classpath = System.getProperty("java.class.path", "");
        String pathSep = File.pathSeparator;
        String[] entries = classpath.split(java.util.regex.Pattern.quote(pathSep));

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Set<String> seenClassNames = new HashSet<>();

        for (String entry : entries) {
            if (entry == null || entry.isEmpty()) continue;
            File f = new File(entry);
            if (!f.exists()) continue;

            try {
                if (f.isDirectory()) {
                    scanDirectory(f.toPath(), f.toPath(), seenClassNames);
                } else if (f.isFile() && entry.toLowerCase().endsWith(".jar")) {
                    scanJarFile(f, seenClassNames);
                }
            } catch (IOException e) {
                System.err.println("Failed to scan classpath entry: " + entry + " : " + e);
            }
        }

        for (String className : seenClassNames) {
            // optional: skip inner classes if you don't want to attempt them
            if (className.contains("$")) continue;

            Class<?> cls = null;

            try {
                // Load class without initializing (we'll initialize when we instantiate)
                cls = Class.forName(className, false, classLoader);
            } catch (Throwable _) { }

            if (cls == null)
                continue;

            try {
                // skip interfaces and abstract classes
                int mods = cls.getModifiers();
                if (Modifier.isInterface(mods) || Modifier.isAbstract(mods)) continue;

                // ensure subclass of baseClass
                if (!baseClass.isAssignableFrom(cls)) continue;

                @SuppressWarnings("unchecked")
                Class<? extends JavaPlugin> tClass = (Class<? extends JavaPlugin>) cls;

                // find no-arg constructor
                Constructor<? extends JavaPlugin> ctor = tClass.getDeclaredConstructor();
                ctor.setAccessible(true);

                // instantiate (this will initialize the class)
                JavaPlugin instance = ctor.newInstance();

                // hand to plugin loader
                try {
                    this.pluginManager.loadPluginInstance(instance, serviceCollection);
                    instantiated.add(instance);
                } catch (Exception ex) {
                    SimpleLog.of(PluginLoader.class).error("Failed to load plugin: ", ex);
                }

            } catch (Exception ex) {
                SimpleLog.of(PluginLoader.class).error("Failed to load plugin class path: ", ex);
            }
        }

        return instantiated;
    }

    // Walk a directory and collect .class file names
    private static void scanDirectory(Path root, Path dir, Set<String> out) throws IOException {
        Files.walk(dir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".class"))
                .forEach(p -> {
                    String clsName = pathToClassName(root, p);
                    if (clsName != null) out.add(clsName);
                });
    }

    private static String pathToClassName(Path root, Path classFile) {
        Path rel = root.relativize(classFile);
        String s = rel.toString();
        if (!s.endsWith(".class")) return null;
        s = s.substring(0, s.length() - 6); // remove ".class"
        // convert file separators to dots
        return s.replace(File.separatorChar, '.');
    }

    // Scan a jar and collect .class entries
    private static void scanJarFile(File jarFile, Set<String> out) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.isDirectory()) continue;
                String name = je.getName();
                if (!name.endsWith(".class")) continue;
                String clsName = name.substring(0, name.length() - 6).replace('/', '.');
                out.add(clsName);
            }
        }
    }
}
