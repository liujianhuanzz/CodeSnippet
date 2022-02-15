package cn.hhspace.flink.datastream;

import cn.hhspace.flink.datastream.sink.FlinkEsOutputProcessFunction;
import cn.hhspace.flink.datastream.sink.FlinkKafkaProducerFatory;
import cn.hhspace.flink.datastream.source.SensorReading;
import cn.hhspace.flink.datastream.source.SensorSource;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/11 3:23 下午
 * @Package: cn.hhspace.flink
 */
public class FlinkSinkToKafka {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000L);
        env.setParallelism(10);

        DataStream<SensorReading> readings = env
                .addSource(new SensorSource())
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(5)) {
                    @Override
                    public long extractTimestamp(SensorReading r) {
                        return r.timestamp;
                    }
                });

        //写入ES
        readings.process(new FlinkEsOutputProcessFunction());

        //写入kafka
        readings.addSink(FlinkKafkaProducerFatory.getFlinkKafkaProducer());
        //readings.print();

        env.execute("Learning Flink Sink To Kafka");
    }
}
