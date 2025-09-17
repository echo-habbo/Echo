package net.h4bbo.echo.server.network;

import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.common.network.codecs.PacketCodec;

public class EventMsg extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 206;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        PacketCodec.create(139)
            .append(DataCodec.BYTES, "HII")
            .send(player);
    }
}
