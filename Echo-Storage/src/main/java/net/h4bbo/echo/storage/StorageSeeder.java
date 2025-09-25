package net.h4bbo.echo.storage;

import net.h4bbo.echo.storage.models.user.User;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.sql.SQLException;

public class StorageSeeder {
    private static final SimpleLog logger = SimpleLog.of(StorageSeeder.class);

    public static void init(StorageContext storageContext) {
        try {
            seedUsers(storageContext);
        } catch (Exception ex) {
            logger.error("Error occurred when seeding:", ex);
        }
    }

    private static void seedUsers(StorageContext storageContext) {
        long userCount = 0;

        try {
            userCount = storageContext.from(User.class).count();
        } catch (Exception ignored) { }

        if (userCount > 0) {
            logger.debug("User table has already been seeded");
        } else {
            storageContext.createTable(User.class);

            var admin = new User();
            admin.setName("Alex");
            admin.setPassword("123");
            admin.setFigure("1000118001270012900121001");
            admin.setSex("F");
            admin.setMotto("");
            admin.setCredits(9999);
            admin.setTickets(100);
            admin.setFilm(100);
            admin.setRank(7);

            storageContext.insert(admin);

            logger.debug("User table has been seeded");
        }
    }
}
