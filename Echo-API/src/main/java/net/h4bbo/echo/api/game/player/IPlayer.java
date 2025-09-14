package net.h4bbo.echo.api.game.player;


import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.session.IConnectionSend;
import net.h4bbo.echo.api.network.session.IConnectionSession;

import java.util.concurrent.CompletableFuture;

/**
 * Defines the contract for a player within the game server.
 */
public interface IPlayer extends IConnectionSend {
    IConnectionSession getConnection();

    /**
     * Disconnects the player from the server.
     */
    CompletableFuture<Void> disconnect();
}
