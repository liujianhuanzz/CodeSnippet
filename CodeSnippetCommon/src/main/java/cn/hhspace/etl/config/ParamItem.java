package cn.hhspace.etl.config;

import java.io.Serializable;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 15:45
 * @Descriptions: 参数对象
 */
public class ParamItem implements Cloneable, Serializable {

    public String key;
    public String value;
    public boolean inherent;

    public ParamItem() {}

    public ParamItem(String key, String value) {
        this.key = key;
        this.value = value;
        this.inherent = false;
    }

    public ParamItem(String key, String value, boolean inherent) {
        this.key = key;
        this.value = value;
        this.inherent = inherent;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == key || null == obj || !(obj instanceof ParamItem)) {
            return false;
        } else {
            return key.equals(((ParamItem) obj).key);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Getter and Setter
     */

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isInherent() {
        return inherent;
    }

    public void setInherent(boolean inherent) {
        this.inherent = inherent;
    }
}
