package net.h4bbo.echo.api.event.types.client;

import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ClientReceivedMessageEvent extends ICancellableEvent {
    private IConnectionSession session;
    private IClientCodec codec;

    public ClientReceivedMessageEvent(IConnectionSession session, IClientCodec codec) {
        this.session = session;
        this.codec = codec;
    }

    public IConnectionSession getSession() {
        return session;
    }

    public IClientCodec getMessage() {
        return codec;
    }
}
