package cn.hhspace.utils.guava;

import java.io.Closeable;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/27 16:19
 * @Descriptions:
 */
public interface Yielder<T> extends Closeable {
    /**
     * Gets the object currently held by this Yielder.
     * @return the currently yielded object, null if done
     */
    T get();

    /**
     * Gets the next Yielder in the chain.
     * @param initValue the initial value to pass along to start the accumulation until the next yield() call or iteration completes.
     * @return the next Yielder in the chain, or undefined if done
     */
    Yielder<T> next(T initValue);

    /**
     * Returns true if this is the last Yielder in the chain.
     * @return true if this is the last Yielder in the chain, false otherwise
     */
    boolean isDone();
}
