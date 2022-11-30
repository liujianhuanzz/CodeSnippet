package cn.hhspace.etl.etlserver;

import java.util.Date;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/29 16:45
 * @Descriptions: 加工时的偏移量, 用来计算EPS
 */
public class ProcessingOffset {
    /**
     * 获取偏移量的时刻
     */
    public long monitorTime;
    /**
     * 偏移量
     */
    public long offset;
}
