package cn.hhspace.designpattern.observer;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/9/2 15:09
 * @Descriptions:
 */
public interface Observer {
    /**
     * 更新操作
     * @param ds 观察的具体数据源
     * @param data 更新的数据
     */
    void update(DataSource ds, String data);
}
