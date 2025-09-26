package net.h4bbo.echo.plugin.handshake;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelId;
import net.h4bbo.echo.api.event.EventHandler;
import net.h4bbo.echo.api.event.types.client.*;
import net.h4bbo.echo.api.network.codecs.ProtocolCodec;
import net.h4bbo.echo.api.plugin.JavaPlugin;
import net.h4bbo.echo.plugin.handshake.encryption.RC4Holder;
import net.h4bbo.echo.plugin.handshake.messages.handshake.InitCryptoMessageEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandshakePlugin extends JavaPlugin {
    private final Map<ChannelId, RC4Holder> encryptionHolders
            = new ConcurrentHashMap<>();

    @Override
    public void load() {
        this.getEventManager().register(this, this);
    }

    @Override
    public void unload() {
        this.encryptionHolders.clear();
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
    }

    @EventHandler
    public void onClientDisconnected(ClientDisconnectedEvent event) {
        this.encryptionHolders.remove(
                event.getConnection().getChannel().id()
        );

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
    }

    public Map<ChannelId, RC4Holder> getEncryptionHolders() {
        return encryptionHolders;
    }
}