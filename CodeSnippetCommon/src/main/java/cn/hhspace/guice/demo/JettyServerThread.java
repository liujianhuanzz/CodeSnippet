package cn.hhspace.guice.demo;

import cn.hhspace.guice.demo.controller.HelloResource;
import cn.hhspace.guice.demo.controller.IndexResource;
import cn.hhspace.guice.demo.controller.PdfResource;
import cn.hhspace.guice.jetty.initialize.JettyServerInitializer;
import cn.hhspace.guice.jetty.initialize.MyJettyServerInitializer;
import cn.hhspace.guice.lifecycle.LifecycleModule;
import cn.hhspace.guice.mapbinder.Jerseys;
import cn.hhspace.guice.mapbinder.annotations.LazySingleton;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 2:32 下午
 * @Descriptions:
 */
public class JettyServerThread extends BaseThread{

    @Override
    protected List<? extends Module> getModules() {
        return ImmutableList.of(
              binder -> {
                  Jerseys.addResource(binder, IndexResource.class);
                  Jerseys.addResource(binder, HelloResource.class);
                  Jerseys.addResource(binder, PdfResource.class);

                  binder.bind(JettyServerInitializer.class).to(MyJettyServerInitializer.class).in(LazySingleton.class);
                  LifecycleModule.register(binder, Server.class);
              }
        );
    }
}
