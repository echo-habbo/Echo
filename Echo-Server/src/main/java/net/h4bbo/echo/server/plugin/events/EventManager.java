package net.h4bbo.echo.server.plugin.events;

import net.h4bbo.echo.api.event.*;
import net.h4bbo.echo.api.event.types.ICancellableEvent;
import net.h4bbo.echo.api.event.types.IEvent;
import net.h4bbo.echo.api.plugin.IPlugin;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventManager implements IEventManager {

    public Map<Class<?>, List<RegisteredHandler>> getHandlers() {
        return handlers;
    }

    private final Map<Class<?>, List<RegisteredHandler>> handlers = new ConcurrentHashMap<>();

    @Override
    public void register(IPlugin plugin, Object listener) {
        if (listener == null) throw new IllegalArgumentException("listener is null");
        Method[] methods = listener.getClass().getDeclaredMethods();

        for (Method m : methods) {
            if (m.isAnnotationPresent(EventHandler.class)) {
                EventHandler attr = m.getAnnotation(EventHandler.class);
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1 || !IEvent.class.isAssignableFrom(params[0])) {
                    throw new IllegalStateException(
                            m.getDeclaringClass().getName() + "." + m.getName() +
                                    " must take exactly one IEvent parameter."
                    );
                }
                RegisteredHandler rh = new RegisteredHandler(
                        plugin,
                        listener,
                        m,
                        null,                // Not lambda
                        params[0],
                        attr.priority(),
                        attr.ignoreCancelled(),
                        false                // once
                );
                addHandler(rh);
            }
        }
    }

    /**
     * Remove all handlers belonging to a given listener instance.
     */
    @Override
    public void unregister(Object listener) {
        if (listener == null) return;

        var it = handlers.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Class<?>, List<RegisteredHandler>> entry = it.next();
            entry.getValue().removeIf(h -> h.target == listener);
            if (entry.getValue().isEmpty()) it.remove();
        }
    }

    /**
     * Fluent subscription without annotations/reflection.
     */
    public <TEvent extends IEvent> AutoCloseable subscribe(
            IPlugin plugin,
            Consumer<TEvent> handler,
            Class<TEvent> eventType,
            EventPriority priority,
            boolean ignoreCancelled,
            boolean once
    ) {
        if (handler == null) throw new IllegalArgumentException("handler is null");
        @SuppressWarnings("unchecked")
        Consumer<IEvent> consumer = (Consumer<IEvent>) handler;
        RegisteredHandler rh = new RegisteredHandler(
                plugin,
                handler, // for lambda, target is the consumer itself
                null,
                consumer,
                eventType,
                priority,
                ignoreCancelled,
                once
        );

        addHandler(rh);

        return new Subscription(this, rh);
    }

    /**
     * Publish/dispatch an event to all matching handlers.
     * Returns true if the event was cancelled, false otherwise.
     */
    @Override
    public boolean publish(IEvent ev) {
        if (ev == null) throw new IllegalArgumentException("event is null");

        List<RegisteredHandler> snapshot = resolveHandlersFor(ev.getClass());
        snapshot.sort(Comparator.comparingInt(a -> a.priority.ordinal()));

        boolean isCancellable = ev instanceof ICancellableEvent;
        List<RegisteredHandler> firedOnce = new ArrayList<>();

        for (RegisteredHandler h : snapshot) {
            // Only skip if cancelled, ignoreCancelled is true, and NOT MONITOR
            if (isCancellable) {
                if (((ICancellableEvent) ev).isCancelled() && h.ignoreCancelled && h.priority != EventPriority.MONITOR)
                    continue;
            }
            boolean invoked = false;
            try {
                h.invoke(ev);
                invoked = true;
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (invoked && h.once)
                    firedOnce.add(h);
            }
        }

        // Prune one-shot handlers that fired
        if (!firedOnce.isEmpty()) {
            for (RegisteredHandler h : firedOnce) {
                List<RegisteredHandler> list = handlers.get(h.eventType);
                if (list != null) {
                    list.remove(h);
                    if (list.isEmpty()) handlers.remove(h.eventType);
                }
            }
        }

        return isCancellable && ((ICancellableEvent) ev).isCancelled();
    }


    private void addHandler(RegisteredHandler handler) {
        List<RegisteredHandler> list = handlers.computeIfAbsent(handler.eventType, k -> new ArrayList<>());
        list.add(handler);
    }

    private List<RegisteredHandler> resolveHandlersFor(Class<?> concreteEventType) {
        List<RegisteredHandler> result = new ArrayList<>();
        // exact type
        List<RegisteredHandler> list = handlers.get(concreteEventType);
        if (list != null) result.addAll(list);

        // base types
        Class<?> t = concreteEventType.getSuperclass();
        while (t != null && IEvent.class.isAssignableFrom(t)) {
            List<RegisteredHandler> baseList = handlers.get(t);
            if (baseList != null) result.addAll(baseList);
            t = t.getSuperclass();
        }

        // interfaces
        for (Class<?> iface : concreteEventType.getInterfaces()) {
            if (IEvent.class.isAssignableFrom(iface)) {
                List<RegisteredHandler> ifaceList = handlers.get(iface);
                if (ifaceList != null) result.addAll(ifaceList);
            }
        }
        return result;
    }
}
