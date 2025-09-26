package net.h4bbo.echo.storage;

import net.h4bbo.echo.api.plugin.DependsOn;
import net.h4bbo.echo.api.plugin.JavaPlugin;

public class TestPlugin extends JavaPlugin {
    @Override
    public void load() {
        this.getLogger().info("Hello, World!");
    }

    @Override
    public void unload() {

    }
}
