package net.h4bbo.echo.plugin.room.services;

import net.h4bbo.echo.api.services.room.IRoomService;
import net.h4bbo.echo.plugin.room.RoomPlugin;
import net.h4bbo.echo.storage.StorageContextFactory;
import net.h4bbo.echo.storage.models.room.RoomData;
import net.h4bbo.echo.storage.models.user.UserData;
import org.oldskooler.entity4j.Query;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

public class RoomService implements IRoomService {
    public RoomPlugin plugin;

    public RoomService(RoomPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<RoomData> getRoomsByCategory(int categoryId) {
        return this.getRooms(x -> x.equals(RoomData::getCategoryId, categoryId));
    }

    @Override
    public List<RoomData> getRoomsByUserId(int userId) {
        return this.getRooms(x -> x.equals(RoomData::getOwnerId, userId));
    }

    public List<RoomData> getRooms(Function<Query.Filters<RoomData>, Query.Filters<RoomData>> predicate) {
        try (var ctx = StorageContextFactory.getStorage()) {
            return ctx.from(RoomData.class).as("r")
                    .select(s -> s
                            .all(RoomData.class)
                            .col(UserData.class, UserData::getName).as("owner_name"))
                    .leftJoin(UserData.class, "u", on ->
                            on.eq(RoomData::getOwnerId, UserData::getId))
                    .filter(predicate)
                    .toList();
        } catch (SQLException e) {
            this.plugin.getLogger().error("Error loading navigator categories: ", e);
        }

        return null;
    }
}


