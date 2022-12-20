package cn.hhspace.jackson.deserialize.annotation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/12/20 15:36
 * @Descriptions:
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = BigHero.class, name = "Big"),
        @JsonSubTypes.Type(value = SmallHero.class, name = "Small"),
})
public interface Hero {
}
