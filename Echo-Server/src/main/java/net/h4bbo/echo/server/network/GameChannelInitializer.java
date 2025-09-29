package net.h4bbo.echo.server.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.network.connection.IConnectionManager;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.server.network.codecs.NetworkDecoder;
import net.h4bbo.echo.server.network.codecs.NetworkEncoder;
import org.oldskooler.inject4j.ServiceProvider;

public class GameChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final IEventManager eventManager;
    private final IPluginManager pluginManager;
    private final ServiceProvider serviceProvider;
    private final IConnectionManager connectionManager;

    public GameChannelInitializer(IEventManager eventManager, IPluginManager pluginManager, IConnectionManager connectionManager, ServiceProvider serviceProvider) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
        this.connectionManager = connectionManager;
        this.serviceProvider = serviceProvider;

    }

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("gameEncoder", new NetworkEncoder(this.eventManager, this.pluginManager));
        pipeline.addLast("gameDecoder", new NetworkDecoder(this.eventManager, this.pluginManager));
        pipeline.addLast("clientHandler", new GameNetworkHandler(this.eventManager, this.pluginManager, this.connectionManager, this.serviceProvider));
    }
}