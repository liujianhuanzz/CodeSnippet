package cn.hhspace.utils;

import com.google.inject.Inject;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 3:39 下午
 * @Descriptions: JVM工具类
 */
public class JvmUtils {
    private static final boolean IS_JAVA9_COMPATIBLE = isJava9Compatible(System.getProperty("java.specification.version"));

    private static boolean isJava9Compatible(String versionString)
    {
        final StringTokenizer st = new StringTokenizer(versionString, ".");
        int majorVersion = Integer.parseInt(st.nextToken());

        return majorVersion >= 9;
    }

    public static boolean isIsJava9Compatible()
    {
        return IS_JAVA9_COMPATIBLE;
    }

    @Inject
    private static RuntimeInfo runtimeInfo = new RuntimeInfo();

    public static RuntimeInfo getRuntimeInfo()
    {
        return runtimeInfo;
    }

    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

    public static boolean isThreadCpuTimeEnabled()
    {
        return THREAD_MX_BEAN.isThreadCpuTimeSupported() && THREAD_MX_BEAN.isThreadCpuTimeEnabled();
    }

    public static long safeGetThreadCpuTime()
    {
        if (!isThreadCpuTimeEnabled()) {
            return 0L;
        } else {
            return getCurrentThreadCpuTime();
        }
    }

    /**
     * Returns the total CPU time for current thread.
     * This method should be called after verifying that cpu time measurement for current thread is supported by JVM
     *
     * @return total CPU time for the current thread in nanoseconds.
     *
     * @throws UnsupportedOperationException if the Java virtual machine does not support CPU time measurement for
     *                                       the current thread.
     */
    public static long getCurrentThreadCpuTime()
    {
        return THREAD_MX_BEAN.getCurrentThreadCpuTime();
    }

    /**
     * Executes and returns the value of {@code function}. Also accumulates the CPU time taken for the function (as
     * reported by {@link #getCurrentThreadCpuTime()} into {@param accumulator}.
     */
    public static <T> T safeAccumulateThreadCpuTime(final AtomicLong accumulator, final Supplier<T> function)
    {
        final long start = safeGetThreadCpuTime();

        try {
            return function.get();
        }
        finally {
            accumulator.addAndGet(safeGetThreadCpuTime() - start);
        }
    }

    public static List<URL> systemClassPath()
    {
        List<URL> jobURLs;
        String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
        jobURLs = Stream.of(paths).map(
                s -> {
                    try {
                        return Paths.get(s).toUri().toURL();
                    }
                    catch (MalformedURLException e) {
                        throw new UnsupportedOperationException("Unable to create URL classpath entry", e);
                    }
                }
        ).collect(Collectors.toList());
        return jobURLs;
    }
}
