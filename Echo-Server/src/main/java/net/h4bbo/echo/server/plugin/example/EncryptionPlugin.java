package net.h4bbo.echo.server.plugin.example;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelId;
import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.types.client.*;
import net.h4bbo.echo.api.plugin.DependsOnAttribute;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.common.network.codecs.PacketCodec;
import net.h4bbo.echo.server.plugin.example.handshake.InitCryptoMessageEvent;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Example plugin implementation
@DependsOnAttribute({"CorePlugin", "DatabasePlugin"})
public class EncryptionPlugin extends JavaPlugin {
    private SimpleLog logger;
    private final Map<ChannelId, RC4Holder> encryptionHolders
            = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "EncryptionPlugin";
    }

    @Override
    public void load() {
        this.logger = SimpleLog.of(EncryptionPlugin.class);
        this.logger.info("ExamplePlugin loaded!");

        this.getEventManager().register(this, this);
    }

    @Override
    public void unload() {
        this.encryptionHolders.clear();
        this.logger.info("ExamplePlugin unloaded!");
    }

    @EventHandler
    public void onClientConnected(ClientConnectedEvent event) {
        event.getSession().getMessageHandler().register(this, InitCryptoMessageEvent.class);
        this.logger.debug("[EncryptionPlugin] Client {} connected", event.getSession().getChannel().remoteAddress().toString());
    }

    @EventHandler
    public void onClientDisconnected(ClientDisconnectedEvent event) {

    }

    @EventHandler
    public void onClientReceivedDataEvent(ClientReceivedDataEvent event) {
        if (!this.encryptionHolders.containsKey(event.getSession().getChannel().id()))
            return;

        var rc4Holder = this.encryptionHolders.get(event.getSession().getChannel().id());

        if (!rc4Holder.isEncryptionReady()) {
            return;
        }

        byte[] message = new byte[event.getBuffer().readableBytes()];
        event.getBuffer().readBytes(message);

        var deciphered = rc4Holder.rc4.decipher(new String(message, PacketCodec.getProtocolEncoding()));
        var buffer = Unpooled.buffer(deciphered.length());

        buffer.writeBytes(deciphered.getBytes(PacketCodec.getProtocolEncoding()));
        event.setBuffer(buffer);

        this.logger.debug("[EncryptionPlugin] Client decipher: {}", deciphered);
    }

    public Map<ChannelId, RC4Holder> getEncryptionHolders() {
        return encryptionHolders;
    }
}