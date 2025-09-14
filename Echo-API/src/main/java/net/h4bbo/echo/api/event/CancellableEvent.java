package net.h4bbo.echo.api.event;

public abstract class CancellableEvent implements ICancellableEvent {
    private boolean isCancelled = false;

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
