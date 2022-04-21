package cn.hhspace.guice.common;

import cn.hhspace.utils.StringUtils;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/4/21 3:43 下午
 * @Descriptions: Unsupported Operation Exception
 */
public class UOE extends UnsupportedOperationException {
    public UOE(String formatText, Object... arguments)
    {
        super(StringUtils.nonStrictFormat(formatText, arguments));
    }
}
