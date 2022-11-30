package cn.hhspace.etl.env.yarn;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/24 15:57
 * @Descriptions:
 */
public class Log4jPropertyHelper {
    public static void updateLog4jConfiguration(Class<?> targetClass,
                                                String log4jPath) throws Exception {
        Properties customProperties = new Properties();
        FileInputStream fs = null;
        InputStream is = null;
        try {
            fs = new FileInputStream(log4jPath);
            is = targetClass.getResourceAsStream("/log4j.properties");
            customProperties.load(fs);
            Properties originalProperties = new Properties();
            originalProperties.load(is);
            for (Map.Entry<Object, Object> entry : customProperties.entrySet()) {
                originalProperties.setProperty(entry.getKey().toString(), entry
                        .getValue().toString());
            }
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(originalProperties);
        }finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fs);
        }
    }
}
