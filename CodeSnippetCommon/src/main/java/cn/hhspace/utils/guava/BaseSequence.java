package cn.hhspace.utils.guava;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/29 12:20
 * @Descriptions:
 */
public class BaseSequence<T, IterType extends Iterator<T>> implements Sequence<T> {

    private final IteratorMaker<T, IterType> maker;

    public BaseSequence(IteratorMaker<T, IterType> maker) {
        this.maker = maker;
    }

    @Override
    public <OutType> OutType accumulate(final OutType initValue,  final Accumulator<OutType, T> accumulator) {
        IterType iterator = maker.make();
        OutType accumulated = initValue;

        try {
            while (iterator.hasNext()) {
                accumulated = accumulator.accumulate(accumulated, iterator.next());
            }
        } catch (Throwable t) {
            try {
                maker.cleanup(iterator);
            } catch (Exception e) {
                t.addSuppressed(e);
            }
            throw t;
        }

        maker.cleanup(iterator);
        return accumulated;
    }

    @Override
    public <OutType> Yielder<OutType> toYielder(
            final OutType initValue,
            final YieldingAccumulator<OutType, T> accumulator) {
        
        final IterType iterator = maker.make();
        
        try {
            return makeYielder(initValue, accumulator, iterator);
        } catch (Throwable t) {
            try {
                maker.cleanup(iterator);
            } catch (Exception e) {
                t.addSuppressed(e);
            }
            throw t;
        }
    }

    private <OutType> Yielder<OutType> makeYielder(
            final OutType initValue,
            final YieldingAccumulator<OutType,T> accumulator,
            final IterType iterator) {
        OutType retVal = initValue;
        while (!accumulator.yielded() && iterator.hasNext()) {
            retVal = accumulator.accumulate(retVal, iterator.next());
        }

        if (!accumulator.yielded()) {
            return Yielders.done(retVal, (Closeable)() -> maker.cleanup(iterator));
        }

        final OutType finalRetVal = retVal;
        return new Yielder<OutType>() {
            @Override
            public OutType get() {
                return finalRetVal;
            }

            @Override
            public Yielder<OutType> next(OutType initValue) {
                accumulator.reset();
                try {
                    return makeYielder(initValue, accumulator, iterator);
                } catch (Throwable t) {
                    try {
                        maker.cleanup(iterator);
                    }
                    catch (Exception e) {
                        t.addSuppressed(e);
                    }
                    throw t;
                }
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public void close() throws IOException {
                maker.cleanup(iterator);
            }
        };
    }

    public interface IteratorMaker<T, IterType extends Iterator<T>> {

        IterType make();

        void cleanup(IterType iterFromMake);
    }
}
