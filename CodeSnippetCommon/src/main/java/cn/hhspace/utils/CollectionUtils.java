package cn.hhspace.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 集合工具
 * @Date: 2021/11/23 2:45 下午
 * @Package: cn.hhspace.utils
 */
public class CollectionUtils {

    private CollectionUtils() {
        throw new IllegalStateException("CollectionUtils class");
    }

    /**
     * 集合判空
     * @param collection
     * @return true/false
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 集合判非空
     * @param collection
     * @return true/false
     */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static <T> Collection<T> subtract(Set<T> a, Set<T> b) {
        return org.apache.commons.collections4.CollectionUtils.subtract(a, b);
    }

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * String to map
     *
     * @param str       string
     * @param separator separator
     * @return string to map
     */
    public static Map<String, String> stringToMap(String str, String separator) {
        return stringToMap(str, separator, "");
    }

    /**
     * String to map
     *
     * @param str       string
     * @param separator separator
     * @param keyPrefix prefix
     * @return string to map
     */
    public static Map<String, String> stringToMap(String str, String separator, String keyPrefix) {
        Map<String, String> emptyMap = new HashMap<>(0);
        if (StringUtils.isEmpty(str)) {
            return emptyMap;
        }
        if (StringUtils.isEmpty(separator)) {
            return emptyMap;
        }
        String[] strings = str.split(separator);
        int initialCapacity = (int)(strings.length / DEFAULT_LOAD_FACTOR) + 1;
        Map<String, String> map = new HashMap<>(initialCapacity);
        for (int i = 0; i < strings.length; i++) {
            String[] strArray = strings[i].split("=");
            if (strArray.length != 2) {
                return emptyMap;
            }
            //strArray[0] KEY  strArray[1] VALUE
            if (StringUtils.isEmpty(keyPrefix)) {
                map.put(strArray[0], strArray[1]);
            } else {
                map.put(keyPrefix + strArray[0], strArray[1]);
            }
        }
        return map;
    }
}
