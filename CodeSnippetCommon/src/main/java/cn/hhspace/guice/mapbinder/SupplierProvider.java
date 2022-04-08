package cn.hhspace.guice.mapbinder;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/8 4:53 下午
 * @Descriptions: 对provider的supplier封装
 */
public class SupplierProvider<T> implements Provider<T>
{
    private final Key<Supplier<T>> supplierKey;

    private Provider<Supplier<T>> supplierProvider;

    public SupplierProvider(
            Key<Supplier<T>> supplierKey
    )
    {
        this.supplierKey = supplierKey;
    }

    @Inject
    public void configure(Injector injector)
    {
        this.supplierProvider = injector.getProvider(supplierKey);
    }

    @Override
    public T get()
    {
        return supplierProvider.get().get();
    }
}
