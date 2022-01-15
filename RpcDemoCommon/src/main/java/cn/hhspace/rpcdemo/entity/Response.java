package cn.hhspace.rpcdemo.entity;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/1/14 2:18 下午
 * @Package: cn.hhspace.rpcdemo.entity
 */
public class Response {

    private String requestId;

    private int code;

    private String errorMsg;

    private Object data;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
