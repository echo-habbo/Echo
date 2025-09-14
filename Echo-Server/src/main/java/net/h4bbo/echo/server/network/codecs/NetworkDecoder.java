package net.h4bbo.echo.server.network.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.h4bbo.echo.common.network.codecs.ClientCodec;
import net.h4bbo.echo.common.util.specialised.Base64Encoding;

import java.util.List;

public class NetworkDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> output) throws Exception {
        // Ignore incoming data if it's less than 5 bytes
        if (buffer.readableBytes() < 5) {
            return;
        }

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