package net.h4bbo.echo.plugin.handshake.messages.handshake;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.plugin.handshake.HandshakePlugin;
import net.h4bbo.echo.plugin.handshake.messages.login.GetAvailableSetsMessageEvent;
import net.h4bbo.echo.plugin.handshake.messages.login.GetDateMessageEvent;
import net.h4bbo.echo.plugin.handshake.messages.login.GetSessionParamsMessageEvent;
import net.h4bbo.echo.plugin.handshake.messages.login.LoginMessageEvent;
import net.h4bbo.echo.codecs.PacketCodec;

public class GenerateKeyMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 202;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        var encryptionPlugin = (HandshakePlugin) this.getPlugin();

        if (encryptionPlugin == null) {
            player.getConnection().close();
            return;
        }

        // Not needed after login
        player.getConnection().getMessageHandler().deregister(encryptionPlugin, GenerateKeyMessageEvent.class);

        // Needed after login
        player.getConnection().getMessageHandler().register(encryptionPlugin, GetSessionParamsMessageEvent.class);
        player.getConnection().getMessageHandler().register(encryptionPlugin, GetDateMessageEvent.class);
        player.getConnection().getMessageHandler().register(encryptionPlugin, GetAvailableSetsMessageEvent.class);
        player.getConnection().getMessageHandler().register(encryptionPlugin, LoginMessageEvent.class);

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