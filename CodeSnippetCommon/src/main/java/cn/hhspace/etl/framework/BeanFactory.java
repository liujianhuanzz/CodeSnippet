package cn.hhspace.etl.framework;

import java.util.Map;

/**
 * 插件类工厂接口
 * Created by liujianhuan on 2019/12/9
 */
public interface BeanFactory extends LifeCycleIntf, BeanInventory {

    /**
     * 返回一个名字和PluginDef的Map
     * @return
     */
    Map<String, PluginDef> getPluginDefs();

    /**
     * 根据插件类型返回插件定义
     * @param pluginType
     * @return
     */
    PluginDef getPluginDef(String pluginType);

    /**
     * 根据插件类型返回实际类
     * @param pluginType
     * @return
     */
    Class<?> getBeanClass(String pluginType);

    /**
     * 根据名称返回JsonSchema字符串
     * @param id
     * @return
     */
    String getJsonSchema(String id);

    /**
     * 根据JSON配置创建所有插件，并登记到Factory中，这里的JSON配置是一个插件配置的json列表
     * @param jsonStr
     */
    void createBeans(String jsonStr);

    /**
     * 根据JSON配置刷新所有插件，并登记到Factory中，这里的JSON配置是一个插件配置的json列表
     * @param jsonStr
     */
    void refressBeans(String jsonStr);

    /**
     * 刷新插件的配置，并重启插件
     * @param id
     * @param type
     * @param json
     * @return
     */
    Plugin refressBean(String id, String type, String json);

    Plugin refressBean(Plugin plugin) throws Exception;
    /**
     * 根据名称、类型、Json配置创建插件，并登记到Factory中,创建的插件名不能已经存在
     * @param id
     * @param type
     * @param json
     * @return
     */
    Plugin createBean(String id, String type, String json);

    /**
     * 根据名称、类型、Json配置创建插件，但不登记到Factory中
     * @param id
     * @param type
     * @param json
     * @return
     */
    Plugin createBeanWithoutSave(String id, String type, String json);

    /**
     * 获取插件的全部配置
     * @return
     */
    String getJsonConfig();

    /**
     * 获取某个插件的配置
     * @param id
     * @return
     */
    String getJsonConfig(String id);

    /**
     * 获取插件的schema
     * @param type
     * @return
     */
    String getPluginJsonSchema(String type);

    /**
     * 将所有的bean开始初始化
     */
    void initBeans();

    /**
     * 销毁工厂
     */
    void destroy();

    /**
     * 校验json转plugin格式是否成功
     * @param id
     * @param pluginType
     * @param json
     * @return
     */
    Plugin checkPluginBeanFormat(String id, String pluginType, String json);
}
