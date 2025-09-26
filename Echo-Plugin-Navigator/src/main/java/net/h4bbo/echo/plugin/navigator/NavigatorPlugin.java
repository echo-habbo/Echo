package net.h4bbo.echo.plugin.navigator;

import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.types.client.ClientConnectedEvent;
import net.h4bbo.echo.api.event.types.player.PlayerDisconnectEvent;
import net.h4bbo.echo.api.event.types.player.PlayerLoginEvent;
import net.h4bbo.echo.api.plugin.DependsOn;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.plugin.navigator.messages.user.UserInfoMessageEvent;

@DependsOn({"HandshakePlugin"})
public class NavigatorPlugin extends JavaPlugin {
    @Override
    public void load() {
        this.getLogger().info("{} loaded!", this.getName());
        this.getEventManager().register(this, this);
    }

    @Override
    public void unload() {
        this.getLogger().info("{} unloaded!", this.getName());
    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        this.getLogger().info("{} has logged in!", event.getPlayer());

        event.getPlayer().getConnection().getMessageHandler()
                .register(this, UserInfoMessageEvent.class);
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        this.getLogger().info("{} has disconnected!", event.getPlayer());
    }
}