package net.h4bbo.echo.server.network.session;

import io.netty.channel.Channel;
import net.h4bbo.echo.api.game.player.IPlayer;
import net.h4bbo.echo.api.messages.IMessageHandler;
import net.h4bbo.echo.api.network.codecs.IPacketCodec;
import net.h4bbo.echo.api.network.session.IConnectionSend;
import net.h4bbo.echo.api.network.session.IConnectionSession;
import net.h4bbo.echo.server.game.player.Player;
import net.h4bbo.echo.server.messages.MessageHandler;

import java.net.InetSocketAddress;

public class ConnectionSession implements IConnectionSession, IConnectionSend {
    private final Channel channel;
    private final Player player;
    private boolean IsDisconnected = false;

    private final IMessageHandler messageHandler;

    public ConnectionSession(Channel channel) {
        if (channel == null) throw new IllegalArgumentException("channel cannot be null");
        this.channel = channel;
        this.player = new Player(this);
        this.messageHandler = new MessageHandler(this);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public IPlayer getPlayer() {
        return player;
    }

    public String getIpAddress() {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        if (remoteAddress != null) {
            return remoteAddress.getHostString();
        } else {
            return "unknown";
        }
    }

    @Override
    public void send(IPacketCodec composer) {
        if (composer == null) throw new IllegalArgumentException("composer cannot be null");
        if (!channel.isActive()) return;

        try {
            // Log.forContext("GameNetworkHandler").debug("SENT: " + composer.getHeaderId() + " / " + ProtocolCodec.Encoding.getString(composer.getBuffer().array()).toConsoleOutput());
            channel.writeAndFlush(composer);
        } catch (Exception ex) {
            // Log.forContext("GameNetworkHandler").debug(ex, "Error sending packet");
        }
    }

    @Override
    public void close() {
        this.channel.close();
    }

    @Override
    public void disconnect() {
        if (IsDisconnected) return;

        IsDisconnected = true;

        try {
            if (player != null) {
                player.disconnect().get();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public IMessageHandler getMessageHandler() {
        return messageHandler;
    }

    @Override
    public boolean isDisconnected() {
        return IsDisconnected;
    }
}