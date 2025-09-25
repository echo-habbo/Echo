package net.h4bbo.echo.api.event.types.client;

import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ConnectionMessageEvent extends ICancellableEvent {
    private final IConnectionSession session;
    private final IClientCodec message;

    public ConnectionMessageEvent(IConnectionSession session, IClientCodec message) {
        this.session = session;
        this.message = message;
    }

    public IConnectionSession getSession() {
        return session;
    }

    public IClientCodec getMessage() {
        return message;
    }
}
