package net.h4bbo.echo.api.network.session;

import io.netty.channel.Channel;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.IMessageHandler;

public interface IConnectionSession extends IConnectionSend {
    /**
     * Gets the underlying network channel associated with this session.
     */
    Channel getChannel();

    /**
     * Gets the player object associated with this connection.
     */
    IPlayer getPlayer();

    /**
     * Gets the remote IP address of the client if available; otherwise returns "unknown".
     */
    String getIpAddress();

    /**
     * Gets a value indicating whether this session has been disconnected.
     */
    boolean isDisconnected();

    /**
     * Gets the message handler associated with this session. 
     * Used to register, deregister, and dispatch message events for the player.
     */
    IMessageHandler getMessageHandler();

    /**
     * Closes the underlying network channel asynchronously.
     */
    void close();

    /**
     * Marks the session as disconnected and attempts to notify the associated player.
     */
    void disconnect();
}
