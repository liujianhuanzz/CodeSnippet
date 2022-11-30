package cn.hhspace.etl.common.exception;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 16:25
 * @Descriptions: 插件相关异常
 */
public class BeanException extends ErrCodeRunException {

    public BeanException(int code) {
        super(code);
    }

    public BeanException(int code, String detail) {
        super(code, detail);
    }

    public BeanException(int code, String detail, Throwable e) {
        super(code, detail, e);
    }

}
