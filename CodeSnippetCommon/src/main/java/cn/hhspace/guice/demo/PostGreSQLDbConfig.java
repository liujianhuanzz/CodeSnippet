package cn.hhspace.guice.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/8 6:32 下午
 * @Descriptions: PG数据库配置类
 */
public class PostGreSQLDbConfig {

    @JsonProperty
    private String username = "";

    @JsonProperty
    private String password = "";

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
