package cn.hhspace.designpattern.singleton;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 17:07
 * @Descriptions: 单例模式的多线程实例
 */
public class DemoMultiThread {

    public static void main(String[] args) {
        Thread thread1Xxx = new Thread(new Thread1Xxx());
        Thread thread1Yyy = new Thread(new Thread1Yyy());
        thread1Xxx.start();
        thread1Yyy.start();

        Thread thread2Xxx = new Thread(new Thread2Xxx());
        Thread thread2Yyy = new Thread(new Thread2Yyy());
        thread2Xxx.start();
        thread2Yyy.start();
    }

    static class Thread1Xxx implements Runnable {
        @Override
        public void run() {
            Singleton xxx = Singleton.getInstance("xxx");
            System.out.println("1" + xxx.getValue());
        }
    }

    static class Thread1Yyy implements Runnable {
        @Override
        public void run() {
            Singleton yyy = Singleton.getInstance("yyy");
            System.out.println("1" + yyy.getValue());
        }
    }

    static class Thread2Xxx implements Runnable {
        @Override
        public void run() {
            SingletonSync xxx = SingletonSync.getInstance("xxx");
            System.out.println("2" + xxx.getValue());
        }
    }

    static class Thread2Yyy implements Runnable {
        @Override
        public void run() {
            SingletonSync yyy = SingletonSync.getInstance("yyy");
            System.out.println("2" + yyy.getValue());
        }
    }
}
