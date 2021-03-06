package cn.hhspace.utils;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/19 5:46 下午
 * @Descriptions:
 */
public class Logger {
    @VisibleForTesting
    static final int SEGMENTS_PER_LOG_MESSAGE = 64;

    private final org.slf4j.Logger log;
    private final boolean stackTraces;
    private final Logger noStackTraceLogger;

    public Logger(String name)
    {
        this(LoggerFactory.getLogger(name), true);
    }

    public Logger(Class clazz)
    {
        this(LoggerFactory.getLogger(clazz), true);
    }

    protected Logger(org.slf4j.Logger log, boolean stackTraces)
    {
        this.log = log;
        this.stackTraces = stackTraces;
        noStackTraceLogger = stackTraces ? new Logger(log, false) : this;
    }

    protected org.slf4j.Logger getSlf4jLogger()
    {
        return log;
    }

    @Override
    public String toString()
    {
        return StringUtils.format("Logger{name=[%s], class[%s]}", log.getName(), log.getClass());
    }

    /**
     * Returns a copy of this Logger that does not log exception stack traces, unless the log level is DEBUG or lower.
     * Useful for writing code like: {@code log.noStackTrace().warn(e, "Something happened.");}
     */
    public Logger noStackTrace()
    {
        return noStackTraceLogger;
    }

    public void trace(String message, Object... formatArgs)
    {
        if (log.isTraceEnabled()) {
            log.trace(StringUtils.nonStrictFormat(message, formatArgs));
        }
    }

    public void debug(String message, Object... formatArgs)
    {
        if (log.isDebugEnabled()) {
            log.debug(StringUtils.nonStrictFormat(message, formatArgs));
        }
    }

    public void debug(Throwable t, String message, Object... formatArgs)
    {
        if (log.isDebugEnabled()) {
            logException(log::debug, t, StringUtils.nonStrictFormat(message, formatArgs));
        }
    }

    public void info(String message, Object... formatArgs)
    {
        if (log.isInfoEnabled()) {
            log.info(StringUtils.nonStrictFormat(message, formatArgs));
        }
    }

    public void info(Throwable t, String message, Object... formatArgs)
    {
        if (log.isInfoEnabled()) {
            logException(log::info, t, StringUtils.nonStrictFormat(message, formatArgs));
        }
    }

    /**
     * Protect against assuming slf4j convention. use `warn(Throwable t, String message, Object... formatArgs)` instead
     *
     * @param message The string message
     * @param t       The Throwable to log
     */
    @Deprecated
    public void warn(String message, Throwable t)
    {
        warn(t, message);
    }

    public void warn(String message, Object... formatArgs)
    {
        log.warn(StringUtils.nonStrictFormat(message, formatArgs));
    }

    public void warn(Throwable t, String message, Object... formatArgs)
    {
        logException(log::warn, t, StringUtils.nonStrictFormat(message, formatArgs));
    }

    public void error(String message, Object... formatArgs)
    {
        log.error(StringUtils.nonStrictFormat(message, formatArgs));
    }

    /**
     * Protect against assuming slf4j convention. use `error(Throwable t, String message, Object... formatArgs)` instead
     *
     * @param message The string message
     * @param t       The Throwable to log
     */
    @Deprecated
    public void error(String message, Throwable t)
    {
        error(t, message);
    }

    public void error(Throwable t, String message, Object... formatArgs)
    {
        logException(log::error, t, StringUtils.nonStrictFormat(message, formatArgs));
    }

    public void assertionError(String message, Object... formatArgs)
    {
        log.error("ASSERTION_ERROR: " + message, formatArgs);
    }

    public boolean isTraceEnabled()
    {
        return log.isTraceEnabled();
    }

    public boolean isDebugEnabled()
    {
        return log.isDebugEnabled();
    }

    public boolean isInfoEnabled()
    {
        return log.isInfoEnabled();
    }

    private void logException(BiConsumer<String, Throwable> fn, Throwable t, String message)
    {
        if (stackTraces || log.isDebugEnabled()) {
            fn.accept(message, t);
        } else {
            if (message.isEmpty()) {
                fn.accept(t.toString(), null);
            } else {
                fn.accept(StringUtils.nonStrictFormat("%s (%s)", message, t.toString()), null);
            }
        }
    }

    @FunctionalInterface
    public interface LogFunction
    {
        void log(String msg, Object... format);
    }
}
