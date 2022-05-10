package cn.hhspace.utils.guava;

import java.util.function.Function;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/5/10 15:51
 * @Descriptions:
 */
public class MappedSequence<T, Out> implements Sequence<Out> {

    private final Sequence<T> baseSequence;
    private final Function<? super T, ? extends Out> fn;

    public MappedSequence(Sequence<T> baseSequence, Function<? super T, ? extends Out> fn) {
        this.baseSequence = baseSequence;
        this.fn = fn;
    }

    @Override
    public <OutType> OutType accumulate(OutType initValue, Accumulator<OutType, Out> accumulator) {
        return baseSequence.accumulate(initValue, new MappingAccumulator<>(fn, accumulator));
    }

    @Override
    public <OutType> Yielder<OutType> toYielder(OutType initValue, YieldingAccumulator<OutType, Out> accumulator) {
        return baseSequence.toYielder(initValue, new MappingYieldingAccumulator<>(fn, accumulator));
    }
}
