package net.h4bbo.echo.api.event.types.client;

import io.netty.buffer.ByteBuf;
import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ClientReceivedDataEvent extends ICancellableEvent {
    private IConnectionSession session;
    private ByteBuf buffer;

    public ClientReceivedDataEvent(IConnectionSession session, ByteBuf buffer) {
        this.session = session;
        this.buffer = buffer;
    }

    public IConnectionSession getSession() {
        return session;
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }
}
