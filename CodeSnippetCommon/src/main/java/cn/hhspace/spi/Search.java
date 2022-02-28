package cn.hhspace.spi;

import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 一个搜索的接口，被具体的实现类实现
 * @Date: 2022/2/28 2:03 下午
 * @Package: cn.hhspace.spi
 */
public interface Search {
    public List<String> searchDoc(String keyword);
}
