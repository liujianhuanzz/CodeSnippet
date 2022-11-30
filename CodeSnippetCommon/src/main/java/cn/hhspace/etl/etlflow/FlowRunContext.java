package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.utils.JSONMate;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 17:26
 * @Descriptions: 记录一个流程运行到的位置和运行时的数据
 */
public class FlowRunContext implements Cloneable{
    /**
     * 当前待运行的Flow
     */
    @JsonIgnore
    public Flow flow;

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    /**
     * 当前待运行的Node
     */
    public Node node;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * 当前待运行的输入数据
     */
    public JSONArray dataIn;


    public JSONArray getDataIn() {
        return dataIn;
    }

    public void setDataIn(JSONArray dataIn) {
        this.dataIn = dataIn;
    }

    /**
     * 停止原因
     */
    public FlowStopReason stopReason;

    public FlowStopReason getStopReason() {
        return stopReason;
    }

    public void setStopReason(FlowStopReason stopReason) {
        this.stopReason = stopReason;
    }

    /**
     * 停止信息
     */
    public String stopMsg;

    public String getStopMsg() {
        return stopMsg;
    }

    public void setStopMsg(String stopMsg) {
        this.stopMsg = stopMsg;
    }

    public String getFlowId() {
        return flow.getId();
    }

    public String getCurrentNodeId() {
        if (null != node) {
            return node.getId();
        }
        return null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        FlowRunContext newContext = (FlowRunContext) super.clone();
        if (null != dataIn) {
            newContext.setDataIn(JSONMate.deepCody(dataIn));
        } else {
            newContext.setDataIn(null);
        }
        return newContext;
    }
}
