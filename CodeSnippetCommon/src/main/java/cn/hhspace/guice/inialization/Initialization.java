package cn.hhspace.guice.inialization;

import cn.hhspace.guice.lifecycle.LifecycleModule;
import cn.hhspace.guice.modules.JettyServerModule;
import cn.hhspace.guice.modules.SecondaryModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 2:57 下午
 * @Descriptions: Initialization
 */
public class Initialization {

    public static Injector makeInjector(Injector baseInjector, List<? extends Module> modules) {
        ModuleList defaultModules = new ModuleList(baseInjector);
        defaultModules.addModules(
                new LifecycleModule(),
                new JettyServerModule()
        );

        ModuleList actualModules = new ModuleList(baseInjector);
        actualModules.addModules(SecondaryModule.class);
        for (Object module: modules) {
            actualModules.addModules(module);
        }

        return Guice.createInjector(Modules.override(defaultModules.getModules()).with(actualModules.getModules()));
    }

    private static class ModuleList{
        private final Injector baseInjector;
        private final List<Module> modules;

        public ModuleList(Injector baseInjector) {
            this.baseInjector = baseInjector;
            this.modules = new ArrayList<>();
        }

        private List<Module> getModules() {
            return Collections.unmodifiableList(modules);
        }

        public void addModule(Object input) {
            if (input instanceof Module) {
                baseInjector.injectMembers(input);
                modules.add((Module) input);
            } else if (input instanceof Class) {
                modules.add(baseInjector.getInstance((Class<? extends Module>)input));
            }
        }

        public void addModules(Object... object) {
            for (Object o: object) {
                addModule(o);
            }
        }
    }
}
