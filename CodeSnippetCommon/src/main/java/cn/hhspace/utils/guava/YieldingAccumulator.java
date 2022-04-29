package cn.hhspace.utils.guava;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/27 16:35
 * @Descriptions:
 */
public abstract class YieldingAccumulator<AccumulatedType, InType> {

    private boolean yielded = false;

    public void yield() {
        yielded = true;
    }

    public boolean yielded() {
        return yielded;
    }

    public void reset() {
        yielded = false;
    }

    public abstract AccumulatedType accumulate(AccumulatedType accumulated, InType in);
}
