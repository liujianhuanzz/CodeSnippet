package cn.hhspace.spi;

import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 搜索接口的文件实现
 * @Date: 2022/2/28 2:05 下午
 * @Package: cn.hhspace.spi
 */
public class FileSearch implements Search{
    @Override
    public List<String> searchDoc(String keyword) {
        System.out.println("文件搜索：" + keyword);
        return null;
    }
}
