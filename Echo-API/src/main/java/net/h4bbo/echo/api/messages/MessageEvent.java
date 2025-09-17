package net.h4bbo.echo.api.messages;

import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.plugin.IPluginManager;

import java.util.Objects;

public abstract class MessageEvent {
    private IEventManager eventManager;
    private IPluginManager pluginManager;

    public void inject(IEventManager eventManager, IPluginManager pluginManager) {
        if (!Objects.isNull(this.eventManager) || !Objects.isNull(this.pluginManager)) throw new RuntimeException("classes have already injected");
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
    }

    public IEventManager getEventManager() {
        return eventManager;
    }

    public IPluginManager getPluginManager() {
        return pluginManager;
    }

    public abstract int getHeaderId();

    public abstract void handle(IPlayer player, IClientCodec msg);
}
