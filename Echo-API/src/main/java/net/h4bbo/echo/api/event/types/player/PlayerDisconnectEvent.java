package net.h4bbo.echo.api.event.types.player;

import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.event.types.IEvent;
import net.h4bbo.echo.api.game.player.IPlayer;

public class PlayerDisconnectEvent extends IEvent {
    private final IPlayer player;

    public PlayerDisconnectEvent(IPlayer player) {
        this.player = player;
    }

    public IPlayer getPlayer() {
        return player;
    }
}
