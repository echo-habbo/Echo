package net.h4bbo.echo.api.event.types.client;

import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ClientConnectedEvent extends ICancellableEvent {
    private IConnectionSession session;

    public ClientConnectedEvent(IConnectionSession session) {
        this.session = session;
    }

    public IConnectionSession getConnection() {
        return session;
    }
}
