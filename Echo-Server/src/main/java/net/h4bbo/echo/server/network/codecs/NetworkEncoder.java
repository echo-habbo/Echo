package net.h4bbo.echo.server.network.codecs;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;

import java.util.List;

public class NetworkEncoder extends MessageToMessageEncoder<IPacketCodec> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IPacketCodec composer, List<Object> output) throws Exception {
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

        output.add(buffer);
    }
}