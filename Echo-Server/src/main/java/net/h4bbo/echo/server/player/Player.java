package net.h4bbo.echo.server.player;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.h4bbo.echo.api.event.types.player.PlayerDisconnectEvent;
import net.h4bbo.echo.api.game.entity.EntityType;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.game.room.entities.RoomEntity;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.connection.IConnectionSession;
import net.h4bbo.echo.server.Echo;
import net.h4bbo.echo.server.network.connection.ConnectionSession;

public class Player implements IPlayer {
    private final IConnectionSession connection;
    private boolean isAuthenticated;
    private RoomEntity roomEntity;

    public Player(ConnectionSession connectionSession) {
        this.connection = connectionSession;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean flag) {
        this.isAuthenticated = flag;
    }

    @Override
    public IConnectionSession getConnection() {
        return connection;
    }

    @Override
    public void disconnect() {
        if (this.isAuthenticated) {
            this.isAuthenticated = false;
            Echo.getEventManager().publish(new PlayerDisconnectEvent(this));
        }
    }

    @Override
    public void send(IPacketCodec composer) {
        this.connection.send(composer);
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.connection.getChannel().attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.connection.getChannel().hasAttr(key);
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public RoomEntity getRoomEntity() {
        return this.attr(RoomEntity.DATA_KEY).get();
    }
}