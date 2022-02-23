package cn.hhspace.flink.cli;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: 命令行参数选项
 * @Date: 2022/2/18 2:17 下午
 * @Package: cn.hhspace.flink.cli
 */
public class CliOptions {

    private final String topic;
    private final String brokers;
    private final String mode;
    private final String hdfsAddress;
    private final String mysqlAddress;
    private final String esAddress;
    private final String sqlFile;

    public CliOptions(String topic, String brokers, String mode) {
        this(topic, brokers, mode, null, null, null, "sqlFile");
    }


    public CliOptions(String topic, String brokers, String mode, String hdfsAddress, String mysqlAddress, String esAddress, String sqlFile) {
        this.topic = topic;
        this.brokers = brokers;
        this.mode = mode;
        this.hdfsAddress = hdfsAddress;
        this.mysqlAddress = mysqlAddress;
        this.esAddress = esAddress;
        this.sqlFile = sqlFile;
    }

    public String getTopic() {
        return topic;
    }

    public String getBrokers() {
        return brokers;
    }

    public String getMode() {
        return mode;
    }

    public String getHdfsAddress() {
        return hdfsAddress;
    }

    public String getMysqlAddress() {
        return mysqlAddress;
    }

    public String getEsAddress() {
        return esAddress;
    }

    public String getSqlFile() {
        return sqlFile;
    }
}
