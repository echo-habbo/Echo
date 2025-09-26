package net.h4bbo.echo.api.event.types.player;

import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.game.player.IPlayer;

public class PlayerClickRegisterEvent extends ICancellableEvent {
    private final IPlayer player;

    public PlayerClickRegisterEvent(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return player;
    }
}
