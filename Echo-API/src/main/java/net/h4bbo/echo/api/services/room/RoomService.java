package net.h4bbo.echo.api.services.room;

import net.h4bbo.echo.storage.models.navigator.NavigatorCategoryData;
import net.h4bbo.echo.storage.models.navigator.RoomData;

import java.util.List;

public interface RoomService {
    public List<RoomData> getRoomsByCatgeory(int categoryId);
}
