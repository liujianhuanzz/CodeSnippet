package cn.hhspace.designpattern.responsibility;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/8/31 15:07
 * @Descriptions: 调用链
 */
public class HandlerChain {
    private List<Handler> handlers = new ArrayList<>();

    public void addHandler(Handler handler) {
        this.handlers.add(handler);
    }

    public boolean process(Request request) {
        for (Handler handler : handlers) {
            Boolean r = handler.process(request);
            if (r != null) {
                System.out.println(request + " " + (r ? "Approved by " : "Denied by ") + handler.getClass().getSimpleName());
                return r;
            }
        }
        throw new RuntimeException("Could not handle request: " + request);
    }
}
