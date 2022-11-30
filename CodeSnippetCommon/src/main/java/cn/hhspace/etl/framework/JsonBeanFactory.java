package cn.hhspace.etl.framework;

import cn.hhspace.etl.common.ErrorCode;
import cn.hhspace.etl.utils.PackageSearch;
import cn.hhspace.etl.utils.ResourceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一个能够通过JSON配置创建插件的工厂
 */

public class JsonBeanFactory implements BeanFactory, LifeCycleIntf {

    private static final Logger logger = LoggerFactory.getLogger(JsonBeanFactory.class);

    private Map<String, PluginDef> pluginDefMap = new LinkedHashMap<>();

    private Map<String, Plugin> beans = new HashMap<>();
    private ArrayList<String> beanIds = new ArrayList<>();

    private transient ClassLoader classLoader = this.getClass().getClassLoader();

    private ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    /**
     * 返回一个名字和PluginDef的Map
     *
     * @return
     */
    @Override
    public Map<String, PluginDef> getPluginDefs() {
        return pluginDefMap;
    }

    /**
     * 根据插件类型返回插件定义
     *
     * @param pluginType
     * @return
     */
    @Override
    public PluginDef getPluginDef(String pluginType) {
        return pluginDefMap.get(pluginType);
    }

    /**
     * 从类搜索路径中搜索所有的conf/EtlPlugin*.json，完成注册Plugin的步骤
     */
    private static String[] predefinedRegisterFiles={"conf/EtlPlugin_Configs.json", "conf/EtlPlugin_Nodes.json","conf/EtlPlugin_Others.json"};
    public synchronized void registerPlugins() {
        logger.info("通过在classpath里搜索EtlPlugin*.json来注册插件");
        PackageSearch pu = new PackageSearch(true, fileName -> fileName.startsWith("EtlPlugin"));
        String packageName = "conf/";
        try{
            List<String> searchClassNames = pu.getResNamesFromPkg(packageName);
            Set<String> classNames = new HashSet<>();
            classNames.addAll(Arrays.asList(predefinedRegisterFiles));
            //classNames.addAll(searchClassNames);

            if (classNames != null) {
                for (String className: classNames) {
                    logger.info("注册插件，文件名: " + className );
                    String json = ResourceUtil.readClassFileToStr(className);
                    List<PluginDef> newDefs = JSON.parseArray(json, PluginDef.class);
                    for (PluginDef pluginDef: newDefs) {
                        if (pluginDefMap.containsKey(pluginDef.pluginType)) {
                            throw new BeanException(ErrorCode.PLUGIN_EXISTS, pluginDef.pluginType);
                        }
                        pluginDefMap.put(pluginDef.pluginType, pluginDef);
                        mapper.registerSubtypes(new NamedType(getBeanClass(pluginDef.pluginType), pluginDef.pluginType));
                    }
                }
            }
        } catch (IOException e) {
            throw new BeanException(ErrorCode.BEAN_IO_ERROR, "", e);
        }
    }

    /**
     * 根据插件类型返回实际类
     *
     * @param pluginType
     * @return
     */
    @Override
    public Class<?> getBeanClass(String pluginType) {
        PluginDef pluginDef = getPluginDef(pluginType);

        if (pluginDef == null) {
            return null;
        }

        String className = pluginDef.java_class;
        try {
            if (null == classLoader) {
                classLoader = this.getClass().getClassLoader();
            }
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e){
            String errDetail = "插件类型对应的Java类无法获取， pluginType：" + pluginType + ", className:" + className;
            throw new BeanException(ErrorCode.BEAN_NOTFOUND_CLASS, errDetail);
        }
    }

    /**
     * 根据名称返回JsonSchema字符串
     *
     * @param id
     * @return
     */
    @Override
    public String getJsonSchema(String id) {
        Plugin bean = getBean(id);
        if (bean == null) {
            throw new BeanException(ErrorCode.BAD_BEAN_ID);
        }

        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        JsonNode schema = schemaGen.generateJsonSchema(bean.getClass());

        try {
            String schemaJson = mapper.writeValueAsString(schema);
            return schemaJson;
        } catch (JsonProcessingException e) {
            throw new BeanException(ErrorCode.BEAN_WRITE_JSON_ERROR, id, e);
        }
    }

