package net.h4bbo.echo.api.event.types;

public abstract class ICancellableEvent extends IEvent {
    private boolean isCancelled;

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
