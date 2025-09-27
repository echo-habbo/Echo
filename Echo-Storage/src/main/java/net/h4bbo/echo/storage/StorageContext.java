package net.h4bbo.echo.storage;

import org.oldskooler.entity4j.DbContext;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class StorageContext extends DbContext {
    public StorageContext(Connection connection) throws SQLException {
        super(connection);
    }
}
