package cn.hhspace.designpattern.responsibility;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 15:06
 * @Descriptions:
 */
public class CEOHandler implements Handler{

    @Override
    public Boolean process(Request request) {
        return true;
    }
}
