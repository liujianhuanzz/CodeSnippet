package cn.hhspace.etl.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

/**
 * @Descriptions: 读取资源文件的工具类
 * @Author: Jianhuan-LIU
 * @Date: 2021/4/3 12:50 下午
 * @Version: 1.0
 */
public class ResourceUtil {

    private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    private static URLClassLoader urlClassLoader = null;

    public static String readClassFileToStr(String fileName) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try(InputStream is = loader.getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IOException("在classpath下找不到指定文件: " + fileName);
            }
            return readStreamToStr(is);
        }
    }

    public static String readStreamToStr(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            int len = 0;
            while ((len = isr.read()) >= 0) {
                sb.append((char) len);
            }
            is.close();
        }
        return sb.toString();
    }

    public static void writeStrToFile(String fileName, String str) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        try(OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            osw.write(str);
        }
        fos.close();
    }

    public static ClassLoader getClassLoader() {
        if (urlClassLoader != null) {
            return urlClassLoader;
        } else {
            return classLoader;
        }
    }
}
