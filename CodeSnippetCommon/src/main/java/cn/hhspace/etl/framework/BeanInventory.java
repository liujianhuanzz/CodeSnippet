package cn.hhspace.etl.framework;

import java.io.Serializable;
import java.util.List;

/**
 * 提供一个对象存储目录接口
 * 这个接口的主要功能是根据id返回一个对象
 * 具体的实现，可以通过数据库、内存里的Map、文件等
 * Created by liujianhuan on 2019/12/9
 */
public interface BeanInventory extends Serializable {
    /**
     * 插件创建后根据名称返回插件对象
     * @param id
     * @return
     * @throws BeanException
     */
    Plugin getBean(String id);

    /**
     * 插件创建后根据名称返回插件对象
     * @param id
     * @param requiredType
     * @param <T>
     * @return
     */
    <T> T getBean(String id, Class<T> requiredType);

    /**
     * 检测是否包含某个名称的插件
     * @param id
     * @return
     */
    boolean containsBean(String id);

    /**
     * 根据一个类型，返回工厂拥有的插件中所有属于此类型的插件名
     * 这个方法主要是在配置界面选择引用对象时使用
     * @param pluginType
     * @return
     */
    List<String> getBeanIdsForType(String pluginType);

    /**
     * 根据一个分类，返回工厂拥有的插件中所有属于此分类的插件名
     * 这个方法主要是在配置界面选择引用对象时使用
     * @param category
     * @return
     */
    List<String> getBeanIdsForCategory(String category);

    /**
     * 返回工厂拥有的全部插件
     * @return
     */
    List<String> getBeanIds();

    /**
     * 从对象仓库中移除一个bean
     * @param id
     */
    void removeBean(String id);

    /**
     * 从对象仓库中移除所有beans
     */
    void removeAll();

    /**
     * 添加一个插件到对象仓库中，不保存
     * @param plugin
     */
    void addBean(Plugin plugin);

    /**
     * 更新一个插件到对象仓库中，不保存
     * @param plugin
     */
    void updateBean(Plugin plugin);

    /**
     * 保存某个对象到外部存储。对于数据库方式，saveBean就是直接保存，文件方式这个方法并不直接报错，要等saveAllBeans才报存到文件
     * @param id
     */
    void saveBean(String id);

    /**
     * 保存全部对象到外部存储
     */
    void saveAllBean();

    /**
     * 是否被其他bean引用，没有引用返回null，否则返回一个字符串，列出所有的ID
     * @param beanId
     * @return
     */
    String referedIds(String beanId);
    String referedIdsWithoutName(String beanId);

    /**
     * 返回所有依赖给定ID的某一类Bean
     * @param beanId
     * @param pluginType
     * @return
     */
    List<String> referedIdsForType(String beanId,String pluginType);

    //PredefineConfig getPredefineCfg();
}
