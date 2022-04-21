package cn.hhspace.guice.jetty;

import cn.hhspace.guice.inialization.ServerConfig;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 5:14 下午
 * @Descriptions: 一种实现初始化
 */
public class MyJettyServerInitializer implements JettyServerInitializer{

    private final ServerConfig serverConfig;

    @Inject
    public MyJettyServerInitializer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void initialize(Server server, Injector injector) {
        final ServletContextHandler root = new ServletContextHandler(ServletContextHandler.SESSIONS);
        root.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ServletHolder holderPwd = new ServletHolder(new DefaultServlet());

        root.addServlet(holderPwd, "/");

        root.addFilter(GuiceFilter.class, "/index/*", null);
        root.addFilter(GuiceFilter.class, "/hello/*", null);

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(
                new Handler[]{
                        JettyServerInitUtils.wrapWithDefaultGzipHandler(
                                root,
                                serverConfig.getInflateBufferSize(),
                                serverConfig.getCompressionLevel()
                        )
                }
        );

        server.setHandler(handlerList);
    }
}
