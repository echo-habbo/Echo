package net.h4bbo.echo.plugin.handshake.messages.login;

import net.h4bbo.echo.api.event.types.player.PlayerClickRegisterEvent;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.codecs.PacketCodec;
import net.h4bbo.echo.storage.models.user.UserData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GetDateMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 49;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        var date = LocalDate.now().format(formatter);

        PacketCodec.create(163)
                .append(DataCodec.BYTES, date)
                .send(player);

        if (!player.hasAttr(UserData.DATA_KEY)) {
            this.getEventManager().publish(new PlayerClickRegisterEvent(player));
        }
    }
}