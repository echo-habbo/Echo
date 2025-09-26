package net.h4bbo.echo.api.messages;

import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.plugin.JavaPlugin;

public interface IMessageHandler {
    void register(JavaPlugin plugin, int headerId, MessageEvent handler);

    <THandler extends MessageEvent> void register(JavaPlugin plugin, Class<THandler> handlerClass);

    <THandler extends MessageEvent> int deregister(JavaPlugin plugin, Class<THandler> handlerClass);

    void handleMessage(IClientCodec packet);
}
