package cn.hhspace.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: JSON工具类
 * @Date: 2021/12/6 5:13 下午
 * @Package: cn.hhspace.utils
 */
public class JSONUtils {

    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JSONUtils() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setTimeZone(TimeZone.getDefault());
    }

    /**
     * Object转JSON串
     * @param object
     * @return JSON String
     */
    public static String toJson(Object object) {
        try {
            return JSON.toJSONString(object, false);
        } catch (Exception e) {
            logger.error("object to json exception!", e);
        }

        return null;
    }

    /**
     * JSON串转对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, Class<T> clazz) {

        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            logger.error("parse object exception!", e);
        }

        return null;
    }

    /**
     * 字节数组转对象
     * @param src
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(byte[] src, Class<T> clazz) {

        if (src == null) {
            return null;
        }

        String json = new String(src, UTF_8);
        return parseObject(json, clazz);
    }

    /**
     * Json to List
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {

        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }

        try {
            return JSONArray.parseArray(json, clazz);
        } catch (Exception e) {
            logger.error("JSONArray.parseArray exception!", e);
        }

        return new ArrayList<>();
    }

    /**
     * object to json string
     * @param object
     * @return
     */
    public static String toJsonString(Object object) {
        try {
            return JSON.toJSONString(object, false);
        } catch (Exception e) {
            throw new RuntimeException("Object json deserialization exception.", e);
        }
    }

    /**
     * 将object序列化为json字节数组
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] toJsonByteArray(T obj) {

        if (obj == null) {
            return null;
        }

        String json = "";
        try {
            json = toJsonString(obj);
        } catch (Exception e) {
            logger.error("json serialize exception.", e);
        }

        return json.getBytes(UTF_8);
    }

    /**
     * 检查Json对象是否有效
     * @param json
     * @return
     */
    public static boolean checkJsonValid(String json) {

        if (StringUtils.isEmpty(json)) {
            return false;
        }

        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            logger.error("check json object valid exception!", e);
        }

        return false;
    }

    /**
     * json to map
     * @param json
     * @return
     */
    public static Map<String, String> toMap(String json) {

        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return JSON.parseObject(json, new TypeReference<HashMap<String, String>>(){});
        } catch (Exception e) {
            logger.error("json to map exception!", e);
        }

        return null;
    }
}
