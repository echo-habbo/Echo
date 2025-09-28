package net.h4bbo.echo.plugin.handshake.services;

import net.h4bbo.echo.api.services.user.IUserService;
import net.h4bbo.echo.plugin.handshake.HandshakePlugin;
import net.h4bbo.echo.storage.StorageContextFactory;
import net.h4bbo.echo.storage.models.user.UserData;

import java.sql.SQLException;
import java.util.Optional;

public class UserService implements IUserService {
    private HandshakePlugin plugin;

    public UserService(HandshakePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Optional<UserData> getUserAuthenticate(String username, String password) {
        try (var storage = StorageContextFactory.getStorage()) {
            return storage.from(UserData.class)
                    .filter(f ->
                            f.equals(UserData::getName, username).equals(UserData::getPassword, password))
                    .first();
        } catch (SQLException e) {
            this.plugin.getLogger().error("Error loading navigator categories: ", e);
        }

        return Optional.empty();
    }
}