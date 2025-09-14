package net.h4bbo.echo.server.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import net.h4bbo.echo.common.messages.headers.OutgoingEvents;
import net.h4bbo.echo.common.network.codecs.ClientCodec;
import net.h4bbo.echo.common.network.codecs.PacketCodec;
import net.h4bbo.echo.server.network.session.ConnectionSession;

public class GameNetworkHandler extends SimpleChannelInboundHandler<ClientCodec> {
    public static final AttributeKey<ConnectionSession> CONNECTION_KEY = AttributeKey.newInstance("CONNECTION_KEY");

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        var connection = new ConnectionSession(ctx.channel());

        ctx.channel().attr(CONNECTION_KEY).setIfAbsent(connection);

        PacketCodec.create(OutgoingEvents.HelloComposer).send(connection);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        var connection = ctx.channel().attr(CONNECTION_KEY).get();
        connection.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientCodec msg) throws Exception {
        var connection = ctx.channel().attr(CONNECTION_KEY).get();
        connection.getMessageHandler().handleMessage(msg);
    }
}