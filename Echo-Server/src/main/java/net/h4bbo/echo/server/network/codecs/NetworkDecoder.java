package net.h4bbo.echo.server.network.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.event.types.client.ConnectionReceivedDataEvent;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.storage.codecs.ClientCodec;
import net.h4bbo.echo.common.util.specialised.Base64Encoding;
import net.h4bbo.echo.server.network.GameNetworkHandler;

import java.util.List;

public class NetworkDecoder extends ByteToMessageDecoder {
    private final IEventManager eventManager;
    private final IPluginManager pluginManager;

    public NetworkDecoder(IEventManager eventManager, IPluginManager pluginManager) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> output) throws Exception {
        var event = new ConnectionReceivedDataEvent(ctx.channel().attr(GameNetworkHandler.CONNECTION_KEY).get(), buffer);
        var isCancelled = this.eventManager.publish(event);

        if (isCancelled) {
            return;
        }

        buffer = event.getBuffer();

        // Ignore incoming data if it's less than 5 bytes
        if (buffer.readableBytes() < 5) {
            return;
        }

        while (buffer.readableBytes() >= 5) {
            buffer.markReaderIndex();

            int length = Base64Encoding.decodeInt32(new byte[]{buffer.readByte(), buffer.readByte(), buffer.readByte()});

            if (buffer.readableBytes() < length) {
                buffer.resetReaderIndex();
                return;
            }

            if (length < 0) {
                return;
            }

            output.add(new ClientCodec(buffer.readBytes(length)));
        }
    }
}