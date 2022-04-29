package cn.hhspace.utils.guava;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/27 16:47
 * @Descriptions:
 */
public class Sequences {

    private static final EmptySequence EMPTY_SEQUENCE = new EmptySequence();

    public static <T> Sequence<T> simple(final Iterable<T> iterable) {
        return new SimpleSequence<>(iterable);
    }

    @SuppressWarnings("unchecked")
    public static <T> Sequence<T> empty() {
        return (Sequence<T>) EMPTY_SEQUENCE;
    }

    private static class EmptySequence implements Sequence<Object> {

        @Override
        public <OutType> OutType accumulate(OutType initValue, Accumulator<OutType, Object> accumulator) {
            return initValue;
        }

        @Override
        public <OutType> Yielder<OutType> toYielder(OutType initValue, YieldingAccumulator<OutType, Object> accumulator) {
            return Yielders.done(initValue, null);
        }
    }
}
