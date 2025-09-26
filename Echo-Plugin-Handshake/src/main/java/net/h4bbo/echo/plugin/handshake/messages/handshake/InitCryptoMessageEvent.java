package net.h4bbo.echo.plugin.handshake.messages.handshake;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.storage.codecs.PacketCodec;

public class InitCryptoMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 206;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        // Needed after login
        player.getConnection().getMessageHandler().register(this.getPlugin(), GenerateKeyMessageEvent.class);

        // Not needed after login
        player.getConnection().getMessageHandler().deregister(this.getPlugin(), InitCryptoMessageEvent.class);

        // Send crypto parameters
        PacketCodec.create(277)
                .append(DataCodec.VL64_INT, 0)
                .send(player);
    }
}
