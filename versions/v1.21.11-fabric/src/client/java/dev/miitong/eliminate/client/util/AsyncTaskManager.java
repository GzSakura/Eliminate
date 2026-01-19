package dev.miitong.eliminate.client.util;

import java.util.function.Supplier;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTaskManager {
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final ThreadPoolExecutor executor;

    static {
        int corePoolSize = getOptimalThreadPoolSize();
        int maxPoolSize = corePoolSize * 2;
        
        executor = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "Eliminate-Async-Worker-" + threadNumber.getAndIncrement());
                    t.setDaemon(true);
                    t.setPriority(Thread.MIN_PRIORITY);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.allowCoreThreadTimeOut(true);
    }

    /**
     * Get optimal thread pool size based on configuration and available processors
     */
    private static int getOptimalThreadPoolSize() {
        try {
            // Try to get thread pool size from config
            dev.miitong.eliminate.config.EliminateConfig config = dev.miitong.eliminate.config.EliminateConfig.getInstance();
            if (config != null && config.threadPoolSize > 0) {
                return config.threadPoolSize;
            }
        } catch (Exception e) {
            // If config is not available, use default
        }
        
        // Default: use available processors
        return Runtime.getRuntime().availableProcessors();
    }

    public static <T> CompletableFuture<T> submit(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }

    public static void submit(Runnable task) {
        executor.execute(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }

    public static int getActiveCount() {
        return executor.getActiveCount();
    }

    public static int getQueueSize() {
        return executor.getQueue().size();
    }
}
