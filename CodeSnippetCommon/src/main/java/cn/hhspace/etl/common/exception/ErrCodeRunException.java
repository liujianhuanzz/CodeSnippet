package cn.hhspace.etl.common.exception;

import cn.hhspace.etl.common.ErrorCodeUtils;

/**
 * @Descriptions: 运行时异常
 * @Author: Jianhuan-LIU
 * @Date: 2021/3/22 2:28 下午
 * @Version: 1.0
 */
public class ErrCodeRunException extends RuntimeException {
    private int code;
    private String detail;

    public ErrCodeRunException(int code) {
        super(ErrorCodeUtils.getErrorMsg(code));
        this.code = code;
    }

    public ErrCodeRunException(int code, String detail) {
        super(ErrorCodeUtils.getErrorMsg(code) + " detail: " + detail);
        this.code = code;
        this.detail = detail;
    }

    public ErrCodeRunException(int code, String detail, Throwable e) {
        super(ErrorCodeUtils.getErrorMsg(code) + " detail: " + detail, e, true, true);
        this.code = code;
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "ErrCodeRunException{" +
                "code=" + code +
                ", detail='" + detail + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
