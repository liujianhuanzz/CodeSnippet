package cn.hhspace.utils;

import org.junit.Test;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 1:26 下午
 * @Package: cn.hhspace.utils
 */
public class IdUtilTest {
    @Test
    public void testId() throws InterruptedException {
        for (int i=0; i < 10; i++) {
            Thread.sleep(2000);
            System.out.println(IdUtil.getId());
        }
    }
}
