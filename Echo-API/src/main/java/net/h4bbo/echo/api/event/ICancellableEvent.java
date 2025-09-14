package net.h4bbo.echo.api.event;

public interface ICancellableEvent extends IEvent {
    boolean isCancelled();
}
