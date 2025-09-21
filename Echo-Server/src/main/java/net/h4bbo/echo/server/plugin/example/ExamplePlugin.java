package net.h4bbo.echo.server.plugin.example;

import io.netty.buffer.Unpooled;
import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.types.client.*;
import net.h4bbo.echo.api.plugin.DependsOnAttribute;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.common.network.codecs.PacketCodec;
import net.h4bbo.echo.server.plugin.example.handshake.GenerateKeyMessageEvent;
import net.h4bbo.echo.server.plugin.example.handshake.GetSessionParamsMessageEvent;
import net.h4bbo.echo.server.plugin.example.handshake.InitCryptoMessageEvent;
import org.oldskooler.simplelogger4j.SimpleLog;

// Example plugin implementation
@DependsOnAttribute({"CorePlugin", "DatabasePlugin"})
public class ExamplePlugin extends JavaPlugin {
    public static String Key;
    public static RC4 rc4;

    @Override
    public void load() {
        System.out.println("ExamplePlugin loaded!");

        this.getEventManager().register(this, this);
    }

    @EventHandler
    public void onClientConnected(ClientConnectedEvent event) {
        event.getSession().getMessageHandler().register(this, InitCryptoMessageEvent.class);
        event.getSession().getMessageHandler().register(this, GenerateKeyMessageEvent.class);
        event.getSession().getMessageHandler().register(this, GetSessionParamsMessageEvent.class);

        SimpleLog.of(ExamplePlugin.class).success("Client {} connected", event.getSession().getChannel().remoteAddress().toString());
    }

    @EventHandler
    public void onClientDisconnected(ClientDisconnectedEvent event) {

    }

    @EventHandler
    public void onClientReceivedMessage(ClientReceivedMessageEvent event) {

    }

    @EventHandler
    public void onClientReceivedDataEvent(ClientReceivedDataEvent event) {
        if (rc4 == null)
            return;

        byte[] message = new byte[event.getBuffer().readableBytes()];
        event.getBuffer().readBytes(message);

        var deciphered = rc4.decipher(new String(message, PacketCodec.getProtocolEncoding()));
        var buffer = Unpooled.buffer(deciphered.length());

        buffer.writeBytes(deciphered.getBytes(PacketCodec.getProtocolEncoding()));
        event.setBuffer(buffer);

        SimpleLog.of(ExamplePlugin.class).success("Client decipher: {}", deciphered);
    }

    @Override
    public void unload() {
        System.out.println("ExamplePlugin unloaded!");
        // Cleanup resources, unregister services, etc.
    }
}