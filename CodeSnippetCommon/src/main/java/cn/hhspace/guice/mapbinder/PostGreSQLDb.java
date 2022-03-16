package cn.hhspace.guice.mapbinder;

/**
 * PG实现类
 */
public class PostGreSQLDb implements Db {
    @Override
    public void connectTest() {
        System.out.println("测试PostGreSQL连接");
    }

    @Override
    public void doQuery() {
        System.out.println("执行PostGreSQL查询");
    }
}
