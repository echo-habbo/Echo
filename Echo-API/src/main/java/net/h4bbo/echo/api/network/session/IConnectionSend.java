package net.h4bbo.echo.api.network.session;

import net.h4bbo.echo.api.network.codecs.IPacketCodec;

public interface IConnectionSend {
    /**
     * Sends a server message through the network channel.
     */
    void send(IPacketCodec composer);
}
