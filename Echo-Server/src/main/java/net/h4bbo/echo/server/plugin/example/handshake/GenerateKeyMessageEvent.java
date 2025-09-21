package net.h4bbo.echo.server.plugin.example.handshake;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.common.network.codecs.PacketCodec;
import net.h4bbo.echo.server.plugin.example.ExamplePlugin;
import net.h4bbo.echo.server.plugin.example.KeyGenerator;
import net.h4bbo.echo.server.plugin.example.RC4;

public class GenerateKeyMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 202;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        ExamplePlugin.Key = KeyGenerator.generateKey();

        PacketCodec.create(1)
                .append(DataCodec.BYTES, ExamplePlugin.Key)
                .send(player);

        ExamplePlugin.rc4 = new RC4(ExamplePlugin.Key);

        // Not needed after handshake
        player.getConnection().getMessageHandler().deregister(null, GenerateKeyMessageEvent.class);
    }
}