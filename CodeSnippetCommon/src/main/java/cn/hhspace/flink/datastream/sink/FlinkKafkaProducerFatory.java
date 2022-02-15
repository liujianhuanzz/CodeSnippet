package cn.hhspace.flink.datastream.sink;

import cn.hhspace.flink.datastream.source.SensorReading;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/11 3:40 下午
 * @Package: cn.hhspace.flink.sink
 */
public class FlinkKafkaProducerFatory {

    private static String brokerList = "hostname:9092";

    private static String topic = "test_flink_kafka";

    public static FlinkKafkaProducer getFlinkKafkaProducer () {
        return new FlinkKafkaProducer<SensorReading>(brokerList, topic, new KeyedSerializationSchema<SensorReading>() {
            @Override
            public byte[] serializeKey(SensorReading sensorReading) {
                return null;
            }

            @Override
            public byte[] serializeValue(SensorReading sensorReading) {
                byte[] messageBytes = new byte[0];
                try {
                    messageBytes = new ObjectMapper().writeValueAsBytes(sensorReading);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return messageBytes;
            }

            @Override
            public String getTargetTopic(SensorReading sensorReading) {
                return topic;
            }
        });
    }
}
