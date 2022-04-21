package cn.hhspace.guice.common;

import cn.hhspace.utils.StringUtils;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 4:09 下午
 * @Descriptions: Runtime Exception
 */
public class RE extends RuntimeException {
    public RE(String formatText, Object... arguments)
    {
        super(StringUtils.nonStrictFormat(formatText, arguments));
    }

    public RE(Throwable cause, String formatText, Object... arguments)
    {
        super(StringUtils.nonStrictFormat(formatText, arguments), cause);
    }

    public RE(Throwable cause)
    {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
