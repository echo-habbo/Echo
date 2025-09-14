package net.h4bbo.echo.server.game.player;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.network.session.IConnectionSession;
import net.h4bbo.echo.server.network.session.ConnectionSession;

import java.util.concurrent.CompletableFuture;

public class Player implements IPlayer {
    private IConnectionSession connection;

    public Player(ConnectionSession connectionSession) {
        this.connection = connectionSession;
    }

    @Override
    public IConnectionSession getConnection() {
        return connection;
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        return null;
    }
}