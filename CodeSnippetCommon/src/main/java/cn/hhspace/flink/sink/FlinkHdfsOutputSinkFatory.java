package cn.hhspace.flink.sink;

import cn.hhspace.flink.sink.gzip.GzipBulkStringWriterFactory;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/16 6:50 下午
 * @Package: cn.hhspace.flink.sink
 */
public class FlinkHdfsOutputSinkFatory {

    public static StreamingFileSink<String> getSink(String hdfsAddress) {
        StreamingFileSink<String> s = StreamingFileSink.forBulkFormat(
                    new Path(String.format("hdfs://%s:9000/tmp/flink_test_hdfs", hdfsAddress)),
                    new GzipBulkStringWriterFactory<String>()
        ).withBucketAssigner(
                new BucketAssigner<String, String>() {
                    JSONObject sensorReading = null;
                    @Override
                    public String getBucketId(String s, Context context) {
                        try {
                            sensorReading = new ObjectMapper().readValue(s, JSONObject.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
                        return "dt=" + dateTimeFormatter.format(Instant.ofEpochMilli(sensorReading.getLong("timestamp")));
                    }

                    @Override
                    public SimpleVersionedSerializer<String> getSerializer() {
                        return SimpleVersionedStringSerializer.INSTANCE;
                    }
                }
        ).withRollingPolicy(OnCheckpointRollingPolicy.build())
        .withBucketCheckInterval(TimeUnit.SECONDS.toMillis(60)).build();

        return s;
    }
}