    /**
     * 根据JSON配置创建所有插件，并登记到Factory中，这里的JSON配置是一个插件配置的json列表
     *
     * @param jsonStr
     */
    @Override
    public synchronized void createBeans(String jsonStr) {
        JSONArray array = JSON.parseArray(jsonStr);
        for (int i = 0; i < array.size(); i++) {
            JSONObject o = array.getJSONObject(i);

            String id = o.getString("id");
            if (id == null) {
                throw new BeanException(ErrorCode.BEAN_REQUIRE_ID);
            }

            String pluginType = o.getString("pluginType");
            if (pluginType == null) {
                throw new BeanException(ErrorCode.BEAN_REQUIRE_PLUGINTYPE);
            }

            createBean(id, pluginType, o.toString(), true);
        }
    }

    /**
     * 根据JSON配置刷新所有插件，并登记到Factory中，这里的JSON配置是一个插件配置的json列表
     *
     * @param jsonStr
     */
    @Override
    public synchronized void refressBeans(String jsonStr) {
        JSONArray array = JSON.parseArray(jsonStr);
        for (int i = 0; i < array.size(); i++) {
            JSONObject o = array.getJSONObject(i);

            String id = o.getString("id");
            if (id == null) {
                throw new BeanException(ErrorCode.BEAN_REQUIRE_ID);
            }

            String pluginType = o.getString("pluginType");
            if (pluginType == null) {
                throw new BeanException(ErrorCode.BEAN_REQUIRE_PLUGINTYPE);
            }

            refressBean(id, pluginType, o.toString());
        }
    }

    /**
     * 刷新插件的配置，并重启插件
     *
     * @param id
     * @param type
     * @param json
     * @return
     */
    @Override
    public Plugin refressBean(String id, String type, String json) {
        return null;
    }

    @Override
    public Plugin refressBean(Plugin plugin) throws Exception {
        String id = plugin.getId();
        boolean beanRunning = false;
        boolean hasOldBean = false;

        if (containsBean(id)) {
            hasOldBean = true;
            if (plugin instanceof LifeCycleIntf) {
                beanRunning = ((LifeCycleIntf) plugin).isRunning();
                if (beanRunning) {
                    ((LifeCycleIntf) plugin).stop();
                }
                ((LifeCycleIntf) plugin).destroy();
            }
        }

        if (!beanRunning && plugin instanceof LifeCycleIntf) {
            ((LifeCycleIntf) plugin).init(this);
            ((LifeCycleIntf) plugin).start();
        }

        beans.put(id, plugin);
        if (!hasOldBean) {
            beanIds.add(id);
        }

        return plugin;
    }

    /**
     * 根据名称、类型、Json配置创建插件，并登记到Factory中,创建的插件名不能已经存在
     * @param id
     * @param pluginType
     * @param json
     * @param saveFlag
     * @return
     */
    protected synchronized Plugin createBean(String id, String pluginType, String json, boolean saveFlag) {
        if (saveFlag && containsBean(id)) {
            throw new BeanException(ErrorCode.BEAN_ID_EXISTS, id);
        }

        try {
            Class clazz = this.getBeanClass(pluginType);
            if (clazz == null) {
                throw new BeanException(ErrorCode.UNREGISTERED_BEAN, pluginType);
            }

            Plugin o = (Plugin) mapper.readValue(json, clazz);
            if (saveFlag) {
                beans.put(id, o);
                beanIds.add(id);
            }
            return o;
        } catch (InvalidTypeIdException ite) {
            throw new BeanException(ErrorCode.BEAN_TYPEID_ERROR, "插件类型： " + ite.getTypeId() + " 不能识别，可能是插件未注册。", ite);
        } catch (JsonParseException jpe) {
            throw new BeanException(ErrorCode.BEAN_BAD_JSON, "JSON串错误。" , jpe);
        } catch (IOException jme) {
            throw new BeanException(ErrorCode.BEAN_READ_JSON_ERROR, "JSON串映射错误", jme);
        }
    }

    /**
     * 根据名称、类型、Json配置创建插件，并登记到Factory中,创建的插件名不能已经存在
     *
     * @param id
     * @param type
     * @param json
     * @return
     */
    @Override
    public synchronized Plugin createBean(String id, String type, String json) {
        return createBean(id, type, json, true);
    }

