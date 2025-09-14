package net.h4bbo.echo.api.network.codecs;

import io.netty.buffer.ByteBuf;

public interface IClientCodec extends AutoCloseable {
    String getHeader();
    int getHeaderId();
    ByteBuf getBuffer();

    String getMessageBody();
    byte[] getReadableBytes();
    String getContent();

    <T> T pop(DataCodec codec, Class<T> type);
    Object get(DataCodec codec);

    @Override
    void close(); // implementors can throw Exception if needed
}

