package cn.hhspace.flink;

import cn.hhspace.flink.cli.CliOptions;
import cn.hhspace.flink.sink.FlinkEsOutputProcessFunction;
import cn.hhspace.flink.sink.FlinkHdfsOutputSinkFatory;
import cn.hhspace.flink.sink.FlinkKafkaProducerFactory;
import cn.hhspace.flink.source.SensorReading;
import cn.hhspace.flink.source.SensorSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: flink write data to kafka es mysql hdfs
 * @Date: 2022/2/18 2:47 下午
 * @Package: cn.hhspace.flink
 */
public class FlinkSinkToKEMH {

    public static void doStart(StreamExecutionEnvironment env, CliOptions options) {
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000L);
        env.setParallelism(1);
        env.enableCheckpointing(5 * 60 * 1000);

        DataStream<SensorReading> readings = env
                .addSource(new SensorSource())
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SensorReading>(Time.seconds(5)) {
                    @Override
                    public long extractTimestamp(SensorReading r) {
                        return r.timestamp;
                    }
                });

        //写入ES
        readings.process(new FlinkEsOutputProcessFunction(options.getEsAddress()));

        //写入kafka
        readings.addSink(FlinkKafkaProducerFactory.getFlinkKafkaProducer1(options.getBrokers(), options.getTopic()));
        //readings.print();

        //写入hdfs
        readings.map(new MapFunction<SensorReading, String>() {
            @Override
            public String map(SensorReading sensorReading) throws Exception {
                return new ObjectMapper().writeValueAsString(sensorReading);
            }
        }).addSink(FlinkHdfsOutputSinkFatory.getSink(options.getHdfsAddress()));

        DataStream<SensorReading> filter = readings.filter(new FilterFunction<SensorReading>() {
            @Override
            public boolean filter(SensorReading sensorReading) throws Exception {
                return sensorReading.temperature > 100;
            }
        });

        filter.addSink(JdbcSink.sink(
                "insert into flink_test_mysql (id, timestamp, temperature) values (?, ? ,?)",
                (stmt, sr) -> {
                    stmt.setString(1, sr.id);
                    stmt.setLong(2, sr.timestamp);
                    stmt.setDouble(3, sr.temperature);
                },
                JdbcExecutionOptions.builder()
                        .withBatchSize(1000)
                        .withBatchIntervalMs(200)
                        .withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(String.format("jdbc:mysql://%s:3306/nice", options.getMysqlAddress()))
                        .withDriverName("com.mysql.jdbc.Driver")
                        .withUsername("root")
                        .withPassword("Hadoop@360")
                        .build()
        ));

        try {
            env.execute("Flink Sink To Kafka/ES/JDBC");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
