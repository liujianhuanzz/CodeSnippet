package cn.hhspace.designpattern.iterator;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/9/2 14:07
 * @Descriptions:
 */
public class ReverseArrayCollection<T> implements Iterable<T> {

    private T[] array;

    public ReverseArrayCollection(T... objs) {
        this.array = Arrays.copyOfRange(objs, 0, objs.length);
    }

    @Override
    public Iterator<T> iterator() {
        return new ReverseIterator();
    }

    class ReverseIterator implements Iterator<T> {

        int index;

        public ReverseIterator() {
            this.index = ReverseArrayCollection.this.array.length;
        }

        @Override
        public boolean hasNext() {
            return index > 0;
        }

        @Override
        public T next() {
            index--;
            return array[index];
        }
    }
}
