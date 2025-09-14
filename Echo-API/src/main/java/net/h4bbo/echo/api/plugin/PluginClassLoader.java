package net.h4bbo.echo.api.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PluginClassLoader extends URLClassLoader {
    private final String pluginName;

    public PluginClassLoader(URL[] urls, ClassLoader parent, String pluginName) {
        super(urls, parent);
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}