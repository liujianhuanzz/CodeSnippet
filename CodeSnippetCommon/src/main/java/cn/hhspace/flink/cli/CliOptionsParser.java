package cn.hhspace.flink.cli;

import org.apache.commons.cli.*;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 命令行解析
 * @Date: 2022/2/18 2:28 下午
 * @Package: cn.hhspace.flink.cli
 */
public class CliOptionsParser {

    public static final Option OPTION_TOPIC = Option
            .builder("t")
            .required(false)
            .longOpt("topic")
            .numberOfArgs(1)
            .argName("topic name")
            .desc("kafka topic name")
            .build();

    public static final Option OPTION_BROKERS = Option
            .builder("b")
            .required(false)
            .longOpt("brokers")
            .numberOfArgs(1)
            .argName("brokers list")
            .desc("kafka brokers list")
            .build();

    public static final Option OPTION_MODE = Option
            .builder("m")
            .required(false)
            .longOpt("mode")
            .numberOfArgs(1)
            .argName("running module")
            .desc("running module")
            .build();

    public static final Option OPTION_HDFS_HOST = Option
            .builder("hh")
            .required(false)
            .longOpt("hdfs_host")
            .numberOfArgs(1)
            .argName("hdfs address")
            .desc("hdfs address")
            .build();

    public static final Option OPTION_ES_HOST = Option
            .builder("eh")
            .required(false)
            .longOpt("es_host")
            .numberOfArgs(1)
            .argName("es address")
            .desc("es address")
            .build();

    public static final Option OPTION_DB_HOST = Option
            .builder("dh")
            .required(false)
            .longOpt("db_host")
            .numberOfArgs(1)
            .argName("db hostname")
            .desc("db hostname")
            .build();

    public static final Option OPTION_SQL_FILE = Option
            .builder("f")
            .required(false)
            .longOpt("sql_file")
            .numberOfArgs(1)
            .argName("sql file")
            .desc("sql file")
            .build();

    public static Options getClientOptions(Options options) {
        options.addOption(OPTION_TOPIC);
        options.addOption(OPTION_BROKERS);
        options.addOption(OPTION_MODE);
        options.addOption(OPTION_HDFS_HOST);
        options.addOption(OPTION_DB_HOST);
        options.addOption(OPTION_ES_HOST);
        options.addOption(OPTION_SQL_FILE);

        return options;
    }

    public static final Options CLIENT_OPTIONS = getClientOptions(new Options());

    public static CliOptions parseClient(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("params error");
        }

        try {
            DefaultParser parser = new DefaultParser();
            CommandLine line = parser.parse(CLIENT_OPTIONS, args, true);
            return new CliOptions(
                    line.getOptionValue(CliOptionsParser.OPTION_TOPIC.getOpt(),""),
                    line.getOptionValue(CliOptionsParser.OPTION_BROKERS.getOpt(),""),
                    line.getOptionValue(CliOptionsParser.OPTION_MODE.getOpt(),""),
                    line.getOptionValue(CliOptionsParser.OPTION_HDFS_HOST.getOpt(), null),
                    line.getOptionValue(CliOptionsParser.OPTION_DB_HOST.getOpt(), null),
                    line.getOptionValue(CliOptionsParser.OPTION_ES_HOST.getOpt(), null),
                    line.getOptionValue(CliOptionsParser.OPTION_SQL_FILE.getOpt(), ""));
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
