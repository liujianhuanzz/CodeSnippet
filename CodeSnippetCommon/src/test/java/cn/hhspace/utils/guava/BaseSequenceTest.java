package cn.hhspace.utils.guava;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/29 15:45
 * @Descriptions:
 */
public class BaseSequenceTest {

    @Test
    public void testSanity() throws Exception {
        final List<Integer> vals = Arrays.asList(1, 2, 3, 4, 5);
        SequenceTestHelper.testAll(makeBaseSequence(vals), vals);
    }

    @Test
    public void testNothing() throws Exception {
        final List<Integer> vals = Collections.emptyList();
        SequenceTestHelper.testAll(makeBaseSequence(vals), vals);
    }

    private <T> Sequence<T> makeBaseSequence(final Iterable<T> iterable) {
        return new BaseSequence<>(
                new BaseSequence.IteratorMaker<T, Iterator<T>>() {
                    @Override
                    public Iterator<T> make() {
                        return iterable.iterator();
                    }

                    @Override
                    public void cleanup(Iterator<T> iterFromMake) {

                    }
                }
        );
    }
}
