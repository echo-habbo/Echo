package net.h4bbo.echo.server.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import net.h4bbo.echo.common.messages.headers.OutgoingEvents;
import net.h4bbo.echo.common.network.codecs.ClientCodec;
import net.h4bbo.echo.common.network.codecs.PacketCodec;
import net.h4bbo.echo.server.network.session.ConnectionSession;
import net.h4bbo.echo.server.plugin.ExamplePlugin;
import org.oldskooler.simplelogger4j.SimpleLog;

public class GameNetworkHandler extends SimpleChannelInboundHandler<ClientCodec> {
    public static final SimpleLog log = SimpleLog.of(GameNetworkHandler.class);
    public static final AttributeKey<ConnectionSession> CONNECTION_KEY = AttributeKey.newInstance("CONNECTION_KEY");

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        var connection = new ConnectionSession(ctx.channel());

        ctx.channel().attr(CONNECTION_KEY).setIfAbsent(connection);

        PacketCodec.create(OutgoingEvents.HelloComposer).send(connection);

        log.info("Client connected to server: {}", connection.getIpAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        var connection = ctx.channel().attr(CONNECTION_KEY).get();

        try {
            connection.close();
        } catch (Exception ex) {
            log.info("Exception when disconnected from server: {}", connection.getIpAddress(), ex);
        } finally {
            log.info("Client disconnected from server: {}", connection.getIpAddress());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientCodec msg) throws Exception {
        var connection = ctx.channel().attr(CONNECTION_KEY).get();
        connection.getMessageHandler().handleMessage(msg);
    }
}