package cn.hhspace.guice.jetty.initialize;

import com.google.inject.Injector;
import org.eclipse.jetty.server.Server;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 5:12 下午
 * @Descriptions: JettyServer初始化
 */
public interface JettyServerInitializer {
    void initialize(Server server, Injector injector);
}
