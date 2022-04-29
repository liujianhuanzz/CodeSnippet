package cn.hhspace.utils.guava;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/27 16:12
 * @Descriptions:
 */
public interface Sequence<T> {

    /**
     * Accumulate this sequence using the given accumulator.
     * @param initValue the initial value to pass along to start the accumulation.
     * @param accumulator the accumulator which is responsible for accumulating input values.
     * @param <OutType> the type of accumulated value.
     * @return accumulated value.
     */
    <OutType> OutType accumulate(OutType initValue, Accumulator<OutType, T> accumulator);

    /**
     * Return a Yielder for accumulated sequence.
     * @param initValue the initial value to pass along to start the accumulation.
     * @param accumulator the accumulator which is responsible for accumulating input values.
     * @param <OutType> the type of accumulated value.
     * @return a Yielder for accumulated sequence.
     */
    <OutType> Yielder<OutType> toYielder(OutType initValue, YieldingAccumulator<OutType, T> accumulator);
}
