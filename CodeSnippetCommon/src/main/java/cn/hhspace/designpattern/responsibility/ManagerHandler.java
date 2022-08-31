package cn.hhspace.designpattern.responsibility;

import java.math.BigDecimal;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 15:00
 * @Descriptions:
 */
public class ManagerHandler implements Handler{
    @Override
    public Boolean process(Request request) {
        if (request.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            return null;
        }
        return !"bob".equalsIgnoreCase(request.getName());
    }
}
