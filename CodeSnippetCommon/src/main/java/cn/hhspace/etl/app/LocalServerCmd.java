package cn.hhspace.etl.app;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/21 17:37
 * @Descriptions: 本地运行的Server的指令体
 */
public class LocalServerCmd {

    public static final String MAGIC_HEAD_STR = "ETL_SERVER_CMD";

    private String magicHead = MAGIC_HEAD_STR;

    private String runInstanceId;

    private ServerCmd cmd;

    private Object result;

    private boolean exceptionFlag;

    private String exceptionMsg;

    public boolean isValid() {
        return MAGIC_HEAD_STR.equals(getMagicHead());
    }

    public String getMagicHead() {
        return magicHead;
    }

    public void setMagicHead(String magicHead) {
        this.magicHead = magicHead;
    }

    public String getRunInstanceId() {
        return runInstanceId;
    }

    public void setRunInstanceId(String runInstanceId) {
        this.runInstanceId = runInstanceId;
    }

    public ServerCmd getCmd() {
        return cmd;
    }

    public void setCmd(ServerCmd cmd) {
        this.cmd = cmd;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isExceptionFlag() {
        return exceptionFlag;
    }

    public void setExceptionFlag(boolean exceptionFlag) {
        this.exceptionFlag = exceptionFlag;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }
}
