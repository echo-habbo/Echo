package net.h4bbo.echo.server.network.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.connection.IConnectionManager;
import net.h4bbo.echo.api.network.connection.IConnectionSession;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager implements IConnectionManager {
    private static ConnectionManager instance;
    private final ConcurrentHashMap<ChannelId, IConnectionSession> connections;

    public ConnectionManager() {
        this.connections = new ConcurrentHashMap<>();
    }

    @Override
    public void addConnection(IConnectionSession session) {
        this.connections.putIfAbsent(session.getChannel().id(), session);
    }

    @Override
    public void removeConnection(IConnectionSession session) {
        this.connections.remove(session.getChannel().id());
    }

    @Override
    public IConnectionSession getConnection(Channel channel) {
        return this.connections.get(channel.id());
    }

    @Override
    public List<IConnectionSession> getConnections() {
        return this.connections.values().stream().toList();
    }

    @Override
    public void send(IPacketCodec composer) {

    }
}