package cn.hhspace.jvm.metrics;

import cn.hhspace.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.*;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/25 10:58 上午
 * @Descriptions: 内存类JVM指标
 */
@Slf4j
public class JvmMetrics {

    public static void main(String[] args) {
        MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMxBean.getHeapMemoryUsage();
        log.info(StringUtils.format("Used: %s, Max: %s, Committed: %s" , heapMemoryUsage.getUsed(), heapMemoryUsage.getMax(), heapMemoryUsage.getCommitted()));

        List<GarbageCollectorMXBean> gcMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcMxBean : gcMxBeans) {
            log.info(StringUtils.format("Name: %s, GcTime: %s, GcCount: %s", gcMxBean.getName(), gcMxBean.getCollectionTime(), gcMxBean.getCollectionCount()));
        }

        List<MemoryPoolMXBean> memoryPoolMxBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean memoryPoolMxBean : memoryPoolMxBeans) {
            MemoryUsage collectionUsage = memoryPoolMxBean.getCollectionUsage();
            if (collectionUsage != null) {
                String name = memoryPoolMxBean.getName();
                log.info(StringUtils.format("name: %s, used: %s, max: %s, committed: %s", name, collectionUsage.getUsed(), collectionUsage.getMax(), collectionUsage.getCommitted()));
            }
        }
    }
}
