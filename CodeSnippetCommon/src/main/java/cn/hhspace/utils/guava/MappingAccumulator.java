package cn.hhspace.utils.guava;

import java.util.function.Function;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/5/10 14:31
 * @Descriptions:
 */
public class MappingAccumulator<OutType, InType, MappedType> implements Accumulator<OutType, InType> {

    private final Function<? super InType, ? extends MappedType> fn;
    private final Accumulator<OutType, MappedType> accumulator;

    public MappingAccumulator(Function<? super InType, ? extends MappedType> fn, Accumulator<OutType, MappedType> accumulator) {
        this.fn = fn;
        this.accumulator = accumulator;
    }

    @Override
    public OutType accumulate(OutType accumulated, InType in) {
        return accumulator.accumulate(accumulated, fn.apply(in));
    }
}
