package cn.hhspace.timewheel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class TimeWheel {

    // 时间轮大小，即时间跨度为tickDuration * wheelSize
    private int wheelSize;
    // 时间轮的时间跨度，单位为毫秒
    private long tickDuration;
    // 时间轮当前的槽
    private List<Task>[] slots;
    // 时间轮指针
    private int currentSlotIndex = 0;
    // 延时队列
    private DelayQueue<Task> delayQueue = new DelayQueue<>();

    public TimeWheel(int wheelSize, long tickDuration) {
        this.wheelSize = wheelSize;
        this.tickDuration = tickDuration;
        // 初始化时间轮的槽
        slots = new List[wheelSize];
        for (int i = 0; i < wheelSize; i++) {
            slots[i] = new ArrayList<>();
        }
        // 启动时间轮指针线程
        Thread pointerThread = new Thread(new PointerTask());
        pointerThread.start();
    }

    // 添加任务到时间轮
    public void addTask(Task task) {
        // 如果任务已经超时，则立即执行
        if (task.delayTime <= 0) {
            task.run();
        } else {
            // 计算任务在时间轮中的位置
            int index = (int) ((task.delayTime / tickDuration + currentSlotIndex) % wheelSize);
            slots[index].add(task);
            task.index = index;
            task.startTime = System.currentTimeMillis();
            delayQueue.offer(task);
        }
    }

    // 时间轮指针任务，每隔tickDuration毫秒转动一次
    private class PointerTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(tickDuration);
                    // 当前槽的任务需要执行
                    List<Task> currentSlot = slots[currentSlotIndex];
                    for (Task task : currentSlot) {
                        task.run();
                    }
                    currentSlot.clear();
                    // 时间轮指针向前移动一格
                    currentSlotIndex = (currentSlotIndex + 1) % wheelSize;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 时间轮中的任务
    private static class Task implements Delayed {
        // 延时时间
        private long delayTime;
        // 任务执行时间
        private long startTime;
        // 任务的位置
        private int index;

        public Task(long delayTime) {
            this.delayTime = delayTime;
        }

        public void run() {
            System.out.println("Task executed at " + System.currentTimeMillis());
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(startTime + delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }
    }

    public static void main(String[] args) {
        TimeWheel timeWheel = new TimeWheel(20, 10000);
        timeWheel.addTask(new Task(25000L));
        timeWheel.addTask(new Task(30000L));
        timeWheel.addTask(new Task(35000L));
        timeWheel.addTask(new Task(40000L));
        timeWheel.addTask(new Task(45000L));
        timeWheel.addTask(new Task(50000L));
        timeWheel.addTask(new Task(55000L));
        timeWheel.addTask(new Task(60000L));
        timeWheel.addTask(new Task(65000L));
    }
}

