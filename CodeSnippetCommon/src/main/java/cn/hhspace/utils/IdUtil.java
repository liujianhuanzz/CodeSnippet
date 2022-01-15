package cn.hhspace.utils;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/15 1:21 下午
 * @Package: cn.hhspace.utils
 */
public class IdUtil {
    private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 1);

    /**
     * 消息ID
     * @return
     */
    public static String getId() {
        return String.valueOf(idWorker.nextId());
    }
}
