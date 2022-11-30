package cn.hhspace.etl.etlflow.nodes;

import cn.hhspace.etl.etlflow.Flow;
import cn.hhspace.etl.framework.BeanInventory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaBool;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInt;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaString;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 21:48
 * @Descriptions: Kafka输入源
 */
@JsonSchemaTitle("Kafka输入")
public class KafkaTopicSrc extends BaseKafkaTopicSrc{
    public static class TopicItem {

        @JsonSchemaFormat("REF:0&0:/api/v1/flows/kafkaTopics/%kafkaConfigId%")
        @JsonSchemaInject(strings = { @JsonSchemaString(path = "options/inputAttributes/style", value = "width:300px")} ,ints =  { @JsonSchemaInt(path = "minLength", value = 1) })
        @JsonProperty(required = true)
        public String topic;
        @JsonSchemaFormat("checkbox")
        @JsonSchemaTitle("失效")
        public boolean disable;
    }

    @JsonSchemaFormat("table")
    @JsonSchemaTitle("读取的Kafka Topic")
    @JsonSchemaInject(strings = { @JsonSchemaString(path = "uniqueItems", value = "true") },bools = { @JsonSchemaBool(path = "required", value = true)},ints =  { @JsonSchemaInt(path = "minLength", value = 1) })
    private List<TopicItem> topicItemList;

    public List<TopicItem> getTopicItemList() {
        return topicItemList;
    }

    public void setTopicItemList(List<TopicItem> topicItemList) {
        this.topicItemList = topicItemList;
        if (null != topicItemList) {
            this.topics = new ArrayList<>(topicItemList.size());
            for (TopicItem item : topicItemList) {
                if (! item.disable) {
                    topics.add(item.topic);
                }
            }
        } else {
            this.topics = new ArrayList<>();
        }
    }

    @Override
    public synchronized void init (BeanInventory beanInventory, Flow flow)  {
        super.init(beanInventory,flow);
    }
}
