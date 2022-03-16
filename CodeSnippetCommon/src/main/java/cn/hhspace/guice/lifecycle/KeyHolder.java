package cn.hhspace.guice.lifecycle;

import com.google.inject.Key;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:31 下午
 * @Descriptions:
 */
public class KeyHolder<T> {
    private final Key<? extends T> key;

    public KeyHolder(
            Key<? extends T> key
    )
    {
        this.key = key;
    }

    public Key<? extends T> getKey()
    {
        return key;
    }
}
