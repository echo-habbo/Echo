package net.h4bbo.echo.api.messages;

import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.plugin.IPlugin;

public interface IMessageHandler {

    void register(IPlugin plugin, int headerId, MessageEvent handler);

    <THandler extends MessageEvent> void register(IPlugin plugin, Class<THandler> handlerClass);

    <THandler extends MessageEvent> int deregister(IPlugin plugin, Class<THandler> handlerClass);

    void handleMessage(IClientCodec packet);
}
