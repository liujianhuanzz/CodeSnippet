package cn.hhspace.etl.framework;

import java.util.Map;

/**
 * Created by liujianhuan on 2019/12/9
 */
public class PluginDef {
    public String pluginType;
    public String category;
    public String catalog;
    public String java_class;
    public String hint;
    public String version;

    /**
     * 前端展示这个插件所需的界面信息，包括icon css等，这是一个json串，由前端进行解释
     */
    public Map<String, String> uiInfo;
}