    /**
     * 根据名称、类型、Json配置创建插件，但不登记到Factory中
     *
     * @param id
     * @param type
     * @param json
     * @return
     */
    @Override
    public synchronized Plugin createBeanWithoutSave(String id, String type, String json) {
        return createBean(id, type, json, false);
    }

    /**
     * 获取插件的全部配置
     *
     * @return
     */
    @Override
    public synchronized String getJsonConfig() {
        List beanList = new ArrayList();

        for (String id: beanIds) {
            beanList.add(getBean(id));
        }

        try {
            String str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(beanList);
            return (str);
        } catch (JsonProcessingException e) {
            throw new BeanException(ErrorCode.BEAN_WRITE_JSON_ERROR, "", e);
        }
    }

    /**
     * 获取某个插件的配置
     *
     * @param id
     * @return
     */
    @Override
    public synchronized String getJsonConfig(String id) {
        Plugin bean = getBean(id);
        if (bean == null) {
            throw new BeanException(ErrorCode.BAD_BEAN_ID, id);
        }

        try {
            String str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
            return str;
        } catch (JsonProcessingException e) {
            throw new BeanException(ErrorCode.BEAN_WRITE_JSON_ERROR, id, e);
        }
    }

    /**
     * 获取插件的schema
     *
     * @param type
     * @return
     */
    @Override
    public String getPluginJsonSchema(String type) {
        return null;
    }

    /**
     * 将所有的bean开始初始化
     */
    @Override
    public synchronized void initBeans() {
        for (String id: beanIds) {
            Plugin bean = beans.get(id);

            try {
                if (bean instanceof Plugin && bean instanceof LifeCycleIntf) {
                    logger.info("init plugin: [{}]", id);
                }

                if (bean instanceof LifeCycleIntf) {
                    ((LifeCycleIntf) bean).init(this);
                }
            } catch (Exception e) {
                logger.error("初始化插件对象失败， id: " + id, e);
            }
        }
    }

    /**
     * 初始化插件，一般在这里完成其他插件的引用和装配工作，从BeanInventory中查找插件
     *
     * @param inv
     * @throws Exception
     */
    @Override
    public synchronized void init(BeanInventory inv) throws Exception {
        //工厂的初始化不等于管理的bean的初始化，bean初始化只要注册bean，装载bean就够了
    }

    /**
     * 销毁工厂管理的插件
     */
    @Override
    public synchronized void destroy() {
        for (int i = beanIds.size() - 1; i >= 0; --i){
            String id = beanIds.get(i);
            Object bean = beans.get(id);
            try {
                if (bean instanceof Plugin && bean instanceof LifeCycleIntf) {
                    logger.info("destroy plugin: [{}]", id);
                }

                if (bean instanceof LifeCycleIntf) {
                    ((LifeCycleIntf) bean).destroy();
                }
            } catch (Exception ex) {
                logger.error("销毁插件对象失败, id" + id, ex);
            }
        }
    }

    /**
     * 启动工厂管理的插件
     *
     * @throws Exception
     */
    @Override
    public synchronized void start() throws Exception {
        for (String id: beanIds) {
            Plugin bean = beans.get(id);

            try {
                if (bean instanceof Plugin && bean instanceof LifeCycleIntf) {
                    logger.info("start plugin: [{}]", id);
                }

                if (bean instanceof LifeCycleIntf) {
                    ((LifeCycleIntf) bean).start();
                }
            } catch (Exception e) {
                logger.error("启动插件对象失败， id: " + id, e);
            }
        }
    }

    /**
     * 停止插件
     *
     * @throws Exception
     */
    @Override
    public synchronized void stop() throws Exception {
        for (int i = beanIds.size() - 1; i >= 0; --i){
            String id = beanIds.get(i);
            Object bean = beans.get(id);
            try {
                if (bean instanceof Plugin && bean instanceof LifeCycleIntf) {
                    logger.info("stop plugin: [{}]", id);
                }

                if (bean instanceof LifeCycleIntf) {
                    ((LifeCycleIntf) bean).stop();
                }
            } catch (Exception ex) {
                logger.error("停止插件对象失败, id" + id, ex);
            }
        }
    }

    /**
     * 返回插件是否正在运行中
     *
     * @return
     */
    @Override
    public boolean isRunning() {
        return false;
    }

    /**
     * 校验json转plugin格式是否成功
     *
     * @param id
     * @param pluginType
     * @param json
     * @return
     */
    @Override
    public Plugin checkPluginBeanFormat(String id, String pluginType, String json) {
        return null;
    }

