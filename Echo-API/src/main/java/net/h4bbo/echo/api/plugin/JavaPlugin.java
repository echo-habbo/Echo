package net.h4bbo.echo.api.plugin;

import net.h4bbo.echo.api.event.IEventManager;
import org.oldskooler.inject4j.ServiceCollection;
import org.oldskooler.inject4j.ServiceProvider;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.util.Objects;

// plugin interface
public abstract class JavaPlugin {
    private IEventManager eventManager;
    private IPluginManager pluginManager;
    private SimpleLog logger;
    private ServiceProvider serviceProvider;

    public void inject(IEventManager eventManager, IPluginManager pluginManager, ServiceProvider serviceProvider) {
        if (!Objects.isNull(this.eventManager) || !Objects.isNull(this.pluginManager)) throw new RuntimeException("plugin classes have already injected");
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
        this.serviceProvider = serviceProvider;
        this.logger = SimpleLog.of(this.getClass());
    }

    public IEventManager getEventManager() {
        return eventManager;
    }

    public IPluginManager getPluginManager() {
        return pluginManager;
    }

    public abstract void assignServices(ServiceCollection services);

    /**
     * Called when the plugin is loaded
     */
    public abstract void load();

    /**
     * Called when the plugin is unloaded
     */
    public abstract void unload();

    /**
     * Get the plugin name
     */
    public String getName() {
        return this.getClass().getPackage().getImplementationTitle() != null
                ? this.getClass().getPackage().getImplementationTitle()
                : this.getClass().getSimpleName();
    }

    /**
     * Get the plugin version
     */
    public String getVersion() {
        return this.getClass().getPackage().getImplementationVersion() != null
                ? this.getClass().getPackage().getImplementationVersion()
                : "1.0.0";
    }

    public SimpleLog getLogger() {
        return logger;
    }

    public ServiceProvider getServices() {
        return serviceProvider;
    }
}