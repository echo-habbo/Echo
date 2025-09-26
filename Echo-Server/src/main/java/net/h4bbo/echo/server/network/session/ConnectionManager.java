package net.h4bbo.echo.server.network.session;

import io.netty.channel.Channel;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.messages.MessageHandler;
import net.h4bbo.echo.api.network.session.IConnectionSession;
import net.h4bbo.echo.api.plugin.IPluginManager;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static ConnectionManager instance;
    private final ConcurrentHashMap<Channel, ConnectionSession> connections;
    private final IEventManager eventManager;
    private final IPluginManager pluginManager;

    public ConnectionManager(IEventManager eventManager, IPluginManager pluginManager) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
        this.connections = new ConcurrentHashMap<>();

    }

    public void addConnection(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        connections.put(channel, new ConnectionSession(channel, eventManager, pluginManager));
    }

    public void removeConnection(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

         connections.remove(channel);
    }

    public ConnectionSession getConnection(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        connections.get(channel);

        return connections.getOrDefault(channel, null);
    }
}