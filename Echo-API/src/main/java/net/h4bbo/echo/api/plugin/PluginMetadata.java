package net.h4bbo.echo.api.plugin;

/**
 * Descriptor of a loaded plugin instance and its owning loader.
 */
public class PluginMetadata {
    private final String name;
    private final String version;
    private final String[] dependencies;
    private final IPlugin instance;
    private final ClassLoader classLoader;
    private final String jarPath;

    public PluginMetadata(String name, String version, String[] dependencies,
                          IPlugin instance, ClassLoader classLoader, String jarPath) {
        this.name = name;
        this.version = version;
        this.dependencies = dependencies != null ? dependencies : new String[0];
        this.instance = instance;
        this.classLoader = classLoader;
        this.jarPath = jarPath;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public IPlugin getInstance() {
        return instance;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getJarPath() {
        return jarPath;
    }
}