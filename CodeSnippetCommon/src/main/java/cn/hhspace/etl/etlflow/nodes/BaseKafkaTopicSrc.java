package cn.hhspace.etl.etlflow.nodes;

import cn.hhspace.etl.common.Constants;
import cn.hhspace.etl.common.exception.NodeConfigErrorException;
import cn.hhspace.etl.config.KafkaConfig;
import cn.hhspace.etl.etlflow.Flow;
import cn.hhspace.etl.etlflow.FlowRunEngine;
import cn.hhspace.etl.etlflow.FlowStatus;
import cn.hhspace.etl.etlflow.SourceNode;
import cn.hhspace.etl.framework.BeanException;
import cn.hhspace.etl.framework.BeanInventory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaTitle;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 20:32
 * @Descriptions: kafka topic 输入源的基类
 */
public class BaseKafkaTopicSrc extends SourceNode {
    private static final Logger logger = LoggerFactory.getLogger(BaseKafkaTopicSrc.class);
    /**
     * 外部需要配置的属性
     */
    @JsonSchemaFormat("REF_ID_REQUIRED:id&name:/api/v1/serverConfigs?pluginType=KafkaConfig")
    @JsonProperty(required = true)
    @JsonSchemaTitle("Kafka配置")
    public String kafkaConfigId;
    public static final String FALSE = "false";

    /**
     * topic列表
     */
    @JsonIgnore
    protected List<String> topics=new ArrayList<>();

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    /**
     * Kafka配置
     */
    @JsonIgnore
    private KafkaConfig kafkaConfig;

    private String deployGroupId;

    public String getDeployGroupId() {
        return deployGroupId;
    }

    public void setDeployGroupId(String deployGroupId) {
        this.deployGroupId = deployGroupId;
    }

    @Override
    public Collection<String> referPluginIds() {
        return Arrays.asList(kafkaConfigId);
    }
    //内部使用变量
    protected Properties kafkaProperties;

    /**
     *  run()方法需要实现，使用独立线程往阻塞队列放数据，执行完要把myThread设置为null
     *  此方法使用独立线程运行，从数据源取数据，往dataQueue里放数据。
     */
    @Override
    public void run() {
        try {
            do {
                try {

                    KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(kafkaProperties);
                    consumer.subscribe(getTopics());
                    quitFlag = false;
                    do {
                        ConsumerRecords<String, byte[]> records = consumer.poll(100000);
                        int count = records.count();
                        Iterator<ConsumerRecord<String, byte[]>> recordIterator = records.iterator();
                        while(!shutdownFlag && recordIterator.hasNext()) {
                            ConsumerRecord<String, byte[]> record = recordIterator.next();
                            String topic = record.topic();
                            if (record.value() == null) {
                                logger.warn("Topic value is null!!! {}", record);
                                continue;
                            }
                            //
                            JSONObject inData = new JSONObject();
                            inData.put("topic", topic);
                            inData.put("value", record.value());
                            inData.put("value_str", new String(record.value(), StandardCharsets.UTF_8));
                            JSONObject rootData = new JSONObject();
                            rootData.put("_in", inData);
                            JSONArray inArray = new JSONArray();
                            inArray.add(rootData);
                            try {
                                boolean isPutOk = false;
                                while (!shutdownFlag && !isPutOk){
                                    isPutOk = dataQueue.offer(inArray, 100000, TimeUnit.MILLISECONDS);
                                }
                            } catch (InterruptedException ie){
                                shutdownFlag = true;
                                logger.error("KafkaTopicSrc run() InterruptedException", ie);
                                Thread.currentThread().interrupt();
                            }
                        }
                    } while (!shutdownFlag);
                    logger.debug(" BaseKafkaTopicSrc 开始关闭consumer");
                } catch (KafkaException | IllegalStateException e) {
                    logger.warn("KAFKA异常:", e);
                    try {
                        Thread.sleep(Constants.KAFKA_RECONN_WAIT_MS);
                    } catch (InterruptedException ie) {
                        //do nothing
                        Thread.currentThread().interrupt();
                    }
                }
            } while (!shutdownFlag);
            quitFlag = true;
            myThread = null;
            logger.debug("BaseKafkaTopicSrc 成功关闭");
        } catch (Throwable e) {
            logger.error("未知异常", e);
        }
    }

    @Override
    public synchronized void init(BeanInventory beanInventory, Flow flow) {
        super.init(beanInventory, flow);
        if (null == kafkaConfigId) {
            throw new NodeConfigErrorException("节点中kafkaConfigId不能为空,Id=" + this.getId());
        }
        if (!beanInventory.containsBean(kafkaConfigId)) {
            throw new NodeConfigErrorException("找不到kafkaConfigId对应的配置，kafkaConfigId=" + kafkaConfigId);
        }
        kafkaConfig = beanInventory.getBean(kafkaConfigId, KafkaConfig.class);
        kafkaProperties = kafkaConfig.getConsumerProps();

        // 根据状态设置对应的group.id
        if (flow.status == FlowStatus.DEBUGING) {
            //TODO
        } else if (flow.status == FlowStatus.RUNNING) {
            //部署配置了groupid，使用部署的，未配置使用kafka的groupid
            if (StringUtils.isBlank(deployGroupId)){
                deployGroupId = kafkaProperties.getProperty(Constants.KAFKA_CONSUMER_RUN_GROUP_ID);
            }
            kafkaProperties.setProperty(Constants.KAFKA_CONSUMER_GROUP_ID, deployGroupId);
        }
        kafkaProperties.remove(Constants.KAFKA_CONSUMER_RUN_GROUP_ID);
        kafkaProperties.remove(Constants.KAFKA_CONSUMER_DEBUG_GROUP_ID);
        //开启自动提交
        kafkaProperties.setProperty(Constants.KAFKA_ENABLE_AUTO_COMMIT,"true");
    }

    @Override
    public void prepareRunEngine(FlowRunEngine engine) throws BeanException {
        super.prepareRunEngine(engine);
        //检查topics是否有重复
        HashMap<String, Integer> countMap = new HashMap<>();
        for (String topic : getTopics()){
            Integer count = countMap.get(topic);
            countMap.put(topic, (null == count) ? 1 : count + 1);
        }
        for (HashMap.Entry<String, Integer> entry : countMap.entrySet()){
            if (entry.getValue() > 1) {
                throw new NodeConfigErrorException("KafkaTopic节点中topic重复，topic:" + entry.getKey());
            }
        }
    }
}
