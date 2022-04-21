package cn.hhspace.utils;

import cn.hhspace.guice.common.UOE;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 3:40 下午
 * @Descriptions:
 */
public class RuntimeInfo {
    public int getAvailableProcessors()
    {
        return Runtime.getRuntime().availableProcessors();
    }

    public long getMaxHeapSizeBytes()
    {
        return Runtime.getRuntime().maxMemory();
    }

    public long getTotalHeapSizeBytes()
    {
        return Runtime.getRuntime().totalMemory();
    }

    public long getFreeHeapSizeBytes()
    {
        return Runtime.getRuntime().freeMemory();
    }

    public long getDirectMemorySizeBytes()
    {
        try {
            Class<?> vmClass = Class.forName("sun.misc.VM");
            Object maxDirectMemoryObj = vmClass.getMethod("maxDirectMemory").invoke(null);

            if (maxDirectMemoryObj == null || !(maxDirectMemoryObj instanceof Number)) {
                throw new UOE("Cannot determine maxDirectMemory from [%s]", maxDirectMemoryObj);
            } else {
                return ((Number) maxDirectMemoryObj).longValue();
            }
        }
        catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("No VM class, cannot do memory check.", e);
        }
        catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("VM.maxDirectMemory doesn't exist, cannot do memory check.", e);
        }
        catch (InvocationTargetException e) {
            throw new UnsupportedOperationException("static method shouldn't throw this", e);
        }
        catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("public method, shouldn't throw this", e);
        }
    }
}
