package cn.hhspace.etl.utils;

/**
 * @Descriptions:
 * @Author: Jianhuan-LIU
 * @Date: 2021/3/22 5:50 下午
 * @Version: 1.0
 */
public interface NameFilter {
    boolean accept(String fileName);
}
