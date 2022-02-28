package cn.hhspace.spi;

import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 搜素接口的数据库搜索实现
 * @Date: 2022/2/28 2:09 下午
 * @Package: cn.hhspace.spi
 */
public class DatabaseSearch implements Search{
    @Override
    public List<String> searchDoc(String keyword) {
        System.out.println("数据库搜索：" + keyword);
        return null;
    }
}
