package cn.hhspace.jackson.deserialize.code;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 通过代码实现多态反序列化
 * @Date: 2022/1/19 8:00 下午
 * @Package: cn.hhspace.jackson.deserialize.annotation
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name")
public interface Equipment2 {
}
