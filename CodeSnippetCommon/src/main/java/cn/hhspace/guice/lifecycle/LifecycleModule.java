package cn.hhspace.guice.lifecycle;

import cn.hhspace.guice.lifecycle.annotations.ManageLifecycle;
import cn.hhspace.guice.lifecycle.annotations.ManageLifecycleAnnouncements;
import cn.hhspace.guice.lifecycle.annotations.ManageLifecycleInit;
import cn.hhspace.guice.lifecycle.annotations.ManageLifecycleServer;
import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:32 下午
 * @Descriptions:
 */
public class LifecycleModule implements Module {
    // this scope includes final logging shutdown, so all other handlers in this lifecycle scope should avoid logging in
    // the 'stop' method, either failing silently or failing violently and throwing an exception causing an ungraceful exit
    private final LifecycleScope initScope = new LifecycleScope(Lifecycle.Stage.INIT);
    private final LifecycleScope scope = new LifecycleScope(Lifecycle.Stage.NORMAL);
    private final LifecycleScope serverScope = new LifecycleScope(Lifecycle.Stage.SERVER);
    private final LifecycleScope annoucementsScope = new LifecycleScope(Lifecycle.Stage.ANNOUNCEMENTS);

    public static void register(Binder binder, Class<?> clazz)
    {
        registerKey(binder, Key.get(clazz));
    }

    public static void register(Binder binder, Class<?> clazz, Class<? extends Annotation> annotation)
    {
        registerKey(binder, Key.get(clazz, annotation));
    }

    public static void registerKey(Binder binder, Key<?> key)
    {
        getEagerBinder(binder).addBinding().toInstance(new KeyHolder<Object>(key));
    }

    private static Multibinder<KeyHolder> getEagerBinder(Binder binder)
    {
        return Multibinder.newSetBinder(binder, KeyHolder.class, Names.named("lifecycle"));
    }

    @Override
    public void configure(Binder binder)
    {
        getEagerBinder(binder); // Load up the eager binder so that it will inject the empty set at a minimum.

        binder.bindScope(ManageLifecycleInit.class, initScope);
        binder.bindScope(ManageLifecycle.class, scope);
        binder.bindScope(ManageLifecycleServer.class, serverScope);
        binder.bindScope(ManageLifecycleAnnouncements.class, annoucementsScope);
    }

    @Provides
    @Singleton
    public Lifecycle getLifecycle(final Injector injector)
    {
        final Key<Set<KeyHolder>> keyHolderKey = Key.get(new TypeLiteral<Set<KeyHolder>>(){}, Names.named("lifecycle"));
        final Set<KeyHolder> eagerClasses = injector.getInstance(keyHolderKey);

        Lifecycle lifecycle = new Lifecycle("module")
        {
            @Override
            public void start() throws Exception
            {
                for (KeyHolder<?> holder : eagerClasses) {
                    injector.getInstance(holder.getKey()); // Pull the key so as to "eagerly" load up the class.
                }
                super.start();
            }
        };
        initScope.setLifecycle(lifecycle);
        scope.setLifecycle(lifecycle);
        serverScope.setLifecycle(lifecycle);
        annoucementsScope.setLifecycle(lifecycle);

        return lifecycle;
    }
}
