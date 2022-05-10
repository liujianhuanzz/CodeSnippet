package cn.hhspace.utils.guava;

import cn.hhspace.utils.StringUtils;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/5/10 16:15
 * @Descriptions:
 */
public class MappedSequenceTest
{
    @Test
    public void testSanity() throws IOException {
        Function<Integer, Integer> fn = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input + 2;
            }
        };

        for (int i = 4; i < 5; ++i) {
            List<Integer> vals = new ArrayList<>();
            for (int j = 0; j < i; ++j) {
                vals.add(j);
            }

            SequenceTestHelper.testAll(
                    StringUtils.format("Run %,d: ", i),
                    new MappedSequence<>(Sequences.simple(vals), fn),
                    Lists.transform(vals, fn::apply)
            );
        }
    }
}
