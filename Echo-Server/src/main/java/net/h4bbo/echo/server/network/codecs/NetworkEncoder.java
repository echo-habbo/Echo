package net.h4bbo.echo.server.network.codecs;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.event.types.client.ClientReceivedDataEvent;
import net.h4bbo.echo.api.event.types.client.ServerSendDataEvent;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.server.network.GameNetworkHandler;

import java.util.List;

public class NetworkEncoder extends MessageToMessageEncoder<IPacketCodec> {
    private final IEventManager eventManager;
    private final IPluginManager pluginManager;

    public NetworkEncoder(IEventManager eventManager, IPluginManager pluginManager) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IPacketCodec composer, List<Object> output) throws Exception {
        // ConnectionSession connection = ctx.getChannel().getAttribute(GameNetworkHandler.CONNECTION_KEY).get();
        // String debugMessage = "";

        var buffer = Unpooled.buffer();

            /*
            for (var objectData : composer.getData()) {
                // debugMessage += objectData.toString();
                buffer.writeBytes(GameServer.Encoding.getBytes(objectData));
            }*/

        buffer.writeBytes(composer.getBuffer());
        buffer.writeByte(1);

        var event = new ServerSendDataEvent(ctx.channel().attr(GameNetworkHandler.CONNECTION_KEY).get(), buffer);
        var isCancelled = this.eventManager.publish(event);

        if (isCancelled) {
            buffer.release();
            return;
        }

        buffer = event.getBuffer();

        output.add(buffer);
    }
}