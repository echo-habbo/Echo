package net.h4bbo.echo.plugin.navigator;

import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.types.client.ClientConnectedEvent;
import net.h4bbo.echo.api.event.types.player.PlayerDisconnectEvent;
import net.h4bbo.echo.api.event.types.player.PlayerLoginEvent;
import net.h4bbo.echo.api.plugin.DependsOn;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.plugin.navigator.messages.user.GetCreditsMessageEvent;
import net.h4bbo.echo.plugin.navigator.messages.user.UserInfoMessageEvent;

@DependsOn({"HandshakePlugin"})
public class NavigatorPlugin extends JavaPlugin {
    @Override
    public void load() {
        this.getEventManager().register(this, this);
    }

    @Override
    public void unload() {

    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        this.getLogger().info("{} has logged in!", event.getPlayer());

        var messageHandler = event.getPlayer().getConnection().getMessageHandler();

        messageHandler.register(this, UserInfoMessageEvent.class);
        messageHandler.register(this, GetCreditsMessageEvent.class);
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        this.getLogger().info("{} has disconnected!", event.getPlayer());
    }
}