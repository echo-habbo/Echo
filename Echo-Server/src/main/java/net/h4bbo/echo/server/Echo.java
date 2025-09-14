package net.h4bbo.echo.server;

import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.plugin.IPluginManager;
import net.h4bbo.echo.server.events.EventManager;
import net.h4bbo.echo.server.network.GameServer;
import net.h4bbo.echo.server.plugin.PluginManager;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Echo {
    public static Properties configuration;
    public static IPluginManager pluginManager;
    public static IEventManager eventManager;
    public static GameServer server;

    private static final SimpleLog log;

    static {
        log = SimpleLog.of(Echo.class);
        configuration = new Properties();
        pluginManager = new PluginManager("plugins");
        eventManager = new EventManager();
        server = new GameServer();
    }

    public static void boot() {
        log.info("Starting server for " + System.getProperty("user.name") + "...");
        log.info("Checking for server.conf");

        ensureDefaultConfig("server.conf");

        try {
            configuration.load(new FileReader("server.conf"));
        } catch (IOException e) {
            log.error("Could not load server.conf: " + e.getMessage());
            return;
        }

        log.success("server.conf found");

        try {
            if (!tryDatabaseConnection()) {
                return;
            }
            if (!tryConfigureServices()) {
                return;
            }
            if (!tryConfigureGame()) {
                return;
            }
            if (!tryCreateServer()) {
                return;
            }
        } catch (Exception ex) {
            log.error("An exception occurred when booting Echo: " + ex.getMessage(), ex);
        }
    }

    /**
     * If config file doesn't exist, create it with sensible defaults
     */
    private static void ensureDefaultConfig(String configFileName) {
        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            log.warn(configFileName + " does not exist, writing default configuration...");
            try (FileWriter writer = new FileWriter(configFile)) {
                // Default DB settings
                writer.write("db.host=localhost\n");
                writer.write("db.port=3306\n");
                writer.write("db.name=echo\n");
                writer.write("db.user=root\n");
                writer.write("db.pass=password\n");

                // Default server socket settings
                writer.write("server.host=0.0.0.0\n");
                writer.write("server.port=30001\n");

                writer.flush();
                log.success("Wrote default config to " + configFileName);
            } catch (IOException e) {
                log.error("Could not write default config: " + e.getMessage(), e);
            }
        }
    }

    private static boolean tryDatabaseConnection() {
        try {
            log.info("Attempting to load database...");


            log.success("Database is loaded successfully!");
        } catch (Exception ex) {
            log.error("An exception occurred attempting to connect to the database: " + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    private static boolean tryConfigureServices() {
        try {
            // Register pluginManager and eventManager
            // You would usually use a DI container, here just placeholders

            log.info("Loading plugins…");

            pluginManager.loadAllPlugins();

            log.success("Loaded {} plugins", pluginManager.getAllPlugins().size());
            return true;
        } catch (Exception ex) {
            log.error("An exception occurred while configuring services: " + ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean tryConfigureGame() {
        try {
            return true;
        } catch (Exception ex) {
            log.error("An exception occurred attempting to configure game: " + ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean tryCreateServer() {
        String host = configuration.getProperty("server.host", "0.0.0.0");
        int port = Integer.parseInt(configuration.getProperty("server.port", "30001"));

        server = new GameServer();
        server.set(host, port);
        server.createSocket();

        try {
            server.bind();
            return true; // Note: Should return true if successful!
        } catch (Exception e) {
            log.error("Failed to bind server: " + e.getMessage(), e);
            return false;
        }
    }

}
