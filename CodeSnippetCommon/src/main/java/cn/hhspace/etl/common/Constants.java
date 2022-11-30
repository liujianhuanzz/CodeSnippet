package cn.hhspace.etl.common;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 19:30
 * @Descriptions: 配置常量定义
 */
public class Constants {

    public Constants() {}

    /**
     * kafka
     */
    public static final String KAFKA_BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static final String KAFKA_ZOOKEEPER_LIST = "zookeeper.list";
    public static final String KAFKA_CONSUMER_GROUP_ID = "group.id";
    public static final String KAFKA_CONSUMER_RUN_GROUP_ID = "run.group.id";
    public static final String KAFKA_CONSUMER_DEBUG_GROUP_ID = "debug.group.id";
    public static final String KAFKA_AUTO_OFFSET_RESET = "auto.offset.reset";
    public static final String KAFKA_ENABLE_AUTO_COMMIT = "enable.auto.commit";
    public static final int KAFKA_RECONN_WAIT_MS=30000;
    public static final String DEPLOY_CONSUMER_GROUP_ID = "deployGroupId";
    public static final int KAFKA_CLOSE_WAIT_SECONDS=2;
    /**
     * 部署服务停止时等待的最大时间，秒
     */
    public static final int SERVER_WAIT_MAX_TIME = 60;
}
