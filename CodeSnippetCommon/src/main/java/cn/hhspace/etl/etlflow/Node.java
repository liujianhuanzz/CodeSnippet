package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.common.ErrorCode;
import cn.hhspace.etl.framework.BeanException;
import cn.hhspace.etl.framework.BeanInventory;
import cn.hhspace.etl.framework.NamedPlugin;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 14:35
 * @Descriptions:
 */
@JsonPropertyOrder({"id", "pluginType"})
public abstract class Node implements NamedPlugin, AutoCloseable {
    /**
     * 配置内容
     */
    protected String id;

    protected String pluginType;

    private String name;

    public Map<String, String> uiInfo;

    /******* 私有属性 ***********/
    /**
     * 流程中的下一个节点
     */
    protected Node next = null;
    /**
     * 节点所在流程
     */
    protected Flow flow;

    /**
     * 初始化节点，获取插件配置、初始化内部变量等
     */
    public synchronized void init(BeanInventory beanInventory, Flow flow) {
        this.flow = flow;
        if (null != flow) {
            String nextNodeId = flow.findNextNodeId(this.id);
            this.next = flow.findNode(nextNodeId);
            if (null != next && !(next instanceof ProcessNode)) {
                throw new BeanException(ErrorCode.NODE_BAD_NEXT_NODE, this.getId());
            }
        }
    }

    /**
     * 运行前准备RunEngine, 一般是用来准备连接池等，运行时相关的变量绑定在RunEngine上，不可放在Node上
     */
    public void prepareRunEngine(FlowRunEngine engine) throws BeanException {
        //该方法由具体的子类来实现
    }

    /**
     * 执行节点
     * 一般数据加工节点只需要重载process方法
     * 控制节点执行还会影响流程控制，需要重载这个方法
     */
    public abstract void run(FlowRunEngine engine) throws InterruptedException;

    @Override
    public void close() throws Exception {}

    /**
     * Getter and Setter
     */
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<String> referPluginIds() {
        return Collections.EMPTY_LIST;
    }
}
