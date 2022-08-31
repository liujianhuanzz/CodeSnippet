package cn.hhspace.designpattern.factory;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/29 17:50
 * @Descriptions: 通用Button接口
 */
public interface Button {
    /**
     * 渲染方法
     */
    void render();

    /**
     * 监听Click事件
     */
    void onClick();
}
