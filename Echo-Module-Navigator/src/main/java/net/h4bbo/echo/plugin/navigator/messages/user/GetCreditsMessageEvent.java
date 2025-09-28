package net.h4bbo.echo.plugin.navigator.messages.user;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.codecs.PacketCodec;
import net.h4bbo.echo.storage.models.user.UserData;

public class GetCreditsMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 8;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        var user = player.attr(UserData.DATA_KEY).get();

        PacketCodec.create(6)
                .append(DataCodec.BYTES, user.getCredits())
                .send(player);
    }
}