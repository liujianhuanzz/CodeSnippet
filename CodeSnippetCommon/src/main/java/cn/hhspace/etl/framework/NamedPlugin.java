package cn.hhspace.etl.framework;

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInt;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 13:56
 * @Descriptions: 带名字的插件
 */
public interface NamedPlugin extends Plugin{

    @JsonSchemaInject(ints = {@JsonSchemaInt(path = "propertyOrder", value = 2)})
    String getName();

    void setName(String name);
}
