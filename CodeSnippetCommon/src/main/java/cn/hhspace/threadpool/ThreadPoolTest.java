package cn.hhspace.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/3/14 15:20
 * @Descriptions:
 */

public class ThreadPoolTest {

    private static final int TASK_NUM = 10000;

    public static void main(String[] args) throws InterruptedException {
        int corePoolSize1 = 2;
        int corePoolSize2 = 10;
        int taskCount = TASK_NUM;

        long startTime = System.currentTimeMillis();
        ExecutorService threadPool1 = Executors.newFixedThreadPool(corePoolSize1);
        for (int i = 0; i < taskCount; i++) {
            threadPool1.execute(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        threadPool1.shutdown();
        while (!threadPool1.isTerminated()) {
            Thread.sleep(10);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time with corePoolSize " + corePoolSize1 + " : " + (endTime - startTime) + "ms");


        startTime = System.currentTimeMillis();
        ExecutorService threadPool2 = Executors.newFixedThreadPool(corePoolSize2);
        for (int i = 0; i < taskCount; i++) {
            threadPool2.execute(() -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        threadPool2.shutdown();
        while (!threadPool2.isTerminated()) {
            Thread.sleep(10);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Elapsed time with corePoolSize " + corePoolSize2 + " : " + (endTime - startTime) + "ms");
    }
}

