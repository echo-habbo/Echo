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

public class UserFlatsMessageEvent extends MessageEvent {
    @Override
    public int getHeaderId() {
        return 150;
    }

    @Override
    public void handle(IPlayer player, IClientCodec msg) throws SQLException {
        var playerData = player.attr(UserData.DATA_KEY).get();

    }
}