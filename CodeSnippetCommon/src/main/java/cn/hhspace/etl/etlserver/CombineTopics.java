package cn.hhspace.etl.etlserver;

import cn.hhspace.etl.etlflow.Flow;
import cn.hhspace.etl.etlflow.SourceNode;
import cn.hhspace.etl.etlflow.nodes.BaseKafkaTopicSrc;
import cn.hhspace.etl.framework.BeanInventory;
import org.apache.commons.collections.map.HashedMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/22 17:52
 * @Descriptions: 把所有具有KafkaTopicSrc源节点的Flow合并成一个Topics列表
 */
public class CombineTopics implements Serializable {

    private List<String> topics;

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    private Map<String, String> topicFlowIdMap;

    public Map<String, String> getTopicFlowIdMap() {
        return topicFlowIdMap;
    }

    public void setTopicFlowIdMap(Map<String, String> topicFlowIdMap) {
        this.topicFlowIdMap = topicFlowIdMap;
    }

    public CombineTopics(BeanInventory beanInventory, String kafkaCfgId, Collection<String> flowIds) throws Exception {
        topics = new ArrayList<>();
        topicFlowIdMap = new HashedMap();

        for (String flowId : flowIds) {
            if (!beanInventory.containsBean(flowId)) {
                throw new Exception("CombineTopics找不到流程，flowId:" + flowId);
            }

            Flow flow = beanInventory.getBean(flowId, Flow.class);
            SourceNode sourceNode = flow.findSourceNode();
            //TODO 子流程则continue
            if (!(sourceNode instanceof BaseKafkaTopicSrc)) {
                throw new Exception("发布的flow中有不是从Kafka为源节点的。flowId=" + flowId);
            }

            BaseKafkaTopicSrc kafkaNode = (BaseKafkaTopicSrc) sourceNode;
            if (!kafkaNode.kafkaConfigId.equals(kafkaCfgId)) {
                throw new Exception("发布的flow中KafKa配置和服务器kafKa配置不一样。flowId=" + flowId);
            }

            for (String topic : kafkaNode.getTopics()) {
                topics.add(topic);
                if (topicFlowIdMap.containsKey(topic)) {
                    throw new Exception("发布的flow中Topic有重复，flowId=" + flowId + ",topic=" + topic);
                }
                topicFlowIdMap.put(topic, flowId);
            }
        }
    }
}
