package cn.hhspace.queue;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/2/24 14:27
 * @Descriptions: 优先级队列
 */
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个优先级队列
        PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>();

        // 添加若干个任务到队列中，每个任务具有不同的优先级
        queue.add(new Task(1));
        queue.add(new Task(2));
        queue.add(new Task(3));
        queue.add(new Task(4));
        queue.add(new Task(5));

        // 创建两个处理线程
        Thread thread1 = new Thread(() -> {
            try {
                while (true) {
                    Task task = queue.take();
                    System.out.println(Thread.currentThread().getName() + " process task " + task.getPriority());
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                while (true) {
                    Task task = queue.take();
                    System.out.println(Thread.currentThread().getName() + " process task " + task.getPriority());
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 启动处理线程
        thread1.start();
        thread2.start();

        // 等待一段时间后，添加一个具有较高优先级的任务到队列中
        Thread.sleep(5000);
        queue.add(new Task(6));

        // 等待一段时间后，再添加一个具有较低优先级的任务到队列中
        Thread.sleep(5000);
        queue.add(new Task(0));
    }

    // 任务对象，具有优先级属性
    private static class Task implements Comparable<Task> {
        private int priority;

        public Task(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public int compareTo(Task o) {
            return Integer.compare(o.priority, this.priority);
        }
    }
}

