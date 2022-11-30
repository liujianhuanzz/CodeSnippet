package cn.hhspace.etl.config;

import cn.hhspace.etl.framework.BeanFactory;
import cn.hhspace.etl.framework.NamedPlugin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 19:49
 * @Descriptions: 配置类插件的基类
 */
@JsonPropertyOrder({"id", "pluginType", "name", "reserved", "creator", "ctime", "modifier", "mtime", "isPublic", "remark", "params"})
public class Config  implements NamedPlugin, Cloneable, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    protected String id;
    protected String name;

    protected boolean reserved;

    protected String creator;
    protected String modifier;

    protected long ctime;
    protected long mtime;

    protected boolean isPublic;
    protected List<ParamItem> params;


    protected long deployedTime;
    protected String remark;

    protected String pluginType;

    @JsonIgnore
    private Map<String, String> paramMap;

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    @JsonIgnore
    public Properties getParamProps() {
        Properties props = new Properties();
        if (paramMap != null) {
            props.putAll(paramMap);
        }
        return props;
    }

    /**
     * 测试此配置的连通性，每个具体配置应该重载此方法
     */
    public boolean testConnection() throws Exception {
        throw new UnsupportedOperationException("本配置不支持连通性测试功能");
    }

    /**
     * 测试此配置的连通性，每个具体配置应该重载此方法
     */
    public boolean testConnection(BeanFactory beanFactory) throws Exception {
        return testConnection();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getPluginType() {
        return pluginType;
    }

    @Override
    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    @Override
    public Collection<String> referPluginIds() {
        return Collections.EMPTY_LIST;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public long getMtime() {
        return mtime;
    }

    public void setMtime(long mtime) {
        this.mtime = mtime;
    }

    public long getDeployedTime() {
        return deployedTime;
    }

    public void setDeployedTime(long deployedTime) {
        this.deployedTime = deployedTime;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<ParamItem> getParams() {
        return params;
    }

    public void setParams(List<ParamItem> params) {
        this.params = params;
        paramMap = new HashMap<>();
        for (ParamItem item : params) {
            paramMap.put(item.key, item.value);
        }
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public Config clone() throws CloneNotSupportedException {
        Config newObj = (Config) super.clone();
        List<ParamItem> newParams = new ArrayList<>();
        for (ParamItem item : params) {
            newParams.add((ParamItem) item.clone());
        }
        newObj.setParams(newParams);
        return newObj;
    }
}
