package cn.hhspace.designpattern.responsibility;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 14:57
 * @Descriptions: 请求处理器
 */
public interface Handler {
    /**
     * 处理请求，返回true成功，返回false拒绝，返回null交给下一个处理
     * @param request
     * @return True/False/Null
     */
    Boolean process(Request request);
}
