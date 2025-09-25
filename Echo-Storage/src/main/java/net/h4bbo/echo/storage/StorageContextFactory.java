package net.h4bbo.echo.storage;

import org.oldskooler.simplelogger4j.SimpleLog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StorageContextFactory {
    private static SimpleLog logger = SimpleLog.of(StorageContextFactory.class);
    private static boolean isSeeded;

    public static StorageContext getStorage() throws SQLException {
        return new StorageContext(TryGetConnection());
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
