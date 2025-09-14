package net.h4bbo.echo.api.event.game.connection;

import net.h4bbo.echo.api.event.CancellableEvent;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ClientConnectedEvent extends CancellableEvent {
    private IConnectionSession session;

    public ClientConnectedEvent(IConnectionSession session) {
        this.session = session;
    }

    public IConnectionSession getSession() {
        return session;
    }
}
