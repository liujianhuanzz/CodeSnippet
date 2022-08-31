package cn.hhspace.designpattern.responsibility;

import java.math.BigDecimal;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 15:11
 * @Descriptions: 客户端调用
 */
public class Client {
    public static void main(String[] args) {
        HandlerChain chain = new HandlerChain();
        chain.addHandler(new ManagerHandler());
        chain.addHandler(new DirectorHandler());
        chain.addHandler(new CEOHandler());

        chain.process(new Request("Bob", new BigDecimal("123.45")));
        chain.process(new Request("Alice", new BigDecimal("1234.56")));
        chain.process(new Request("Bill", new BigDecimal("12345.67")));
        chain.process(new Request("John", new BigDecimal("123456.78")));
    }
}
