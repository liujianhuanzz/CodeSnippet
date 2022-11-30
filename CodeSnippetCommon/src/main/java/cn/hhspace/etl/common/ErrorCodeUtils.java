package cn.hhspace.etl.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @Descriptions:
 * @Author: Jianhuan-LIU
 * @Date: 2021/3/22 2:30 下午
 * @Version: 1.0
 */
public class ErrorCodeUtils {

        private static final Logger logger = LoggerFactory.getLogger(ErrorCodeUtils.class);

        private static Properties prop;


        static {
            try {
                if (null == prop)
                {
                    prop = new Properties();
                    prop.load(ErrorCodeUtils.class.getClassLoader().getResourceAsStream("errorcode.properties"));
                    prop.load(ErrorCodeUtils.class.getClassLoader().getResourceAsStream("errorcode2.properties"));

                }
            } catch(IOException e) {
                logger.error("read errorcode file failed.", e);
            }
        }

        private ErrorCodeUtils() {

        }

        public static String getErrorMsg(int errorCode) {
            if (null == prop) {
                logger.error("prop is null.");
                return null;
            }

            return prop.getProperty(String.valueOf(errorCode));
        }
}
