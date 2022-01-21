package cn.hhspace.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 重试工具类
 * @Date: 2022/1/19 1:54 下午
 * @Package: cn.hhspace.utils
 */
public class RetryUtils {

    public static final long MAX_SLEEP_MILLIS = 60000;
    public static final long BASE_SLEEP_MILLIS = 1000;
    public static final int DEFAULT_MAX_TRIES = 10;

    public interface Task<T> {
        T perform() throws Exception;
    }

    public interface CleanupAfterFailure {
        // 重试失败后执行
        void cleanup();
    }

    public static <T> T retry(
            final Task<T> f,
            final Predicate<Throwable> shouldRetry,
            final int quietTries,
            final int maxTries,
            final CleanupAfterFailure cleanupAfterFailure,
            final String messageOnRetry
    ) throws Exception
    {
        Preconditions.checkArgument(maxTries > 0, "maxTries > 0");
        Preconditions.checkArgument(quietTries >= 0, "quietTries >= 0");
        int nTry = 0;
        final int maxRetries = maxTries - 1;
        while (true) {
            try {
                nTry++;
                return f.perform();
            }
            catch (Throwable e) {
                if (cleanupAfterFailure != null) {
                    cleanupAfterFailure.cleanup();
                }
                if (nTry < maxTries && shouldRetry.apply(e)) {
                    awaitNextRetry(e, messageOnRetry, nTry, maxRetries, nTry <= quietTries);
                } else {
                    Throwables.propagateIfInstanceOf(e, Exception.class);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static <T> T retry(final Task<T> f, Predicate<Throwable> shouldRetry, final int maxTries) throws Exception
    {
        return retry(f, shouldRetry, 0, maxTries);
    }

    public static <T> T retry(
            final Task<T> f,
            final Predicate<Throwable> shouldRetry,
            final int quietTries,
            final int maxTries
    ) throws Exception
    {
        return retry(f, shouldRetry, quietTries, maxTries, null, null);
    }

    public static <T> T retry(
            final Task<T> f,
            final Predicate<Throwable> shouldRetry,
            final CleanupAfterFailure onEachFailure,
            final int maxTries,
            final String messageOnRetry
    ) throws Exception
    {
        return retry(f, shouldRetry, 0, maxTries, onEachFailure, messageOnRetry);
    }

    public static void awaitNextRetry(
            final Throwable e,
            final String messageOnRetry,
            final int nTry,
            final int maxRetries,
            final boolean quiet
    ) throws InterruptedException
    {
        final long sleepMillis = nextRetrySleepMillis(nTry);
        final String fullMessage;

        if (messageOnRetry == null) {
            fullMessage = StringUtils.format("Retrying (%d of %d) in %,dms.", nTry, maxRetries, sleepMillis);
        } else {
            fullMessage = StringUtils.format(
                    "%s, retrying (%d of %d) in %,dms.",
                    messageOnRetry,
                    nTry,
                    maxRetries,
                    sleepMillis
            );
        }

        Thread.sleep(sleepMillis);
    }

    public static long nextRetrySleepMillis(final int nTry)
    {
        final double fuzzyMultiplier = Math.min(Math.max(1 + 0.2 * ThreadLocalRandom.current().nextGaussian(), 0), 2);
        final long sleepMillis = (long) (Math.min(MAX_SLEEP_MILLIS, BASE_SLEEP_MILLIS * Math.pow(2, nTry - 1))
                * fuzzyMultiplier);
        return sleepMillis;
    }
}
