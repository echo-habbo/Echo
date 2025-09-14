package net.h4bbo.echo.api.network.codecs;

import net.h4bbo.echo.api.network.session.IConnectionSend;

public interface IPacketCodec {
    int getHeaderId();
    byte[] getBuffer();
    int getLength();
    boolean isComposed();

    IPacketCodec append(DataCodec codec, Object value);

    void send(IConnectionSend target);
    void sendAll(IConnectionSend... targets);
}
