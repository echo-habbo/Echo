package net.h4bbo.echo.api.event.types.client;

import io.netty.buffer.ByteBuf;
import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.network.session.IConnectionSession;

public class ConnectionOutgoingDataEvent extends ICancellableEvent {
    private IConnectionSession connection;
    private ByteBuf buffer;

    public ConnectionOutgoingDataEvent(IConnectionSession session, ByteBuf buffer) {
        this.connection = session;
        this.buffer = buffer;
    }

    public IConnectionSession getConnection() {
        return connection;
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }
}
