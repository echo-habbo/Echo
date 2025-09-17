package net.h4bbo.echo.api.event.types;

import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.plugin.IPluginManager;

public abstract class EventListener {
    public abstract IEventManager getEventManager();
    public abstract IPluginManager getPluginManager();

}
