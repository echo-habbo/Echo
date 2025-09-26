package net.h4bbo.echo.plugin.navigator.messages.user;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.storage.StorageContext;
import net.h4bbo.echo.storage.StorageContextFactory;
import net.h4bbo.echo.storage.codecs.PacketCodec;
import net.h4bbo.echo.storage.models.user.User;

import java.sql.SQLException;

public class UserInfoMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 7;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        try (var db = StorageContextFactory.getStorage()) {
            var user = db.from(User.class)
                    .filter(f -> f.equals(User::getName, "Alex"))
                    .toList().get(0);

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

        } catch (Exception e) {
            this.getLogger().error("Error occurred when selecting user: ", e);
        }
    }
}