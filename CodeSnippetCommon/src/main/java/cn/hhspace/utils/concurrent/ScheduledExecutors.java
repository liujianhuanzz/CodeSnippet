package cn.hhspace.utils.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.Duration;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/2 11:51 上午
 * @Descriptions: ScheduledExecutors
 */

@Slf4j
public class ScheduledExecutors {

    /**
     * Run runnable repeatedly with the given delay between calls, after the given
     * initial delay. Exceptions are caught and logged as errors.
     */
    public static void scheduleWithFixedDelay(
            final ScheduledExecutorService exec,
            final Duration initialDelay,
            final Duration delay,
            final Runnable runnable
    ){
        scheduleWithFixedDelay(
                exec,
                initialDelay,
                delay,
                new Callable<Signal>() {
                    @Override
                    public Signal call() throws Exception {
                        runnable.run();
                        if (exec.isShutdown()) {
                            log.warn("ScheduledExecutorService is ShutDown. Return 'Signal.STOP' and stopped rescheduling %s (delay %s)", this, delay);
                            return Signal.STOP;
                        } else {
                            return Signal.REPEAT;
                        }
                    }
                }
        );
    }

    /**
     * Run callable repeatedly with the given delay between calls, after the given
     * initial delay, until it returns Signal.STOP. Exceptions are caught and
     * logged as errors.
     */
    public static void scheduleWithFixedDelay(ScheduledExecutorService exec, Duration delay, Callable<Signal> callable) {
        scheduleWithFixedDelay(exec, delay, delay, callable);
    }

    /**
     * Run callable repeatedly with the given delay between calls, until it
     * returns Signal.STOP. Exceptions are caught and logged as errors.
     */
    public static void scheduleWithFixedDelay(
            final ScheduledExecutorService exec,
            final Duration initialDelay,
            final Duration delay,
            final Callable<Signal> callable
    ){
        log.debug("Scheduling repeatedly: %s with delay %s", callable, delay);
        exec.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.trace("Running %s (delay %s)", callable, delay);
                            if (callable.call() == Signal.REPEAT) {
                                log.trace("Rescheduling %s (delay %s)", callable, delay);
                                exec.schedule(this, delay.getMillis(), TimeUnit.MILLISECONDS);
                            } else {
                                log.debug("Stopped rescheduling %s (delay %s)", callable, delay);
                            }
                        } catch (Throwable e) {
                            log.error(e.getMessage());
                        }
                    }
                },
                initialDelay.getMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Run runnable once every period, after the given initial delay. Exceptions
     * are caught and logged as errors.
     */
    public static void scheduleAtFixedRate(
            final ScheduledExecutorService exec,
            final Duration initialDelay,
            final Duration period,
            final Runnable runnable
    ) {
        scheduleWithFixedDelay(
                exec,
                initialDelay,
                period,
                new Callable<Signal>() {
                    @Override
                    public Signal call() throws Exception {
                        runnable.run();
                        return Signal.REPEAT;
                    }
                }
        );
    }

    public static void scheduleAtFixedRate(ScheduledExecutorService exec, Duration rate, Callable<Signal> callable)
    {
        scheduleAtFixedRate(exec, rate, rate, callable);
    }

    public static void scheduleAtFixedRate(
            final ScheduledExecutorService exec,
            final Duration initialDelay,
            final Duration rate,
            final Callable<Signal> callable
    )
    {
        log.debug("Scheduling periodically: %s with period %s", callable, rate);
        exec.schedule(
                new Runnable()
                {
                    private volatile Signal prevSignal = null;

                    @Override
                    public void run()
                    {
                        if (prevSignal == null || prevSignal == Signal.REPEAT) {
                            exec.schedule(this, rate.getMillis(), TimeUnit.MILLISECONDS);
                        }

                        try {
                            log.trace("Running %s (period %s)", callable, rate);
                            prevSignal = callable.call();
                        }
                        catch (Throwable e) {
                            log.error(e.getMessage());
                        }
                    }
                },
                initialDelay.getMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    public enum Signal {
        REPEAT,
        STOP
    }
}
