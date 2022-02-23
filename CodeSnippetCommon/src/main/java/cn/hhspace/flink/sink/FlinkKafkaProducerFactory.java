package cn.hhspace.flink.sink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/11 3:40 下午
 * @Package: cn.hhspace.flink.sink
 */
public class FlinkKafkaProducerFactory {

    public static FlinkKafkaProducer getFlinkKafkaProducer1 (String brokerList, String topic) {
        return new FlinkKafkaProducer(brokerList, topic, new KeyedSerializationSchema() {
            @Override
            public byte[] serializeKey(Object o) {
                return null;
            }

            @Override
            public byte[] serializeValue(Object o) {
                byte[] messageBytes = new byte[0];
                try {
                    messageBytes = new ObjectMapper().writeValueAsBytes(o);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return messageBytes;
            }

            @Override
            public String getTargetTopic(Object o) {
                return topic;
            }
        });
    }

    public static FlinkKafkaProducer getFlinkKafkaProducer2 (String brokerList, String topic) {
        return new FlinkKafkaProducer(topic, new KeyedSerializationSchema<Object>() {
            private static final long serialVersionUID = 548360652554460870L;

            @Override
            public byte[] serializeKey(Object o) {
                return o.toString().getBytes();
            }

            @Override
            public byte[] serializeValue(Object o) {
                byte[] messageBytes = new byte[0];
                try {
                    messageBytes = new ObjectMapper().writeValueAsBytes(o);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return messageBytes;
            }

            @Override
            public String getTargetTopic(Object o) {
                return topic;
            }
        }, getProducerProperties(brokerList), java.util.Optional.of(new CustomKafkaPartitioner()));
    }

    public static Properties getProducerProperties(String brokerList) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("acks","1");
        props.put("retries",3);
        props.put("batch.size",16384);
        return props;
    }

    public static class CustomKafkaPartitioner extends FlinkKafkaPartitioner {

        @Override
        public int partition(Object o, byte[] bytes, byte[] bytes1, String s, int[] ints) {
            return Math.abs(new String(bytes).hashCode() % ints.length);
        }
    }
}
