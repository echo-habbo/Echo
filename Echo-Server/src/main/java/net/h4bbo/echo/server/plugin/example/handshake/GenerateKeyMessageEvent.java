package net.h4bbo.echo.server.plugin.example.handshake;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.common.network.codecs.PacketCodec;
import net.h4bbo.echo.server.plugin.example.EncryptionPlugin;

public class GenerateKeyMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 202;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        var encryptionPlugin = (EncryptionPlugin) this.getPlugin();

        if (encryptionPlugin == null) {
            player.getConnection().close();
            return;
        }

        // Not needed after handshake
        player.getConnection().getMessageHandler().deregister(encryptionPlugin, GenerateKeyMessageEvent.class);

        // Needed after handshake
        player.getConnection().getMessageHandler().register(encryptionPlugin, GetSessionParamsMessageEvent.class);
        player.getConnection().getMessageHandler().register(encryptionPlugin, GetDateMessageEvent.class);
        player.getConnection().getMessageHandler().register(encryptionPlugin, GetAvailableSetsMessageEvent.class);

        var rc4Holder = encryptionPlugin
                .getEncryptionHolders()
                .get(player.getConnection().getChannel().id());

        if (rc4Holder == null) {
            player.getConnection().close();
            return;
        }

        PacketCodec.create(1)
                .append(DataCodec.BYTES, rc4Holder.key)
                .send(player);

        rc4Holder.setEncryptionReady(true);
    }
}