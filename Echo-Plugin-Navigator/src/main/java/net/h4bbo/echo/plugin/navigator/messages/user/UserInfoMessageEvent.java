package net.h4bbo.echo.plugin.navigator.messages.user;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.codecs.PacketCodec;
import net.h4bbo.echo.storage.models.user.UserData;

public class UserInfoMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 7;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        var user = player.attr(UserData.DATA_KEY).get();

        PacketCodec.create(5)
                .append(DataCodec.STRING, user.getId())
                .append(DataCodec.STRING, user.getName())
                .append(DataCodec.STRING, user.getFigure())
                .append(DataCodec.STRING, user.getSex())
                .append(DataCodec.STRING, user.getMotto())
                .append(DataCodec.VL64_INT, user.getTickets())
                .append(DataCodec.STRING, user.getFigurePool())
                .append(DataCodec.VL64_INT, user.getFilm())
                .append(DataCodec.BOOL, user.isDirectMail())
                .send(player);
    }
}