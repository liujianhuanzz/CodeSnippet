package cn.hhspace.etl.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 16:41
 * @Descriptions: 对BlockingQueue包装，增加一个计数功能
 */
public class CountedBlockQueue<E> implements BlockingQueue<E> {

    private BlockingQueue<E> innerQueue;

    long writeCount = 0;

    public CountedBlockQueue(BlockingQueue<E> innerQueue) {
        this.innerQueue = innerQueue;
        writeCount = 0;
    }

    public void incWriteCount() {
        ++writeCount;
    }

    public long getWriteCount() {
        return writeCount;
    }

    @Override
    public boolean add(E e) {
        boolean result = innerQueue.add(e);
        if (result) {
            incWriteCount();
        }
        return result;
    }

    @Override
    public boolean offer(E e) {
        boolean result = innerQueue.offer(e);
        if (result) {
            incWriteCount();
        }
        return result;
    }

    @Override
    public E remove() {
        return innerQueue.remove();
    }

    @Override
    public E poll() {
        return innerQueue.poll();
    }

    @Override
    public E element() {
        return innerQueue.element();
    }

    @Override
    public E peek() {
        return innerQueue.peek();
    }

    @Override
    public void put(E e) throws InterruptedException {
        innerQueue.put(e);
        incWriteCount();
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        boolean result = innerQueue.offer(e, timeout, unit);
        if (result) {
            incWriteCount();
        }
        return result;
    }

    @Override
    public E take() throws InterruptedException {
        return innerQueue.take();
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return innerQueue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return innerQueue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return innerQueue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return innerQueue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return innerQueue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return innerQueue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return innerQueue.retainAll(c);
    }

    @Override
    public void clear() {
        innerQueue.clear();
    }

    @Override
    public int size() {
        return innerQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return innerQueue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return innerQueue.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return innerQueue.iterator();
    }

    @Override
    public Object[] toArray() {
        return innerQueue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return innerQueue.toArray(a);
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return innerQueue.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return innerQueue.drainTo(c, maxElements);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return innerQueue.removeIf(filter);
    }

    @Override
    public Spliterator<E> spliterator() {
        return innerQueue.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return innerQueue.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return innerQueue.parallelStream();
    }

    @Override
    public boolean equals(Object obj) {
        return innerQueue.equals(obj);
    }
}
