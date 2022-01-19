package cn.hhspace.jackson.deserialize.annotation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 通过注解实现多态反序列化
 * @Date: 2022/1/19 8:00 下午
 * @Package: cn.hhspace.jackson.deserialize.annotation
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = Daedalus.class, name = "Daedalus"),
        @JsonSubTypes.Type(value = HugeDrink.class, name = "Huge drink"),
        @JsonSubTypes.Type(value = StarWand.class, name = "Star wand"),
})
public interface Equipment1 {
}
