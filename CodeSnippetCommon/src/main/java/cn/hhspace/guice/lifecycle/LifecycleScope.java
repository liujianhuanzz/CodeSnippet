package cn.hhspace.guice.lifecycle;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:29 下午
 * @Descriptions:
 */
@Slf4j
public class LifecycleScope implements Scope {
    private final Lifecycle.Stage stage;

    private Lifecycle lifecycle;
    private final List<Object> instances = new ArrayList<>();

    public LifecycleScope(Lifecycle.Stage stage)
    {
        this.stage = stage;
    }

    public void setLifecycle(Lifecycle lifecycle)
    {
        synchronized (instances) {
            this.lifecycle = lifecycle;
            for (Object instance : instances) {
                lifecycle.addManagedInstance(instance, stage);
            }
        }
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped)
    {
        return new Provider<T>()
        {
            private volatile T value = null;

            @Override
            public synchronized T get()
            {
                if (value == null) {
                    final T retVal = unscoped.get();

                    synchronized (instances) {
                        if (lifecycle == null) {
                            instances.add(retVal);
                        } else {
                            try {
                                lifecycle.addMaybeStartManagedInstance(retVal, stage);
                            }
                            catch (Exception e) {
                                log.warn("Caught exception when trying to create a[%s]", key, e);
                                return null;
                            }
                        }
                    }

                    value = retVal;
                }

                return value;
            }
        };
    }
}
