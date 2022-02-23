package cn.hhspace.flink;

import cn.hhspace.flink.cli.CliOptions;
import cn.hhspace.flink.cli.CliOptionsParser;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/11 3:23 下午
 * @Package: cn.hhspace.flink
 */
public class FlinkTestMain {

    public static final String SINK_STORAGE_MODE = "sink";
    public static final String PRODUCE_USER_BEHAVIOR = "produce_user_behavior";
    public static final String FLINK_SQL = "flink_sql";

    public static void main(String[] args) throws Exception {

        CliOptions options = CliOptionsParser.parseClient(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        if (options.getMode().equals(SINK_STORAGE_MODE)){
            FlinkSinkToKEMH.doStart(env, options);
        } else if (options.getMode().equals(PRODUCE_USER_BEHAVIOR)) {
            FlinkProduceUserBehavior.doStart(env, options);
        } else if (options.getMode().equals(FLINK_SQL)){
            new FlinkSqlSubmit(options).run();
        }

    }
}
