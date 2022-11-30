package cn.hhspace.etl.etlflow;

import cn.hhspace.etl.common.ErrorCode;
import cn.hhspace.etl.config.ParamItem;
import cn.hhspace.etl.framework.BeanException;
import cn.hhspace.etl.framework.BeanInventory;
import cn.hhspace.etl.framework.NamedPlugin;
import cn.hutool.core.io.IoUtil;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 15:32
 * @Descriptions: 流程对象
 */
@JsonPropertyOrder({"id", "pluginType", "name", "creator", "ctime", "modifier", "mtime", "status", "type", "nodes", "edges"})
public class Flow implements NamedPlugin, AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(Flow.class);

    /**
     * 对外的公共属性
     */
    String id;

    private String name;

    public String creator;

    public String modifier;

    public long ctime;

    public long mtime;

    public long deployedTime;

    public String pluginType;

    /**
     * 流程状态
     */
    public FlowStatus status = FlowStatus.NORMAL;

    /**
     * Flow中的节点列表
     */
    public List<Node> nodes;

    /**
     * 一个流程的有向边列表
     */
    public List<FlowEdge> edges;
    /**
     * 流程的属性列表
     */
    public List<ParamItem> attrs;
    /**
     * 流程类型
     */
    public FlowType type = FlowType.MAIN_FLOW_TYPE;
    /**
     * 是否公开
     */
    public Boolean isPublic;

    /******** Flow的私有属性区域 ********/
    @JsonIgnore
    private boolean hasInited = false;

    @JsonIgnore
    private boolean hasDestroyed = false;

    @JsonIgnore
    private Set<String> subFlowIdSets;

    private Set<Flow> subFlows;

    @JsonIgnore
    private SourceNode sourceNode;

    @JsonIgnore
    private ProcessNode firstProcessNode=null;

    @JsonIgnore
    private Map<String, String> nextNodeIdMap;

    @JsonIgnore
    private Map<String, Node> nodeMap;

    private Collection<OutputNode> outputNodes;

    public Node findNode(String nodeId) {
        return nodeMap.get(nodeId);
    }

    public String findNextNodeId(String nodeId) {
        return nextNodeIdMap.get(nodeId);
    }

    /**
     * 初始化插件
     */
    public synchronized void init(BeanInventory beanInventory) {
        if (!hasInited) {
            nodeMap = new HashMap<>();
            outputNodes = new ArrayList<>();
            if (null == nodes) {
                nodes = new ArrayList<>();
            }
            //找出来所有的点
            for (Node node : nodes) {
                nodeMap.put(node.getId(), node);
                if (node instanceof SourceNode) {
                    sourceNode = (SourceNode) node;
                } else if (node instanceof OutputNode) {
                    outputNodes.add((OutputNode) node);
                }
            }
            //找出来node的next
            nextNodeIdMap = new HashMap<>();
            if (null == edges) {
                edges = new ArrayList<>();
            }
            for (FlowEdge d : edges){
                nextNodeIdMap.put(d.from, d.to);
            }
            //初始化节点
            for (Node node : nodes){
                node.init(beanInventory, this);
            }
            //找到所有的子流程
            subFlowIdSets = findSubFlowIds(beanInventory);
            subFlows = new HashSet<>();
            for (String subFlowId : subFlowIdSets) {
                if (!beanInventory.containsBean(subFlowId)) {
                    throw new BeanException(ErrorCode.FLOW_NOT_FOUND, subFlowId);
                }
                Flow subFlow = beanInventory.getBean(subFlowId, Flow.class);
                subFlow.init(beanInventory);
                subFlows.add(subFlow);
            }
            //找到第一个处理节点
            firstProcessNode = (ProcessNode) findNode(findNextNodeId(sourceNode.getId()));

            hasInited = true;
            hasDestroyed = false;
        }
    }

    @JsonIgnore
    public long getOutputRecs() {
        if (null == outputNodes) {
            return 0;
        }
        long count = 0;
        for (OutputNode outputNode : outputNodes) {
            count += outputNode.getOutputCounts();
        }
        return count;
    }

    private Set<String> findSubFlowIds(BeanInventory beanInventory) {
        Set<String> subFlowIds = new HashSet<>();
        findSubFlowSets(beanInventory, subFlowIds);
        return subFlowIds;
    }

    private void findSubFlowSets(BeanInventory beanInventory, Set<String> subFlowIds) {
        //TODO
    }

    public synchronized void prepareRunEngine(FlowRunEngine engine) {
        for (Node node : nodes){
            node.prepareRunEngine(engine);
        }
        for (String subFlowId : subFlowIdSets){
            if (!engine.getBeanInventory().containsBean(subFlowId)) {
                throw new BeanException(ErrorCode.FLOW_NOT_FOUND, subFlowId);
            }
            Flow subFlow = engine.getBeanInventory().getBean(subFlowId, Flow.class);
            subFlow.prepareRunEngine(engine);
        }
    }

    /**
     * 在Flow没有init前通过该方法来获取SourceNode，init后直接通过getSourceNode获取
     */
    public SourceNode findSourceNode(){
        for (Node node : nodes) {
            if (node instanceof SourceNode) {
                return (SourceNode) node;
            }
        }
        return null;
    }


    /******* 方法重写 **********/
    @Override
    public Collection<String> referPluginIds() {
        Collection<String> referIds = new HashSet<>();
        if (null != nodes) {
            for (Node node : nodes) {
                referIds.addAll(node.referPluginIds());
            }
            referIds.remove("");
        }
        return referIds;
    }

    /**
     * 销毁流程
     * @throws Exception
     */
    @Override
    public synchronized void close() throws Exception {
        if (!hasDestroyed) {
            if (null != subFlows) {
                for (Flow subFlow : subFlows) {
                    IoUtil.close(subFlow);
                }
            }

            for (Node node : nodes) {
                IoUtil.close(node);
            }

            hasDestroyed = true;
            hasInited = false;
            subFlows = null;
            subFlowIdSets = null;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Flow)) {
            return false;
        } else {
            Flow otherFlow = (Flow) obj;
            return id.equals(otherFlow.id);
        }
    }

    /******* Getter and Setter *************/
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
    public String getPluginType() {
        return pluginType;
    }

    @Override
    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    public FlowType getType() {
        return type;
    }

    public void setType(FlowType type) {
        this.type = type;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public SourceNode getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(SourceNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Map<String, String> getNextNodeIdMap() {
        return nextNodeIdMap;
    }

    public void setNextNodeIdMap(Map<String, String> nextNodeIdMap) {
        this.nextNodeIdMap = nextNodeIdMap;
    }

    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public ProcessNode getFirstProcessNode() {
        return firstProcessNode;
    }

    public void setFirstProcessNode(ProcessNode firstProcessNode) {
        this.firstProcessNode = firstProcessNode;
    }
}
