package cn.hhspace.guice.demo;

/**
 * Db接口
 */
public interface Db {
    /**
     * 连接测试
     */
    void connectTest();

    /**
     * 执行查询
     */
    void doQuery();
}
