package net.h4bbo.echo.server.plugin.example.handshake;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.common.network.codecs.PacketCodec;

public class InitCryptoMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 206;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        PacketCodec.create(277)
            .append(DataCodec.VL64_INT, 0)
            .send(player);

        // Not needed after handshake
        player.getConnection().getMessageHandler().deregister(null, InitCryptoMessageEvent.class);
    }
}
