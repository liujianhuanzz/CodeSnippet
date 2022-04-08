package cn.hhspace.guice.demo;

import cn.hhspace.utils.StringUtils;
import com.google.inject.Inject;

/**
 * PG实现类
 */
public class PostGreSQLDb implements Db {

    @Inject
    PostGreSQLDbConfig postGreSQLDbConfig;

    @Override
    public void connectTest() {
        System.out.println("测试PostGreSQL连接");
    }

    @Override
    public void doQuery() {
        System.out.println("执行PostGreSQL查询");
        System.out.println(StringUtils.format(
                "username: %s, password: %s",
                postGreSQLDbConfig.getUsername(),
                postGreSQLDbConfig.getPassword()
        ));
    }
}
