package net.h4bbo.echo.server.network.connection;

import io.netty.channel.Channel;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.connection.IConnectionManager;
import net.h4bbo.echo.api.network.connection.IConnectionSession;
import net.h4bbo.echo.api.plugin.IPluginManager;
import org.oldskooler.inject4j.ServiceProvider;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager implements IConnectionManager {
    private static ConnectionManager instance;
    private final ConcurrentHashMap<Channel, IConnectionSession> connections;

    private final IEventManager eventManager;
    private final IPluginManager pluginManager;
    private final ServiceProvider serviceProvider;

    public ConnectionManager(IEventManager eventManager, IPluginManager pluginManager, ServiceProvider serviceProvider) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
        this.serviceProvider = serviceProvider;
        this.connections = new ConcurrentHashMap<>();
    }

    @Override
    public void addConnection(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        connections.put(channel, new ConnectionSession(channel, this.eventManager, this.pluginManager, this.serviceProvider));
    }

    @Override
    public void removeConnection(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

         connections.remove(channel);
    }

    @Override
    public IConnectionSession getConnection(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        connections.get(channel);

        return connections.getOrDefault(channel, null);
    }

    @Override
    public void send(IPacketCodec composer) {

    }
}