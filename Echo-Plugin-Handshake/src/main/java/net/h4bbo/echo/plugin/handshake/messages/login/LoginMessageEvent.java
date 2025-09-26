package net.h4bbo.echo.plugin.handshake.messages.login;

import net.h4bbo.echo.api.event.types.player.PlayerLoginEvent;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.storage.codecs.PacketCodec;

public class LoginMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 4;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) {
        var isCancelled = this.getEventManager().publish(new PlayerLoginEvent(player));

        if (isCancelled) {
            player.getConnection().close();
            return;
        }

        player.setAuthenticated(true);

        PacketCodec.create(2)
                .send(player);

        PacketCodec.create(3)
                .send(player);

        // Register login classes
        // player.getConnection().getMessageHandler().register(this.getPlugin(), UserInfoMessageEvent.class);
    }
}
