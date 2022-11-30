package cn.hhspace.etl.etlflow;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 17:32
 * @Descriptions: Flow的停止枚举
 */
public enum FlowStopReason {
    TRAP_BEGIN("TRAP_BEGIN", 0),
    ASSIGNED_CURR_NODE("ASSIGNED_CURR_NODE", 1),
    TRAP_BREAKPOINT("TRAP_BREAKPOINT", 2),
    TRAP_EXCEPTION("TRAP_EXCEPTION", 3),
    TRAP_SINLE_STEP("TRAP_SINLE_STEP", 4),
    TRAP_INTERRUPT("TRAP_INTERRUPT", 5),
    RUNNING("RUNNING", 6)
    ;

    private String reasonLabel;
    private int reasonCode;

    FlowStopReason(String reasonLabel, int reasonCode) {
        this.reasonLabel = reasonLabel;
        this.reasonCode = reasonCode;
    }

    public String getReasonLabel() {
        return reasonLabel;
    }

    public void setReasonLabel(String reasonLabel) {
        this.reasonLabel = reasonLabel;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }
}
