package cn.hhspace.guice.demo;

import cn.hhspace.guice.inialization.Initialization;
import cn.hhspace.guice.lifecycle.Lifecycle;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 2:39 下午
 * @Descriptions: 基础线程
 */
public abstract class BaseThread implements Runnable{

    Injector baseInjector;

    @Inject
    public void configure(Injector injector)
    {
        this.baseInjector = injector;
    }

    protected abstract List<? extends Module> getModules();

    @Override
    public void run() {
        final Injector injector = Initialization.makeInjector(baseInjector, getModules());
        Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
        try {
            lifecycle.start();
            lifecycle.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
