package cn.hhspace.flink;

import cn.hhspace.flink.cli.CliOptions;
import cn.hhspace.flink.sink.FlinkKafkaProducerFactory;
import cn.hhspace.flink.source.UserBehaviorProducerSource;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:生产用户行为数据
 * @Date: 2022/2/18 4:31 下午
 * @Package: cn.hhspace.flink
 */
public class FlinkProduceUserBehavior {

    public static void doStart(StreamExecutionEnvironment env, CliOptions options) throws Exception {
        env.setParallelism(6);

        DataStream<JSONObject> ts = env.addSource(new UserBehaviorProducerSource())
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<JSONObject>(Time.seconds(5)) {
                    @Override
                    public long extractTimestamp(JSONObject s) {
                        return s.getLong("ts");
                    }
                });

        //写入kafka
        ts.addSink(FlinkKafkaProducerFactory.getFlinkKafkaProducer2(options.getBrokers(), options.getTopic()));
        //ts.print();

        env.execute("Produce User Behavior Data by Flink");
    }

}
