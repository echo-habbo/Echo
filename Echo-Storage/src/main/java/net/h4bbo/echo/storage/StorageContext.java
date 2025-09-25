package net.h4bbo.echo.storage;

import org.oldskooler.entity4j.DbContext;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class StorageContext extends DbContext {
    private static boolean isSeeded = false;
    private static SimpleLog logger = SimpleLog.of(StorageContext.class);

    public StorageContext() throws SQLException {
        super(Objects.requireNonNull(TryGetConnection()));

        if (!isSeeded) {
            isSeeded = true;
            StorageSeeder.init(this);
        }
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
