package cn.hhspace.designpattern.singleton;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 16:51
 * @Descriptions: 单线程单例模式实例
 */
public class DemoSingleThread {
    public static void main(String[] args) {
        Singleton xxx = Singleton.getInstance("xxx");
        Singleton yyy = Singleton.getInstance("yyy");

        System.out.println(xxx.getValue());
        System.out.println(yyy.getValue());
    }
}
