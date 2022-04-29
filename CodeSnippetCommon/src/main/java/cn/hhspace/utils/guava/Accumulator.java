package cn.hhspace.utils.guava;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/27 16:14
 * @Descriptions:
 */
public interface Accumulator<AccumulatorType, InType> {
    /**
     * Accumulator
     * @param accumulated
     * @param in
     * @return AccumulatorType
     */
    AccumulatorType accumulate(AccumulatorType accumulated, InType in);
}
