package net.h4bbo.echo.api.plugin;

import org.oldskooler.inject4j.ServiceCollection;
import org.oldskooler.inject4j.ServiceProvider;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

// plugin interface
public interface IPlugin {
    /**
     * Called when the plugin is loaded
     */
    void load();

    /**
     * Called when the plugin is unloaded
     */
    void unload();

    /**
     * Get the plugin name
     */
    default String getName() {
        return this.getClass().getPackage().getImplementationTitle() != null
                ? this.getClass().getPackage().getImplementationTitle()
                : this.getClass().getSimpleName();
    }

    /**
     * Get the plugin version
     */
    default String getVersion() {
        return this.getClass().getPackage().getImplementationVersion() != null
                ? this.getClass().getPackage().getImplementationVersion()
                : "1.0.0";
    }
}