package cn.hhspace.guice.lifecycle;

import java.util.IllegalFormatException;
import java.util.Locale;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/16 4:17 下午
 * @Descriptions:
 */
public class ISE extends IllegalStateException{
    public ISE(String formatText, Object... arguments)
    {
        super(nonStrictFormat(formatText, arguments));
    }

    public ISE(Throwable cause, String formatText, Object... arguments)
    {
        super(nonStrictFormat(formatText, arguments), cause);
    }

    private static String nonStrictFormat(String message, Object... formatArgs)
    {
        if (formatArgs == null || formatArgs.length == 0) {
            return message;
        }
        try {
            return String.format(Locale.ENGLISH, message, formatArgs);
        }
        catch (IllegalFormatException e) {
            StringBuilder bob = new StringBuilder(message);
            for (Object formatArg : formatArgs) {
                bob.append("; ").append(formatArg);
            }
            return bob.toString();
        }
    }
}
