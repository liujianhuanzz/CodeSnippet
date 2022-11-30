package cn.hhspace.etl.framework;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInt;

import java.util.Collection;

/**
 * 插件的基类。插件不一定要继承这个类，只要有id和type属性就可以。
 * 插件是由外部工厂产生，注入属性的。所以应该有一个无参数的公共构造函数。
 * 一个插件要引用其他插件时，一般是用一个属性记录外部I插件D，构造完后，调用一个init() 函数进行装配。可以参见LifeCycleIntf接口
 * 为了插件能够生成jsonSchema，需要对jsonSchema做一点约定。
 * jsonSchema 采用 V4 draft
 * 可以使用下面几个Tag
 *  @JsonIgnore
 *  @JsonSchemaDefault("/usr/local1")
 *  @JsonSchemaDescription("the directory of application installed")
 *  @JsonSchemaTitle("Application Dir")
 *  @JsonProperty(value = "appDir",required = true)
 *  @JsonSchemaFormat("utc-millisec")
 * JsonSchema中只定义了7中类型："array", "boolean", "integer", "null", "number", "object", "string" ,"table"
 *  其中JsonSchemaFormat在规范里未定义，我们自行定义几种：
 *  utc-millisec,Date,Time,password,PATH,FILE,REF_ID:id&name:url ,REF:id&name:url <pluginType>
 *  utc-millisec 是dateTime类型，返回一个整形
 *  Date,Time都返回字符串
 *  PATH、FILE是目录或文件选择列表,value中填文件名
 *  REF_ID:id&name:/url
 *  REF_ID_REQUIRED:id&name:/url
 *  REF:fieldName&fieldName:/api/v1/dataStructures/fields/%dataStructId%
 *  REF:0&0:/api/v1/kafkaTopics/%kafkaConfigId%
 * Created by liujianhuan on 2019/12/9
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "pluginType", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonPropertyOrder({"id", "pluginType"})
public interface Plugin {
    @JsonProperty(value = "id", required = true)
    @JsonSchemaInject(ints = {@JsonSchemaInt(path = "propertyOrder", value = 1)})
    String getId();

    void setId(String id);

    String getPluginType();

    void setPluginType(String pluginType);

    /**
     * 这个插件引用了哪些别的pluginId
     * @return
     */
    Collection<String> referPluginIds();
}
