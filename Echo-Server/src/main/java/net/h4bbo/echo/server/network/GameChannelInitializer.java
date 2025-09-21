package net.h4bbo.echo.server.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.server.network.codecs.NetworkDecoder;
import net.h4bbo.echo.server.network.codecs.NetworkEncoder;

import java.util.concurrent.ConcurrentHashMap;

public class GameChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final IEventManager eventManager;
    private final IPluginManager pluginManager;

    public GameChannelInitializer(IEventManager eventManager, IPluginManager pluginManager) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;

    }

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("gameEncoder", new NetworkEncoder(this.eventManager, this.pluginManager));
        pipeline.addLast("gameDecoder", new NetworkDecoder(this.eventManager, this.pluginManager));
        pipeline.addLast("clientHandler", new GameNetworkHandler(this.eventManager, this.pluginManager));
    }
}