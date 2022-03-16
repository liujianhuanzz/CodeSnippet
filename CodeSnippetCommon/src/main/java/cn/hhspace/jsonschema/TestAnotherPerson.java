package cn.hhspace.jsonschema;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/18 7:45 下午
 * @Package: cn.hhspace.jsonschema
 */

public class TestAnotherPerson {

    @JsonProperty
    String name;

    @JsonCreator
    @Inject
    public TestAnotherPerson(@JacksonInject @Named("name") @JsonProperty String name) {
        this.name = name;
    }
}
