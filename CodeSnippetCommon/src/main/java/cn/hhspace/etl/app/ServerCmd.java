package cn.hhspace.etl.app;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 17:34
 * @Descriptions: 运行需要一些控制指令来控制启停等
 */
public enum ServerCmd {
    /**
     * 停止命令
     */
    STOP,
    /**
     * 状态命令
     */
    STATUS,
    /**
     * 获取偏移量命令
     */
    GETOFFSET,
    /**
     * 获取EPS命令
     */
    GETEPS
}
