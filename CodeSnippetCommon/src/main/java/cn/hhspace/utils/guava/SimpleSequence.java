package cn.hhspace.utils.guava;

import java.util.Iterator;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/29 14:56
 * @Descriptions:
 */
public class SimpleSequence<T> extends BaseSequence<T, Iterator<T>> {
    private final Iterable<T> iterable;

    SimpleSequence(final Iterable<T> iterable) {
        super(
                new BaseSequence.IteratorMaker<T, Iterator<T>>(){

                    @Override
                    public Iterator<T> make() {
                        return iterable.iterator();
                    }

                    @Override
                    public void cleanup(Iterator<T> iterFromMake) {

                    }
                }
        );
        this.iterable = iterable;
    }

    public Iterable<T> getIterable() {
        return iterable;
    }
}
