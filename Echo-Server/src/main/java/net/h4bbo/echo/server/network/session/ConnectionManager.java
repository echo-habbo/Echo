package net.h4bbo.echo.server.network.session;

import io.netty.channel.Channel;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static ConnectionManager instance;
    private final ConcurrentHashMap<Channel, ConnectionSession> connections;

    private ConnectionManager() {
        this.connections = new ConcurrentHashMap<>();
    }

    public void addConnection(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel must not be null");
        }

        connections.put(channel, new ConnectionSession(channel));
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

   public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }

        return instance;
   }
}