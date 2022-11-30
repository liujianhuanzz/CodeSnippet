package cn.hhspace.etl.config;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Map;
import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/17 20:25
 * @Descriptions: Kafka配置类
 */
public class KafkaConfig extends Config{

    private static final long serialVersionUID = 5212638796959270634L;

    @JsonIgnore
    public Properties getConsumerProps(){
        Properties properties = new Properties();
        Map<String, String> paramMap = getParamMap();
        if (null != paramMap) {
            properties.putAll(paramMap);
        }
        return properties;
    }
}
