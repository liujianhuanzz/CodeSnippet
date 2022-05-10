package cn.hhspace.utils.guava;

import java.util.function.Function;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/5/10 14:48
 * @Descriptions:
 */
public class MappingYieldingAccumulator<OutType, InType, MappedType> extends YieldingAccumulator<OutType, InType> {

    private final Function<? super InType, ? extends MappedType> fn;
    private final YieldingAccumulator<OutType, MappedType> baseAccumulator;

    public MappingYieldingAccumulator(Function<? super InType, ? extends MappedType> fn, YieldingAccumulator<OutType, MappedType> baseAccumulator) {
        this.fn = fn;
        this.baseAccumulator = baseAccumulator;
    }

    @Override
    public void yield() {
        baseAccumulator.yield();
    }

    @Override
    public boolean yielded() {
        return baseAccumulator.yielded();
    }

    @Override
    public void reset() {
        baseAccumulator.reset();
    }

    @Override
    public OutType accumulate(OutType accumulated, InType in) {
        return baseAccumulator.accumulate(accumulated, fn.apply(in));
    }
}
