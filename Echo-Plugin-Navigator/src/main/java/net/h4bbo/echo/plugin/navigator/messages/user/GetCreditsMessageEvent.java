package net.h4bbo.echo.plugin.navigator.messages.user;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.storage.StorageContextFactory;
import net.h4bbo.echo.codecs.PacketCodec;
import net.h4bbo.echo.storage.models.user.User;

public class GetCreditsMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 8;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        try (var db = StorageContextFactory.getStorage()) {
            var user = db.from(User.class)
                    .filter(f -> f.equals(User::getName, "Alex"))
                    .toList().get(0);

            PacketCodec.create(6)
                    .append(DataCodec.BYTES, user.getCredits())
                    .send(player);

        } catch (Exception e) {
            this.getLogger().error("Error occurred when selecting user: ", e);
        }
    }
}