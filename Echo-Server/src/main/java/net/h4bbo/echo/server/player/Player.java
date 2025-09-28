package net.h4bbo.echo.server.player;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import net.h4bbo.echo.api.commands.CommandSender;
import net.h4bbo.echo.api.event.types.player.PlayerDisconnectEvent;
import net.h4bbo.echo.api.event.types.player.PlayerLoginEvent;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.session.IConnectionSend;
import net.h4bbo.echo.api.network.session.IConnectionSession;
import net.h4bbo.echo.server.Echo;
import net.h4bbo.echo.server.network.session.ConnectionSession;

import java.util.concurrent.CompletableFuture;

public class Player implements IPlayer {
    private final IConnectionSession connection;
    private boolean isAuthenticated;

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
    public CompletableFuture<Void> disconnect() {
        if (this.isAuthenticated) {
            this.isAuthenticated = false;
            Echo.getEventManager().publish(new PlayerDisconnectEvent(this));
        }

        return null;
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
}