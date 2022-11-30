package cn.hhspace.etl.framework;

import cn.hhspace.etl.common.exception.ErrCodeRunException;

/**
 * Created by liujianhuan on 2019/12/9
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
