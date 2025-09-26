package net.h4bbo.echo.server.plugin.example;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelId;
import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.types.client.*;
import net.h4bbo.echo.api.network.codecs.ProtocolCodec;
import net.h4bbo.echo.api.plugin.DependsOn;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.server.plugin.example.handshake.InitCryptoMessageEvent;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Example plugin implementation
@DependsOn({"CorePlugin", "DatabasePlugin"})
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
        this.logger = SimpleLog.of("EncryptionPlugin");
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
        var rc4Holder = new RC4Holder(
                event.getConnection()
        );

        this.encryptionHolders.putIfAbsent(
                event.getConnection().getChannel().id(),
                rc4Holder
        );

        event.getConnection().getMessageHandler().register(this, InitCryptoMessageEvent.class);
        this.logger.info("[EncryptionPlugin] Client {} connected", event.getConnection().getChannel().remoteAddress().toString());
    }

    @EventHandler
    public void onClientDisconnected(ClientDisconnectedEvent event) {
        this.encryptionHolders.remove(
                event.getConnection().getChannel().id()
        );

        this.logger.info("[EncryptionPlugin] Client {} disconnected", event.getConnection().getChannel().remoteAddress().toString());
    }

    @EventHandler
    public void onConnectionReceivedDataEvent(ConnectionReceivedDataEvent event) {
        if (!this.encryptionHolders.containsKey(event.getConnection().getChannel().id()))
            return;

        var rc4Holder = this.encryptionHolders.get(event.getConnection().getChannel().id());

        if (!rc4Holder.isEncryptionReady()) {
            return;
        }

        byte[] message = new byte[event.getBuffer().readableBytes()];
        event.getBuffer().readBytes(message);

        var deciphered = rc4Holder.rc4.decipher(message);
        var buffer = Unpooled.buffer(deciphered.length);

        buffer.writeBytes(deciphered);
        event.setBuffer(buffer);

        this.logger.debug("[EncryptionPlugin] Client decipher: {}", new String(message, ProtocolCodec.getEncoding()));
    }

    public Map<ChannelId, RC4Holder> getEncryptionHolders() {
        return encryptionHolders;
    }
}