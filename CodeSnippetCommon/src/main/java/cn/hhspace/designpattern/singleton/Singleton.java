package cn.hhspace.designpattern.singleton;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 16:45
 * @Descriptions: 单例模式类
 */
public class Singleton {
    private static Singleton instance;
    private String value;

    private Singleton(String value) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.value = value;
    }

    public static Singleton getInstance(String value) {
        if (instance == null) {
            instance = new Singleton(value);
        }
        return instance;
    }

    public String getValue() {
        return value;
    }
}
