package net.h4bbo.echo.server.plugin.events;

import java.util.List;

public class Subscription implements AutoCloseable {
    private final EventManager bus;
    private final RegisteredHandler handler;
    private boolean disposed = false;

    public Subscription(EventManager bus, RegisteredHandler handler) {
        this.bus = bus;
        this.handler = handler;
    }

    @Override
    public void close() {
        if (disposed) return;
        disposed = true;

        List<RegisteredHandler> list = bus.getHandlers().get(handler.eventType);
        if (list != null) {
            list.remove(handler);
            if (list.isEmpty()) bus.getHandlers().remove(handler.eventType);
        }
    }
}