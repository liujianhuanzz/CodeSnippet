package cn.hhspace.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/5/11 11:58
 * @Descriptions: 线程池管理工具，优雅停止
 */
public class ExecutorUtils {
        /**
     * Gracefully shutdown the given {@link ExecutorService}. The call waits the given timeout that
     * all ExecutorServices terminate. If the ExecutorServices do not terminate in this time, they
     * will be shut down hard.
     *
     * @param timeout to wait for the termination of all ExecutorServices
     * @param unit of the timeout
     * @param executorServices to shut down
     */
    public static void gracefulShutdown(
            long timeout, TimeUnit unit, ExecutorService... executorServices) {
        for (ExecutorService executorService : executorServices) {
            executorService.shutdown();
        }

        boolean wasInterrupted = false;
        final long endTime = unit.toMillis(timeout) + System.currentTimeMillis();
        long timeLeft = unit.toMillis(timeout);
        boolean hasTimeLeft = timeLeft > 0L;

        for (ExecutorService executorService : executorServices) {
            if (wasInterrupted || !hasTimeLeft) {
                executorService.shutdownNow();
            } else {
                try {
                    if (!executorService.awaitTermination(timeLeft, TimeUnit.MILLISECONDS)) {
                        System.out.println(
                                "ExecutorService did not terminate in time. Shutting it down now.");
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    System.out.println(
                            "Interrupted while shutting down executor services. Shutting all "
                                    + "remaining ExecutorServices down now.");
                    executorService.shutdownNow();

                    wasInterrupted = true;

                    Thread.currentThread().interrupt();
                }

                timeLeft = endTime - System.currentTimeMillis();
                hasTimeLeft = timeLeft > 0L;
            }
        }
    }

    /**
     * Shuts the given {@link ExecutorService} down in a non-blocking fashion. The shut down will be
     * executed by a thread from the common fork-join pool.
     *
     * <p>The executor services will be shut down gracefully for the given timeout period.
     * Afterwards {@link ExecutorService#shutdownNow()} will be called.
     *
     * @param timeout before {@link ExecutorService#shutdownNow()} is called
     * @param unit time unit of the timeout
     * @param executorServices to shut down
     * @return Future which is completed once the {@link ExecutorService} are shut down
     */
    public static CompletableFuture<Void> nonBlockingShutdown(
            long timeout, TimeUnit unit, ExecutorService... executorServices) {
        return CompletableFuture.supplyAsync(
                () -> {
                    gracefulShutdown(timeout, unit, executorServices);
                    return null;
                });
    }
}