    /**
     * 插件创建后根据名称返回插件对象
     *
     * @param id
     * @return
     * @throws BeanException
     */
    @Override
    public synchronized Plugin getBean(String id) {
        Plugin bean = beans.get(id);
        if (bean == null) {
            throw new BeanException(ErrorCode.BAD_BEAN_ID, "id=" + id);
        }
        return bean;
    }

    /**
     * 插件创建后根据名称返回插件对象
     *
     * @param id
     * @param requiredType
     * @return
     */
    @Override
    public synchronized  <T> T getBean(String id, Class<T> requiredType) {
        Plugin bean = beans.get(id);
        if (bean == null) {
            throw new BeanException(ErrorCode.BAD_BEAN_ID, "id=" + id);
        }

        if (!requiredType.isInstance(bean)) {
            throw new BeanException(ErrorCode.BEAN_CAST_ERROR, "获取插件对象时类型转换错误， id=" + id + ", requiredType=" + requiredType);
        }
        return (T) bean;
    }

    /**
     * 检测是否包含某个名称的插件
     *
     * @param id
     * @return
     */
    @Override
    public synchronized boolean containsBean(String id) {
        return beans.containsKey(id);
    }

    /**
     * 根据一个类型，返回工厂拥有的插件中所有属于此类型的插件名
     * 这个方法主要是在配置界面选择引用对象时使用
     *
     * @param pluginType
     * @return
     */
    @Override
    public List<String> getBeanIdsForType(String pluginType) {
        Class clazz = getBeanClass(pluginType);
        if (null == clazz) {
            throw new BeanException(ErrorCode.UNREGISTERED_BEAN, pluginType);
        }
        List<String> ids = new ArrayList<>();
        for (Plugin bean : beans.values()) {
            if (clazz.isInstance(bean)){
                ids.add(bean.getId());
            }
        }
        return ids;
    }

    /**
     * 根据一个分类，返回工厂拥有的插件中所有属于此分类的插件名
     * 这个方法主要是在配置界面选择引用对象时使用
     *
     * @param category
     * @return
     */
    @Override
    public List<String> getBeanIdsForCategory(String category) {
        return null;
    }

    /**
     * 返回工厂拥有的全部插件
     *
     * @return
     */
    @Override
    public List<String> getBeanIds() {
        return null;
    }

    /**
     * 从对象仓库中移除一个bean
     *
     * @param id
     */
    @Override
    public void removeBean(String id) {

    }

    /**
     * 从对象仓库中移除所有beans
     */
    @Override
    public void removeAll() {

    }

    /**
     * 添加一个插件到对象仓库中，不保存
     *
     * @param plugin
     */
    @Override
    public synchronized void addBean(Plugin plugin) {
        String id = plugin.getId();
        if (beans.containsKey(id)) {
            throw new BeanException(ErrorCode.BEAN_ID_EXISTS, id);
        }

        beans.put(id, plugin);
        beanIds.add(id);
    }

    /**
     * 更新一个插件到对象仓库中，不保存
     *
     * @param plugin
     */
    @Override
    public synchronized void updateBean(Plugin plugin) {
        String id = plugin.getId();
        if(!beans.containsKey(id)) {
            beans.put(id, plugin);
            beanIds.add(id);
        }
    }

    /**
     * 保存某个对象到外部存储。对于数据库方式，saveBean就是直接保存，文件方式这个方法并不直接报错，要等saveAllBeans才报存到文件
     *
     * @param id
     */
    @Override
    public void saveBean(String id) {
        //JsonBeanFactory的方式不需要保存外部
        //do nothing
    }

    /**
     * 保存全部对象到外部存储
     */
    @Override
    public void saveAllBean() {
        //do nothing
    }

    /**
     * 是否被其他bean引用，没有引用返回null，否则返回一个字符串，列出所有的ID
     *
     * @param beanId
     * @return
     */
    @Override
    public String referedIds(String beanId) {
        return null;
    }

    @Override
    public String referedIdsWithoutName(String beanId) {
        return null;
    }

    /**
     * 返回所有依赖给定ID的某一类Bean
     *
     * @param beanId
     * @param pluginType
     * @return
     */
    @Override
    public List<String> referedIdsForType(String beanId, String pluginType) {
        return null;
    }
}
