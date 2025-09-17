package net.h4bbo.echo.server.plugin;

import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.game.connection.ClientConnectedEvent;
import net.h4bbo.echo.api.plugin.DependsOnAttribute;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import org.oldskooler.simplelogger4j.SimpleLog;

// Example plugin implementation
@DependsOnAttribute({"CorePlugin", "DatabasePlugin"})
public class ExamplePlugin extends JavaPlugin {

    @Override
    public void load() {
        System.out.println("ExamplePlugin loaded!");
        this.getEventManager().register(this, this);
    }

    @EventHandler
    public void onClientConnected(ClientConnectedEvent event) {
        SimpleLog.of(ExamplePlugin.class).success("Client {} connected", event.getSession().getChannel().remoteAddress().toString());
    }

    @Override
    public void unload() {
        System.out.println("ExamplePlugin unloaded!");
        // Cleanup resources, unregister services, etc.
    }
}