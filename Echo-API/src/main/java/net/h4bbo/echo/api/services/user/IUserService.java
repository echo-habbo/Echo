package net.h4bbo.echo.api.services.user;

import net.h4bbo.echo.storage.models.user.UserData;

import java.util.Optional;

public interface IUserService {
    Optional<UserData> getUserAuthenticate(String username, String password);
}
