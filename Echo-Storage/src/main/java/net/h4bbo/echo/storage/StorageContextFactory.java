package net.h4bbo.echo.storage;

import net.h4bbo.echo.storage.models.navigator.RoomData;
import net.h4bbo.echo.storage.models.user.UserData;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StorageContextFactory {
    private static SimpleLog logger = SimpleLog.of(StorageContextFactory.class);
    private static boolean isSeeded;

    public static StorageContext getStorage() throws SQLException {
        var ctx = new StorageContext(TryGetConnection());

        if (!isSeeded) {
            isSeeded = true;
            StorageSeeder.init(ctx);
        }

        /*
        var t = ctx.from(RoomData.class).as("r")
                .select(s -> s
                        .all(RoomData.class)
                        .col(UserData.class, UserData::getName).as("owner_name"))
                .leftJoin(UserData.class, "u", on ->
                        on.eq(RoomData::getOwnerId, UserData::getId));

        System.out.println(t.toList());
         */

        return ctx;
    }

    private static Connection TryGetConnection() throws SQLException {
        String mode = System.getenv("ECHO_DB_MODE");
        boolean isDev = mode != null && mode.equalsIgnoreCase("DEVELOPMENT");

        File dbFile = new File("echo-server.db");

        if (isDev && !isSeeded) {
            // Delete existing database if we are in development mode
            if (dbFile.exists()) {
                if (!dbFile.delete()) {
                    logger.warn("[Failed to delete existing echo-server.db");
                } else {
                    logger.debug("Development mode: existing echo-server.db deleted.");
                }
            }
        }

        return DriverManager.getConnection("jdbc:sqlite:echo-server.db");
    }
}
