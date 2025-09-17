package net.h4bbo.echo.server.plugin.events;

import net.h4bbo.echo.api.event.EventPriority;
import net.h4bbo.echo.api.event.types.IEvent;
import net.h4bbo.echo.api.plugin.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class RegisteredHandler {
    public final JavaPlugin plugin;
    public final Object target;
    public final Method method; // For attribute-based
    public final Consumer<IEvent> consumer; // For lambda-style
    public final Class<?> eventType;
    public final EventPriority priority;
    public final boolean ignoreCancelled;
    public final boolean once;

    public RegisteredHandler(JavaPlugin plugin, Object target, Method method, Consumer<IEvent> consumer, Class<?> eventType, EventPriority priority, boolean ignoreCancelled, boolean once) {
        this.plugin = plugin;
        this.target = target;
        this.method = method;
        this.consumer = consumer;
        this.eventType = eventType;
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.once = once;
    }

    void invoke(IEvent event) throws InvocationTargetException, IllegalAccessException {
        if (method != null) {
            method.setAccessible(true);
            method.invoke(target, event);
        } else if (consumer != null) {
            consumer.accept(event);
        }
    }
}