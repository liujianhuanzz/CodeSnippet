package cn.hhspace.etl.framework;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 对于能够创建、启动、停止、注销的插件，需要实现这个接口
 * Created by liujianhuan on 2019/12/9
 */
public interface LifeCycleIntf {
    /**
     * 初始化插件，一般在这里完成其他插件的引用和装配工作，从BeanInventory中查找插件
     * @param inv
     * @throws Exception
     */
    void init(BeanInventory inv) throws Exception;

    /**
     * 销毁插件
     * @throws Exception
     */
    void destroy() throws Exception;

    /**
     * 启动插件
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 停止插件
     * @throws Exception
     */
    void stop() throws Exception;

    /**
     * 返回插件是否正在运行中
     * @return
     */
    @JsonIgnore
    boolean isRunning();
}
