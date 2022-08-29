package cn.hhspace.designpattern.singleton;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 16:45
 * @Descriptions: 单例模式类
 */
public class SingletonSync {
    private static volatile SingletonSync instance;
    private String value;

    private SingletonSync(String value) {
        this.value = value;
    }

    public static SingletonSync getInstance(String value) {
        if (instance != null) {
            return instance;
        }

        synchronized (SingletonSync.class) {
            if (instance == null) {
                instance = new SingletonSync(value);
            }
            return instance;
        }
    }

    public String getValue() {
        return value;
    }
}
