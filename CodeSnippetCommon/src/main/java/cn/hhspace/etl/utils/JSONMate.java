package cn.hhspace.etl.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 17:42
 * @Descriptions: JSON工具
 */
public class JSONMate {

    public static JSONArray deepCody(JSONArray array) {
        JSONArray newArray = new JSONArray();
        for (int i=0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONObject) {
                value = deepCody((JSONObject)value, 0);
            } else if (value instanceof JSONArray) {
                value = deepCody((JSONArray) value);
            }
            newArray.add(value);
        }
        return newArray;
    }

    /**
     * JSONObject的深拷贝
     * @param obj
     * @param level
     * @return JSONObject
     */
    private static JSONObject deepCody(Map<String, Object> obj, int level) {
        if (level > 8) {
            throw new RuntimeException("JSONObject递归层次太多");
        }

        JSONObject newObject = new JSONObject();
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                value = deepCody((Map<String, Object>) value, level + 1);
            } else if (value instanceof JSONArray) {
                value = deepCody((JSONArray) value);
            }
            newObject.put(key, value);
        }
        return newObject;
    }

    private static JSONObject deepCody(Map<String, Object> obj) {
        return deepCody(obj, 0);
    }

    /**
     * 返回JSONObject的对应字段，field可以用.表示多级对象
     * @param json
     * @param fieldName
     * @return Object
     */
    public static Object getField(JSONObject json, String fieldName) {
        if (null == fieldName || fieldName.trim().equals("")) {
            return json;
        }

        Map node = json;
        int idx1 = 0;
        int idx2 = fieldName.indexOf(".");
        String partName;
        while (idx2 >= 0) {
            partName = fieldName.substring(idx1, idx2);
            Object co = node.get(partName);
            if (null == co || !(co instanceof Map)) {
                return null;
            } else {
                node = (Map) co;
            }
            idx1 = idx2 + 1;
            idx2 = fieldName.indexOf(".", idx1);
        }
        partName = fieldName.substring(idx1);
        return node.get(partName);
    }
}
