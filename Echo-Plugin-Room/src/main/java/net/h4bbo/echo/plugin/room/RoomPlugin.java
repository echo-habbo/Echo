package net.h4bbo.echo.plugin.room;

import net.h4bbo.echo.api.plugin.DependsOn;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.api.services.navigator.INavigatorService;
import org.oldskooler.inject4j.ServiceCollection;

@DependsOn({"HandshakePlugin"})
public class RoomPlugin extends JavaPlugin {
    private RoomManager roomManager;

    @Override
    public void assignServices(ServiceCollection services) {

    }

    @Override
    public void load() {
        this.roomManager = new RoomManager();
    }

    @Override
    public void unload() {

    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    /*
    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {

    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        this.getLogger().info("{} has disconnected!", event.getPlayer());
    }*/
}