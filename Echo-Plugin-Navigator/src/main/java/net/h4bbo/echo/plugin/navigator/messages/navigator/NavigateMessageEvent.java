package net.h4bbo.echo.plugin.navigator.messages.navigator;

import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.MessageEvent;
import net.h4bbo.echo.api.network.codecs.DataCodec;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.codecs.PacketCodec;
import net.h4bbo.echo.plugin.navigator.NavigatorPlugin;
import net.h4bbo.echo.storage.StorageContextFactory;
import net.h4bbo.echo.storage.models.navigator.RoomData;
import net.h4bbo.echo.storage.models.user.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NavigateMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 150;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) throws SQLException {
        var playerData = player.attr(UserData.DATA_KEY).get();
        var navigatorPlugin = (NavigatorPlugin) this.getPlugin();

        boolean hideFulLRooms = msg.pop(DataCodec.BOOL, Boolean.class);
        int categoryId = msg.pop(DataCodec.VL64_INT, Integer.class);

        var navigatorCategoryOpt = navigatorPlugin.getNavigatorCategories().stream()
                .filter(x -> x.getId() == categoryId)
                .findFirst();

        if (navigatorCategoryOpt.isEmpty()) {
            return;
        }

        var navigatorCategory = navigatorCategoryOpt.get();

        if (navigatorCategory.getRankId() > playerData.getRank()) {
            return;
        }

        List<RoomData> roomList = new ArrayList<>();

        try (var ctx = StorageContextFactory.getStorage()) {
            roomList = ctx.from(RoomData.class).as("r")
                    .select(s -> s
                            .all(RoomData.class)
                            .col(UserData.class, UserData::getName).as("owner_name"))
                    .leftJoin(UserData.class, "u", on ->
                            on.eq(RoomData::getOwnerId, UserData::getId))
                    .filter(x -> x.equals(RoomData::getCategoryId, categoryId))
                    .toList();
        } catch (SQLException e) {
            this.getLogger().error("Error loading navigator categories: ", e);
        }

        var isPublicRoomCategory = navigatorPlugin.isPublicRoomCategory(navigatorCategory);

        var codec = PacketCodec.create(220)
                .append(DataCodec.BOOL, hideFulLRooms)
                .append(DataCodec.VL64_INT, navigatorCategory.getId())
                .append(DataCodec.VL64_INT, isPublicRoomCategory ? 0 : 2)
                .append(DataCodec.STRING, navigatorCategory.getName())
                .append(DataCodec.VL64_INT, 0) // TODO: Current visitors
                .append(DataCodec.VL64_INT, 25) // TODO: Max visitors
                .append(DataCodec.VL64_INT, navigatorCategory.getParentId());

        if (!isPublicRoomCategory) {
            codec = codec.append(DataCodec.VL64_INT, roomList.size());
        }

        for (var room : roomList) {
            if (isPublicRoomCategory) {
                int door = 0;
                String description = room.getDescription();

                if (room.getDescription().contains("/")) {
                    String[] data = description.split("/");
                    description = data[0];
                    door = Integer.parseInt(data[1]);
                }

                codec = codec
                        .append(DataCodec.VL64_INT, room.getId())
                        .append(DataCodec.VL64_INT, 1)
                        .append(DataCodec.STRING, room.getName())
                        .append(DataCodec.VL64_INT, room.getVisitorsNow())
                        .append(DataCodec.VL64_INT, room.getVisitorsMax())
                        .append(DataCodec.VL64_INT, room.getCategoryId())
                        .append(DataCodec.STRING, description)
                        .append(DataCodec.VL64_INT, room.getId())
                        .append(DataCodec.VL64_INT, door)
                        .append(DataCodec.STRING, room.getCcts() == null ? "" : room.getCcts())
                        .append(DataCodec.VL64_INT, 0)
                        .append(DataCodec.VL64_INT, 1);
            } else {
                codec = codec
                        .append(DataCodec.VL64_INT, room.getId())
                        .append(DataCodec.STRING, room.getName())
                        .append(DataCodec.STRING, room.getOwnerName())
                        .append(DataCodec.STRING, "open")
                        .append(DataCodec.VL64_INT, room.getVisitorsNow())
                        .append(DataCodec.VL64_INT, room.getVisitorsMax())
                        .append(DataCodec.STRING, room.getDescription());
            }
        }

        var subCategories = navigatorPlugin.getNavigatorCategories().stream()
                .filter(x -> x.getParentId() == navigatorCategory.getId() &&
                        playerData.getRank() >= x.getRankId())
                .toList();

        for (var category : subCategories) {
            codec = codec
                    .append(DataCodec.VL64_INT, category.getId())
                    .append(DataCodec.VL64_INT, 0)
                    .append(DataCodec.STRING, category.getName())
                    .append(DataCodec.VL64_INT, 0) // TODO: Current visitors
                    .append(DataCodec.VL64_INT, 25) // TODO: Max visitors
                    .append(DataCodec.VL64_INT, navigatorCategory.getId());
        }

        codec.send(player);
    }
}