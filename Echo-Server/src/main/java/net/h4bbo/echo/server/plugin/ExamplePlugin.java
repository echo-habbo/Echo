package net.h4bbo.echo.server.plugin;

import net.h4bbo.echo.api.plugin.DependsOnAttribute;
import net.h4bbo.echo.api.plugin.IPlugin;

// Example plugin implementation
@DependsOnAttribute({"CorePlugin", "DatabasePlugin"})
public class ExamplePlugin implements IPlugin {

    @Override
    public void load() {
        System.out.println("ExamplePlugin loaded!");
        // Initialize resources, register services, etc.
    }

    @Override
    public void unload() {
        System.out.println("ExamplePlugin unloaded!");
        // Cleanup resources, unregister services, etc.
    }

    @Override
    public String getName() {
        return "ExamplePlugin";
    }

    @Override
    public String getVersion() {
        return "1.2.0";
    }
}