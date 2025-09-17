package net.h4bbo.echo.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.h4bbo.echo.api.event.IEventManager;
import net.h4bbo.echo.api.plugin.IPluginManager;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer  {
    final private static int BACK_LOG = 20;
    final private static int BUFFER_SIZE = 2048;

    private final SimpleLog log;

    private String ip;
    private int port;

    private DefaultChannelGroup channels;
    private ServerBootstrap bootstrap;
    private AtomicInteger connectionIds;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private final IEventManager eventManager;
    private final IPluginManager pluginManager;

    public GameServer(String ip, int port, IEventManager eventManager, IPluginManager pluginManager) {
        this.eventManager = eventManager;
        this.pluginManager = pluginManager;
        this.ip = ip;
        this.port = port;
        this.log = SimpleLog.of(GameServer.class);
    }

    /**
     * Create the Netty sockets.
     */
    public void createSocket() {
        int threads = Runtime.getRuntime().availableProcessors();
        this.bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        this.workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.bootstrap = new ServerBootstrap();
        this.connectionIds = new AtomicInteger(0);

        this.bootstrap.group(bossGroup, workerGroup)
                .channel((Epoll.isAvailable()) ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new GameChannelInitializer(this.eventManager, this.pluginManager))
                .option(ChannelOption.SO_BACKLOG, BACK_LOG)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
                .childOption(ChannelOption.RECVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(BUFFER_SIZE))
                .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
    }

    /**
     * Bind the server to its address that's been specified
     *
     * @return
     */
    public boolean bind() throws InterruptedException {
        ChannelFuture future = this.bootstrap.bind(new InetSocketAddress(this.getIp(), this.getPort()));
        boolean completed = future.await(5, TimeUnit.SECONDS); // Wait up to 5 seconds

        if (!completed) {
            log.error("Bind operation timed out for {}:{}", this.getIp(), this.getPort());
            return false;
        }
        if (future.isSuccess()) {
            log.info("Server is listening on {}:{}", this.getIp(), this.getPort());
        } else {
            Throwable cause = future.cause();
            if (cause instanceof BindException) {
                log.error("Port {} is already in use!", this.getPort());
            } else {
                log.error("Failed to start server on {}:{}: {}", this.getIp(), this.getPort(), cause.getMessage(), cause);
            }
            log.error("Please double check there's no programs using the same port, and you have set the correct IP address to listen on.", this.getIp(), this.getPort());
        }
        return future.isSuccess();
    }


    /**
     * Get the IP of this server.
     *
     * @return the server ip
     */
    private String getIp() {
        return ip;
    }

    /**
     * Get the port of this server.
     *
     * @return the port
     */
    private Integer getPort() {
        return port;
    }

    /**
     * Get default channel group of channels
     * @return channels
     */
    public DefaultChannelGroup getChannels() {
        return channels;
    }

    /**
     * Get handler for connection ids.
     *
     * @return the atomic int instance
     */
    public AtomicInteger getConnectionIds() {
        return connectionIds;
    }
}