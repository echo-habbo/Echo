package net.h4bbo.echo.server.plugin.example.handshake;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.IMessageHandler;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.common.network.codecs.PacketCodec;

public class GetSessionParamsMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 181;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        PacketCodec.create(257)
                .append(DataCodec.VL64_INT, 1)
                .append(DataCodec.BOOL, false)
                .append(DataCodec.VL64_INT, 2)
                .append(DataCodec.BOOL, false)
                .append(DataCodec.VL64_INT, 3)
                .append(DataCodec.BOOL, false)
                .append(DataCodec.VL64_INT, 4)
                .append(DataCodec.BOOL, false)
                .append(DataCodec.VL64_INT, 5)
                .append(DataCodec.STRING, "dd-MM-yyyy")
                .append(DataCodec.VL64_INT, 6)
                .append(DataCodec.BOOL, false)
                .append(DataCodec.VL64_INT, 7)
                .append(DataCodec.BOOL, false)
                .append(DataCodec.VL64_INT, 8)
                .append(DataCodec.STRING, "")
                .append(DataCodec.VL64_INT, 9)
                .append(DataCodec.BOOL, true) // Wether tutorial is enabled or not
                .send(player);

        // Not needed after handshake
        player.getConnection().getMessageHandler().deregister(null, GetSessionParamsMessageEvent.class);
    }
}
