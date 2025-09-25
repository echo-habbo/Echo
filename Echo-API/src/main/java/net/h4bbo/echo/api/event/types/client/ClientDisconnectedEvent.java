package net.h4bbo.echo.api.event.types.client;

import net.h4bbo.echo.api.event.types.IEvent;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ClientDisconnectedEvent extends IEvent {
    private final IConnectionSession session;

    public ClientDisconnectedEvent(IConnectionSession session) {
        this.session = session;
    }

    public IConnectionSession getConnection() {
        return session;
    }
}
