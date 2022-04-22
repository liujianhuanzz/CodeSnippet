package cn.hhspace.guice.jetty.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/22 8:30 下午
 * @Descriptions:
 */
public class NotFoundRequestException extends RuntimeException{
    public NotFoundRequestException()
    {
        super("Not Found.");
    }

    @JsonCreator
    public NotFoundRequestException(@JsonProperty("errorMessage") String msg)
    {
        super(msg);
    }

    @JsonProperty
    public String NotFoundRequestException()
    {
        return super.getMessage();
    }
}
