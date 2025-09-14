package net.h4bbo.echo.server.messages;

import net.h4bbo.echo.api.messages.IMessageEvent;
import net.h4bbo.echo.api.messages.IMessageHandler;
import net.h4bbo.echo.api.network.codecs.IClientCodec;
import net.h4bbo.echo.api.network.session.IConnectionSession;
import net.h4bbo.echo.api.plugin.IPlugin;
import net.h4bbo.echo.common.network.codecs.ClientCodec;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

public class MessageHandler implements IMessageHandler {
    private final IConnectionSession connectionSession;
    private final ConcurrentHashMap<Integer, List<IMessageEvent>> events = new ConcurrentHashMap<>();
    private final SimpleLog log;

    public MessageHandler(IConnectionSession connectionSession) {
        this.log = SimpleLog.of(MessageHandler.class);
        this.connectionSession = connectionSession;
    }

    public void register(IPlugin plugin, int headerId, IMessageEvent handler) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(handler);

        List<IMessageEvent> list = events.computeIfAbsent(headerId, k -> new ArrayList<>());
        list.add(handler);

        log.debug("Registered handler {} for header {}", handler.getClass().getSimpleName(), headerId);
    }

    // Registers a handler by type. The handler must implement IMessageEvent and expose a public HeaderId property.
    public <THandler extends IMessageEvent> void register(IPlugin plugin, Class<THandler> handlerClass) {
        try {
            THandler instance = handlerClass.getDeclaredConstructor().newInstance();
            int headerId = instance.getHeaderId();
            register(plugin, headerId, instance);
        } catch (Exception e) {
            log.error("Failed to register handler", e);
        }
    }

    // Deregisters all handlers of the specified type.
    public <THandler extends IMessageEvent> int deregister(IPlugin plugin, Class<THandler> handlerClass) {
        int headerId;
        try {
            THandler instance = handlerClass.getDeclaredConstructor().newInstance();
            headerId = instance.getHeaderId();
        } catch (Exception e) {
            log.error("Failed to deregister handler", e);
            return 0;
        }
        List<IMessageEvent> list = events.get(headerId);
        if (list != null) {
                int originalSize = list.size();
                list.removeIf(h -> h.getClass() == handlerClass);
                int removed = originalSize - list.size();
                if (list.isEmpty()) {
                    events.remove(headerId);
                }
                if (removed > 0) {
                    log.debug("Deregistered {} handler(s) of type {} for header {}", removed, handlerClass.getSimpleName(), headerId);
                }
                return removed;
        }
        return 0;
    }

    // Dispatches an incoming message to all handlers registered for its header identifier.
    public void handleMessage(IClientCodec packet) {
        try {
            List<IMessageEvent> handlers = events.get(packet.getHeaderId());
            if (handlers == null || handlers.isEmpty()) {
                log.debug("Unknown: [{}] {} / {}", packet.getHeaderId(), packet.getHeader(), packet.getMessageBody());
                packet.getBuffer().release();
                return;
            }

            log.debug("RECEIVED {}: [{} / {}] / {}", handlers.get(0).getClass().getSimpleName(), packet.getHeader(), packet.getHeaderId(), packet.getMessageBody());

            List<IMessageEvent> snapshot = new ArrayList<>(handlers);

            if (snapshot.size() == 1) {
                snapshot.getFirst().handle(connectionSession.getPlayer(), packet);
                packet.getBuffer().release();
                return;
            }

            for (IMessageEvent handler : snapshot) {
                var copy = packet.getBuffer().copy();
                try {
                    handler.handle(connectionSession.getPlayer(), new ClientCodec(copy));
                } finally {
                    copy.release();
                }
            }
            packet.getBuffer().release();
        } catch (Exception ex) {
            log.error("Error occurred in MessageHandler", ex);
            try {
                if (packet != null && packet.getBuffer() != null)
                    packet.getBuffer().release();
            } catch (Exception ignore) { }
        }
    }
}
