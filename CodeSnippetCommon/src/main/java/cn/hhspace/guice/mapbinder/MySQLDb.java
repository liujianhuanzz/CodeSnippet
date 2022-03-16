package cn.hhspace.guice.mapbinder;

/**
 * mysql实现类
 */
public class MySQLDb implements Db {
    @Override
    public void connectTest() {
        System.out.println("测试MySQL连接");
    }

    @Override
    public void doQuery() {
        System.out.println("执行MySQL查询");
    }
}
