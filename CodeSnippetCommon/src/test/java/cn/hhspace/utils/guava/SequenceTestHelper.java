package cn.hhspace.utils.guava;

import org.junit.Assert;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/29 15:12
 * @Descriptions:
 */
public class SequenceTestHelper {

    public static void testAll(Sequence<Integer> seq, List<Integer> nums) throws IOException {
        testAll("", seq, nums);
    }

    public static void testAll(String prefix, Sequence<Integer> seq, List<Integer> nums) throws IOException {
        testAccumulation(prefix, seq, nums);
        testYield(prefix, seq, nums);
    }

    private static void testYield(String prefix, Sequence<Integer> seq, List<Integer> nums) throws IOException {
        testYield(prefix, 3, seq, nums);
        testYield(prefix, 1, seq, nums);
    }

    private static void testYield(
            String prefix,
            int numToTake,
            Sequence<Integer> seq,
            List<Integer> nums) throws IOException {
        Iterator<Integer> numsIter = nums.iterator();
        Yielder<Integer> yielder = seq.toYielder(
                0,
                new YieldingAccumulator<Integer, Integer>() {

                    final Iterator<Integer> valsIter = nums.iterator();
                    int count = 0;

                    @Override
                    public Integer accumulate(Integer accumulated, Integer in) {
                        if (++count >= numToTake) {
                            count = 0;
                            yield();
                        }

                        Assert.assertEquals(prefix, valsIter.next(), in);
                        return accumulated + in;
                    }
                }
        );

        int expectedSum = 0;
        while (numsIter.hasNext()) {
            int i = 0;
            for (; i < numToTake && numsIter.hasNext(); ++i) {
                expectedSum += numsIter.next();
            }

            if (i >= numToTake) {
                Assert.assertFalse(prefix, yielder.isDone());
                Assert.assertEquals(prefix, expectedSum, yielder.get().intValue());

                expectedSum = 0;
                yielder = yielder.next(0);
            }
        }

        Assert.assertEquals(expectedSum, yielder.get().intValue());
        Assert.assertTrue(prefix, yielder.isDone());

        yielder.close();
    }


    private static void testAccumulation(String prefix, Sequence<Integer> seq, List<Integer> nums) {
        int expectedSum = 0;
        for (Integer num : nums) {
            expectedSum += num;
        }

        int sum = seq.accumulate(
                0,
                new Accumulator<Integer, Integer>() {
                    final Iterator<Integer> valsIter = nums.iterator();

                    @Override
                    public Integer accumulate(Integer accumulated, Integer in) {
                        Assert.assertEquals(prefix, valsIter.next(), in);
                        return accumulated + in;
                    }
                }
        );

        Assert.assertEquals(prefix, expectedSum, sum);
    }

}
