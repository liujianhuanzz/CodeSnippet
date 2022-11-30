package cn.hhspace.jsonschema;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.name.Named;
import com.kjetland.jackson.jsonSchema.annotations.*;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/18 7:45 下午
 * @Package: cn.hhspace.jsonschema
 */
@JsonSchemaTitle("定义了一个测试人")
public class TestPersion {

    @JsonSchemaTitle("测试人的姓名")
    @JsonSchemaDefault("张三")
    @JsonSchemaDescription("这个字段就是用来指定测试人的姓名")
    @JsonSchemaInject(ints = { @JsonSchemaInt(path = "minlength", value = 1) })
    @JsonProperty(required = true)
    @JacksonInject
    @Named("name")
    public String name = "张三";

    @JsonSchemaTitle("测试人的年龄")
    @JsonSchemaDefault("30")
    @JsonSchemaDescription("这个字段就是用来指定测试人的年龄")
    public int age = 30;

    @JsonSchemaFormat("REF_ID_REQUIRED:id&name:/api/v1/serverConfigs?pluginType=KafkaConfig")
    @JsonProperty(required = true)
    @JsonSchemaTitle("测试人ID")
    public String personId;
}
