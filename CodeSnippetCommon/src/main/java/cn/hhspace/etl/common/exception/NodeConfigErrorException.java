package cn.hhspace.etl.common.exception;

import cn.hhspace.etl.common.ErrorCode;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 20:42
 * @Descriptions: 节点配置类异常
 */
public class NodeConfigErrorException extends ErrCodeRunException {

    public NodeConfigErrorException(String detail) {
        super(ErrorCode.NODE_CONFIG_ERROR, detail);
    }

    public NodeConfigErrorException( String detail, Throwable e) {
        super(ErrorCode.NODE_CONFIG_ERROR, detail, e);
    }
}
