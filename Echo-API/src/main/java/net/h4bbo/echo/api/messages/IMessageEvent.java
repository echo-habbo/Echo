package net.h4bbo.echo.api.messages;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.network.codecs.IClientCodec;

public interface IMessageEvent {
    int getHeaderId();

    void handle(IPlayer player, IClientCodec msg);
}
