package net.h4bbo.echo.api.services.room;

import net.h4bbo.echo.storage.models.room.RoomData;

import java.util.List;

public interface IRoomService {
    List<RoomData> getRoomsByCategory(int categoryId);

    List<RoomData> getRoomsByUserId(int userId);
}
