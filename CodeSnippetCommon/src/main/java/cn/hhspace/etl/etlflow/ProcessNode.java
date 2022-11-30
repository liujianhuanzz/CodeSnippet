package cn.hhspace.etl.etlflow;

import com.alibaba.fastjson.JSONArray;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 19:08
 * @Descriptions: 数据加工插件的基类
 */
public abstract class ProcessNode extends Node {
    /**
     * 数据加工的方法，如果不需要控制程序流向的节点只需要重载该方法
     */
    public JSONArray process(FlowRunEngine engine, JSONArray in) {
        return in;
    }

    /**
     * 执行节点
     * 一般数据加工节点只需要重载process方法
     * 如果节点为控制节点还需要控制流程方向，需要重载这个方法
     */
    @Override
    public void run(FlowRunEngine engine) throws InterruptedException {
        FlowRunContext context = engine.getCurrentContext();
        JSONArray out = process(engine, context.dataIn);
        context.setNode(this.next);
        context.setDataIn(out);
        engine.setCurrentContext(context);
    }
}
