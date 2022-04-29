package cn.hhspace.utils.guava;

import com.google.common.base.Throwables;

import java.io.IOException;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/27 16:51
 * @Descriptions:
 */
public class Yielders {

    public static <T> Yielder<T> each(final Sequence<T> sequence) {
        return sequence.toYielder(
                null,
                new YieldingAccumulator<T, T>() {
                    @Override
                    public T accumulate(T accumulated, T in) {
                        yield();
                        return in;
                    }
                }
        );
    }

    public static <T> Yielder<T> done(final T finalValue, final AutoCloseable closeable) {
        return new Yielder<T>() {
            @Override
            public T get() {
                return finalValue;
            }

            @Override
            public Yielder<T> next(T initValue) {
                return null;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public void close() throws IOException {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                        Throwables.propagateIfInstanceOf(e, IOException.class);
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }
}
