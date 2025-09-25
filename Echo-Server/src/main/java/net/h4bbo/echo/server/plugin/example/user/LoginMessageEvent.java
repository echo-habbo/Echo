package net.h4bbo.echo.server.plugin.example.user;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.server.plugin.example.handshake.GenerateKeyMessageEvent;
import net.h4bbo.echo.storage.codecs.PacketCodec;

public class LoginMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 4;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        PacketCodec.create(2)
                .send(player);

        PacketCodec.create(3)
                .send(player);

        // Register login classes
        player.getConnection().getMessageHandler().register(this.getPlugin(), UserInfoMessageEvent.class);
    }
}
