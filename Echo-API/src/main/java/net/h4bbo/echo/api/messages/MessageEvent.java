package net.h4bbo.echo.api.messages;

import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.sql.SQLException;
import java.util.Objects;

public abstract class MessageEvent {
    private IEventManager eventManager;
    private IPluginManager pluginManager;
    private JavaPlugin plugin;

    public void inject(IEventManager eventManager, IPluginManager pluginManager, JavaPlugin plugin) {
        if (!Objects.isNull(this.eventManager) || !Objects.isNull(this.pluginManager)) throw new RuntimeException("message event classes have already injected");
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    public IEventManager getEventManager() {
        return eventManager;
    }

    public IPluginManager getPluginManager() {
        return pluginManager;
    }

    public abstract int getHeaderId();

    public abstract void handle(IPlayer player, IClientCodec msg);

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public SimpleLog getLogger() {
        return SimpleLog.of(this.getClass());
    }
}
