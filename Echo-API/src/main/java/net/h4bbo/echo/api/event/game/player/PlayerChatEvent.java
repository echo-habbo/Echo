package net.h4bbo.echo.api.event.game.player;

import net.h4bbo.echo.api.event.types.ICancellableEvent;

public class PlayerChatEvent extends ICancellableEvent {
    private final String playerName;
    private String message;

    public PlayerChatEvent(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
