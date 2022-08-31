package cn.hhspace.designpattern.responsibility;

import java.math.BigDecimal;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 14:54
 * @Descriptions: 请求对象，在责任链上传递
 */
public class Request {
    private String name;
    private BigDecimal amount;

    public Request(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
