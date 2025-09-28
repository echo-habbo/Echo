package net.h4bbo.echo.plugin.room;

import net.h4bbo.echo.api.plugin.DependsOn;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.api.services.navigator.INavigatorService;
import net.h4bbo.echo.api.services.room.IRoomService;
import net.h4bbo.echo.plugin.room.services.RoomService;
import org.oldskooler.inject4j.ServiceCollection;

@DependsOn({"HandshakePlugin"})
public class RoomPlugin extends JavaPlugin {
    private RoomManager roomManager;

    @Override
    public void assignServices(ServiceCollection services) {
        services.addSingleton(IRoomService.class, RoomService.class);
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
}