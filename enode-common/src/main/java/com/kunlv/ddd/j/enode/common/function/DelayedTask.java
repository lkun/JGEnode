package com.kunlv.ddd.j.enode.common.function;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kunlv.ddd.j.enode.common.exception.ENodeRuntimeException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lvk618@gmail.com
 */
public class DelayedTask {
    private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(
            1, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("DelayedThread-%d").build());

    public static <T> CompletableFuture<T> startDelayedTaskFuture(Duration duration, Func<T> action) {
        CompletableFuture<T> future = new CompletableFuture<>();
        EXECUTOR.schedule(() -> future.complete(action.apply()), duration.toMillis(), TimeUnit.MILLISECONDS);
        return future;
    }

    public static void startDelayedTask(Duration duration, Action action) {
        DelayedTask.EXECUTOR.schedule(() -> {
            try {
                action.apply();
            } catch (InterruptedException e) {
                throw new ENodeRuntimeException(e);
            }
        }, duration.toMillis(), TimeUnit.MILLISECONDS);
    }
}
