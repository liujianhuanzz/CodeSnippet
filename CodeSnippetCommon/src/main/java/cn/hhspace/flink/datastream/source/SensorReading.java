package cn.hhspace.flink.datastream.source;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/11 3:08 下午
 * @Package: cn.hhspace.flink.source
 */
public class SensorReading {

    @JsonProperty
    public String id;

    @JsonProperty
    public long timestamp;

    @JsonProperty
    public double temperature;

    public SensorReading(String id, long timestamp, double temperature) {
        this.id = id;
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "(" + this.id + "," + this.timestamp + "," + this.temperature + ")";
    }
}
