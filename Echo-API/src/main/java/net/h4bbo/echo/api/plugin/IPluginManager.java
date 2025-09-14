package net.h4bbo.echo.api.plugin;

import java.util.Map;

public interface IPluginManager {

    /**
     * Load all plugins from the plugin directory
     */
    void loadAllPlugins();

    /**
     * Load a specific plugin by JAR file path
     * @param jarPath the file path to the plugin JAR
     * @return true if loaded successfully, false otherwise
     */
    boolean loadPlugin(String jarPath);

    /**
     * Unload a specific plugin
     * @param pluginName the name of the plugin
     * @return true if unloaded successfully, false otherwise
     */
    boolean unloadPlugin(String pluginName);

    /**
     * Reload a specific plugin
     * @param pluginName the name of the plugin
     * @return true if reloaded successfully, false otherwise
     */
    boolean reloadPlugin(String pluginName);

    /**
     * Reload multiple plugins
     * @param pluginNames names of plugins to reload
     * @return map of plugin names to reload result
     */
    Map<String, Boolean> reloadPlugins(String... pluginNames);

    /**
     * Get loaded plugin by name
     * @param name the name of the plugin
     * @return the plugin instance, or null if not loaded
     */
    IPlugin getPlugin(String name);

    /**
     * Get all loaded plugins
     * @return map of plugin names to plugin instances
     */
    Map<String, IPlugin> getAllPlugins();

    /**
     * Check if plugin is loaded
     * @param name the name of the plugin
     * @return true if loaded, false otherwise
     */
    boolean isPluginLoaded(String name);

    /**
     * Get plugin metadata
     * @param name the name of the plugin
     * @return the plugin metadata, or null if not loaded
     */
    PluginMetadata getPluginMetadata(String name);

    /**
     * Unload all plugins
     */
    void unloadAllPlugins();
}
