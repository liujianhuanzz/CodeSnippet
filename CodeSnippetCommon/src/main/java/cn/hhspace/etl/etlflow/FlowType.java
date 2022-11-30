package cn.hhspace.etl.etlflow;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 16:01
 * @Descriptions: 流程类型
 */
public enum FlowType {
    /**
     * 主流程
     */
    MAIN_FLOW_TYPE(0),
    /**
     * 子流程
     */
    SUB_FLOW_TYPE(1);

    private int code;

    FlowType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
