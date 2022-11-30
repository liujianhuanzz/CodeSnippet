package cn.hhspace.etl.etlserver;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/22 19:36
 * @Descriptions: 实现这个接口可以返回一个当时的处理记录数
 */
public interface IProcessCount {
    /**
     * 获取处理记录数
     */
    long getProcessCount();
}
