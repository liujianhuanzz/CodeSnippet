package cn.hhspace.etl.etlserver;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 16:33
 * @Descriptions:
 */
public enum DeployServerStatus {
    /**
     * 未知状态
     */
    STATUS_UNKNOWN(-1),
    /**
     * 停止状态
     */
    STATUS_STOP(0),
    /**
     * 运行中
     */
    STATUS_RUNNING(1),
    /**
     * 停止中
     */
    STATUS_STOPPING(2),
    /**
     * 需要重启
     */
    STATUS_NEED_RESTART(3),
    /**
     * 停止数据
     */
    STATUS_STOP_DATA(4),
    /**
     * 启动中
     */
    STATUS_STARTING(5),
    ;

    private int value;

    DeployServerStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
