package cn.hhspace.etl.etlflow;

import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 15:36
 * @Descriptions: 流程图的一条有向边
 */
public class FlowEdge {

    public String from;

    public String to;

    public Map<String, String> uiInfo;

    public FlowEdge(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public FlowEdge() {
        from = null;
        to = null;
        uiInfo = null;
    }
}
