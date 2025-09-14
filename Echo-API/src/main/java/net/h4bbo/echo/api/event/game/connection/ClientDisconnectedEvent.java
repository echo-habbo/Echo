package net.h4bbo.echo.api.event.game.connection;

import net.h4bbo.echo.api.event.IEvent;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ClientDisconnectedEvent implements IEvent {
    private final IConnectionSession session;

    public ClientDisconnectedEvent(IConnectionSession session) {
        this.session = session;
    }

    public IConnectionSession getSession() {
        return session;
    }
}
