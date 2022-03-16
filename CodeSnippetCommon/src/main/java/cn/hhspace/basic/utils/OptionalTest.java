package cn.hhspace.basic.utils;


import com.google.common.base.Optional;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/14 10:45 上午
 * @Descriptions: Optional
 */
public class OptionalTest {
    public static void main(String[] args) {
        Optional<Integer> optional = Optional.of(5);
        System.out.println(optional.isPresent());
        System.out.println(optional.get());
        Optional<Object> absent = Optional.absent();
        System.out.println(absent.orNull());
        System.out.println(absent.or(10));
    }
}
