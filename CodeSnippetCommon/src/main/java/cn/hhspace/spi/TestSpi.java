package cn.hhspace.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 测试spi
 * @Date: 2022/2/28 2:12 下午
 * @Package: cn.hhspace.spi
 */
public class TestSpi {
    public static void main(String[] args) {
        ServiceLoader<Search> serviceLoader = ServiceLoader.load(Search.class);
        Iterator<Search> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            Search search = iterator.next();
            search.searchDoc("Hello World");
        }
    }
}
