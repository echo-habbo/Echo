package net.h4bbo.echo.plugin.navigator;

import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.types.player.PlayerDisconnectEvent;
import net.h4bbo.echo.api.event.types.player.PlayerLoginEvent;
import net.h4bbo.echo.api.plugin.DependsOn;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.api.services.navigator.INavigatorService;
import net.h4bbo.echo.api.services.room.IRoomService;
import net.h4bbo.echo.plugin.navigator.messages.navigator.NavigateMessageEvent;
import net.h4bbo.echo.plugin.navigator.messages.navigator.UserFlatsMessageEvent;
import net.h4bbo.echo.plugin.navigator.messages.user.GetCreditsMessageEvent;
import net.h4bbo.echo.plugin.navigator.messages.user.UserInfoMessageEvent;
import net.h4bbo.echo.plugin.navigator.services.NavigatorService;
import net.h4bbo.echo.storage.models.navigator.NavigatorCategoryData;
import net.h4bbo.echo.storage.models.room.RoomData;
import net.h4bbo.echo.storage.models.user.UserData;
import org.oldskooler.inject4j.ServiceCollection;

import java.util.List;

@DependsOn({"HandshakePlugin", "RoomPlugin"})
public class NavigatorPlugin extends JavaPlugin {
    @Override
    public void assignServices(ServiceCollection services) {
        services.addSingleton(INavigatorService.class, NavigatorService.class);
    }

    @Override
    public void load() {
        // this.getLogger().info("Loaded {} navigator categories", this.navigatorCategories.size());
        this.getEventManager().register(this, this);
    }

    @Override
    public void unload() {

    }

    @EventHandler
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
        this.getLogger().info("{} has logged in!", event.getPlayer().attr(UserData.DATA_KEY).get().getName());

        var messageHandler = event.getPlayer().getConnection().getMessageHandler();

        messageHandler.register(this, UserInfoMessageEvent.class);
        messageHandler.register(this, GetCreditsMessageEvent.class);
        messageHandler.register(this, NavigateMessageEvent.class);
        messageHandler.register(this, UserFlatsMessageEvent.class);
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        this.getLogger().info("{} has disconnected!", event.getPlayer());
    }

    public NavigatorCategoryData getTopParentCategory(int categoryId) {
        NavigatorCategoryData current = this.getNavigatorCategories()
                .stream()
                .filter(c -> c.getId() == categoryId)
                .findFirst()
                .orElse(null);

        while (current != null && current.getParentId() != 0) {
            int parentId = current.getParentId();
            current = this.getNavigatorCategories().stream()
                    .filter(c -> c.getId() == parentId)
                    .findFirst()
                    .orElse(null);
        }
        return current; // This is the top-most parent (or null if not found)
    }

    public List<NavigatorCategoryData> getNavigatorCategories() {
        return this.getServices()
                .getRequiredService(INavigatorService.class)
                .getCategories();
    }

    public List<RoomData> getRoomsByCategory(int categoryId) {
        return this.getServices()
                .getRequiredService(IRoomService.class)
                .getRoomsByCategory(categoryId);
    }

    public List<RoomData> getRoomsByUserId(int userId) {
        return this.getServices()
                .getRequiredService(IRoomService.class)
                .getRoomsByUserId(userId);
    }

    public boolean isPublicRoomCategory(int categoryId) {
        var navigatorCategory = this.getNavigatorCategories().stream().filter(x -> x.getId() == categoryId).findFirst().orElse(null);

        if (navigatorCategory == null) {
            throw new NullPointerException("Category " + categoryId + " does not exist");
        }

        return this.getTopParentCategory(navigatorCategory.getId()).getId() == 3;
    }
}