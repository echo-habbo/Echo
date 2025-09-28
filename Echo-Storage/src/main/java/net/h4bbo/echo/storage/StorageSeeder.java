package net.h4bbo.echo.storage;

import net.h4bbo.echo.storage.models.navigator.NavigatorCategoryData;
import net.h4bbo.echo.storage.models.room.RoomData;
import net.h4bbo.echo.storage.models.user.UserData;
import org.oldskooler.simplelogger4j.SimpleLog;

public class StorageSeeder {
    private static final SimpleLog logger = SimpleLog.of(StorageSeeder.class);

    public static void init(StorageContext storageContext) {
        try {
            seedUsers(storageContext);
            seedNavigatorCategories(storageContext);
            seedRooms(storageContext);
        } catch (Exception ex) {
            logger.error("Error occurred when seeding:", ex);
        }
    }

    private static void seedRooms(StorageContext storageContext) {
        long roomCount = 0;

        try {
            roomCount = storageContext.from(RoomData.class).count();
        } catch (Exception ignored) { }

        if (roomCount > 0) {
            logger.debug("Rooms table has already been seeded");
        } else {
            storageContext.createTable(RoomData.class);

            var room = new RoomData();
            room.setName("Welcome Lounge");
            room.setDescription("welcome_lounge");
            room.setModel("newbie_lobby");
            room.setCcts("hh_room_nlobby");
            room.setVisitorsNow(0);
            room.setVisitorsMax(25);
            room.setCategoryId(3);
            storageContext.insert(room);


            room = new RoomData();
            room.setName("Cafe Ole");
            room.setDescription("cafe_ole");
            room.setModel("cafe_ole");
            room.setCcts("hh_room_cafe");
            room.setCategoryId(3);
            storageContext.insert(room);

            room = new RoomData();
            room.setName("Habbo Lido");
            room.setDescription("habbo_lido");
            room.setModel("pool_a");
            room.setCcts("hh_room_pool,hh_people_pool");
            room.setVisitorsNow(0);
            room.setVisitorsMax(25);
            room.setCategoryId(5);
            storageContext.insert(room);

            room = new RoomData();
            room.setName("Alex's Room");
            room.setDescription("random room description");
            room.setModel("model_b");
            room.setVisitorsNow(0);
            room.setVisitorsMax(25);
            room.setCategoryId(4);
            room.setOwnerId(1);
            storageContext.insert(room);

            room = new RoomData();
            room.setName("Another Testing Room");
            room.setDescription("so i herd u liek mudkipz");
            room.setModel("model_a");
            room.setVisitorsNow(0);
            room.setVisitorsMax(25);
            room.setCategoryId(7);
            room.setOwnerId(1);
            storageContext.insert(room);

            logger.debug("Rooms table has been seeded");
        }
    }

    private static void seedNavigatorCategories(StorageContext storageContext) {
        long categoryCount = 0;

        try {
            categoryCount = storageContext.from(NavigatorCategoryData.class).count();
        } catch (Exception ignored) { }

        if (categoryCount > 0) {
            logger.debug("Navigator categories table has already been seeded");
        } else {
            storageContext.createTable(NavigatorCategoryData.class);

            var category = new NavigatorCategoryData();
            category.setName("Public Rooms");
            category.setId(3);
            category.setOrderId(0);
            storageContext.insert(category);

            category = new NavigatorCategoryData();
            category.setName("Guest Rooms");
            category.setId(4);
            category.setOrderId(0);
            storageContext.insert(category);

            category = new NavigatorCategoryData();
            category.setName("Swimming Pools");
            category.setId(5);
            category.setOrderId(0);
            category.setParentId(3);
            storageContext.insert(category);

            category = new NavigatorCategoryData();
            category.setName("Test");
            category.setId(6);
            category.setOrderId(0);
            category.setParentId(5);
            storageContext.insert(category);


            category = new NavigatorCategoryData();
            category.setName("Chill & Chat Rooms");
            category.setId(7);
            category.setParentId(4);
            category.setOrderId(0);
            storageContext.insert(category);

            logger.debug("Navigator categories table has been seeded");
        }
    }

    private static void seedUsers(StorageContext storageContext) {
        long userCount = 0;

        try {
            userCount = storageContext.from(UserData.class).count();
        } catch (Exception ignored) { }

        if (userCount > 0) {
            logger.debug("User table has already been seeded");
        } else {
            storageContext.createTable(UserData.class);

            var admin = new UserData();
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
