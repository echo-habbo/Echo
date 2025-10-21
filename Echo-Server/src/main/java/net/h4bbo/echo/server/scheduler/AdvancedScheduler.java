package net.h4bbo.echo.server.scheduler;

import net.h4bbo.echo.api.IAdvancedScheduler;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class AdvancedScheduler implements IAdvancedScheduler {
    private final ScheduledExecutorService scheduler;

    public AdvancedScheduler(int poolSize) {
        this.scheduler = Executors.newScheduledThreadPool(poolSize);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return scheduler.schedule(task, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    @Override
    public <T> CompletableFuture<T> scheduleAsync(Supplier<T> supplier, long delay, TimeUnit unit) {
        CompletableFuture<T> future = new CompletableFuture<>();
        scheduler.schedule(() -> {
            try {
                T result = supplier.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, delay, unit);
        return future;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task) {
        return scheduler.schedule(task, 0, TimeUnit.MICROSECONDS);
    }

    @Override
    public <T> CompletableFuture<T> scheduleAsync(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        scheduler.schedule(() -> {
            try {
                T result = supplier.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, 0, TimeUnit.MICROSECONDS);
        return future;
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
