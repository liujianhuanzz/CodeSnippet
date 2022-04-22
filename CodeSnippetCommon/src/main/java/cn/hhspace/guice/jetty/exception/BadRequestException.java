package cn.hhspace.guice.jetty.exception;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 7:38 下午
 * @Descriptions: BadRequestException
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String msg)
    {
        super(msg);
    }
}
