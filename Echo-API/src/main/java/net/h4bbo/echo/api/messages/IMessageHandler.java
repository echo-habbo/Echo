package net.h4bbo.echo.api.messages;

import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.plugin.IPlugin;

public interface IMessageHandler {

    void register(IPlugin plugin, int headerId, IMessageEvent handler);

    <THandler extends IMessageEvent> void register(IPlugin plugin, Class<THandler> handlerClass);

    <THandler extends IMessageEvent> int deregister(IPlugin plugin, Class<THandler> handlerClass);

    void handleMessage(IClientCodec packet);
}
