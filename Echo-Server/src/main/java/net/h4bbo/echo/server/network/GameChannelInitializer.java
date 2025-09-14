package net.h4bbo.echo.server.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.h4bbo.echo.server.network.codecs.NetworkDecoder;
import net.h4bbo.echo.server.network.codecs.NetworkEncoder;

public class GameChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final GameServer server;

    public GameChannelInitializer(GameServer server) {
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("gameEncoder", new NetworkEncoder());
        pipeline.addLast("gameDecoder", new NetworkDecoder());
        pipeline.addLast("clientHandler", new GameNetworkHandler());
    }
}