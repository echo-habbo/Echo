package net.h4bbo.echo.server.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.event.types.client.ClientConnectedEvent;
import net.h4bbo.echo.api.event.types.client.ClientDisconnectedEvent;
import net.h4bbo.echo.api.event.types.client.ConnectionMessageEvent;
import net.h4bbo.echo.api.network.session.IConnectionSession;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.common.messages.headers.OutgoingEvents;
import net.h4bbo.echo.codecs.ClientCodec;
import net.h4bbo.echo.codecs.PacketCodec;
import net.h4bbo.echo.server.network.session.ConnectionSession;
import org.oldskooler.simplelogger4j.SimpleLog;

public class GameNetworkHandler extends SimpleChannelInboundHandler<ClientCodec> {
    public static final SimpleLog log = SimpleLog.of(GameNetworkHandler.class);
    public static final AttributeKey<IConnectionSession> CONNECTION_KEY = AttributeKey.newInstance("CONNECTION_KEY");
    private final IEventManager eventManager;
    private final IPluginManager pluginManager;

    public GameNetworkHandler(IEventManager eventManager, IPluginManager pluginManager) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        var connection = new ConnectionSession(ctx.channel(), this.eventManager, this.pluginManager);

        ctx.channel().attr(CONNECTION_KEY).setIfAbsent(connection);

        var isCancelled = this.eventManager.publish(new ClientConnectedEvent(connection));

        if (isCancelled) {
            return;
        }

        PacketCodec
            .create(OutgoingEvents.HelloComposer)
            .send(connection);

        log.info("Client connected to server: {}", connection.getIpAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        var connection = ctx.channel().attr(CONNECTION_KEY).get();

        var isCancelled = this.eventManager.publish(new ClientDisconnectedEvent(connection));

        if (isCancelled) {
            return;
        }

        try {
            connection.getPlayer().disconnect();
        } catch (Exception ex) {
            log.info("Exception when disconnected from server: {}", connection.getIpAddress(), ex);
        } finally {
            log.info("Client disconnected from server: {}", connection.getIpAddress());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientCodec msg) throws Exception {
        var connection = ctx.channel().attr(CONNECTION_KEY).get();

        var isCancelled = this.eventManager.publish(new ConnectionMessageEvent(connection, msg));

        if (isCancelled) {
            return;
        }

        connection.getMessageHandler().handleMessage(msg);
    }
}