package cn.hhspace.designpattern.responsibility;

import java.math.BigDecimal;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 15:04
 * @Descriptions:
 */
public class DirectorHandler implements Handler{

    @Override
    public Boolean process(Request request) {
        if (request.getAmount().compareTo(BigDecimal.valueOf(10000)) > 0) {
            return null;
        }
        return true;
    }
}
