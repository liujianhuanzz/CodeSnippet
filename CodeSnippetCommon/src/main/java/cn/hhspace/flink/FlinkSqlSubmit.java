package cn.hhspace.flink;

import cn.hhspace.flink.cli.CliOptions;
import cn.hhspace.flink.cli.sql.SqlCommandParser;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: Flink SQL 提交入口
 * @Date: 2022/2/21 3:38 下午
 * @Package: cn.hhspace.flink
 */
public class FlinkSqlSubmit {

    private TableEnvironment tEnv;
    private String sqlFilePath;

    public FlinkSqlSubmit(CliOptions options) {
        this.sqlFilePath = options.getSqlFile();
    }

    public void run() throws Exception {
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        this.tEnv = TableEnvironment.create(settings);

        //List<String> sql = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource(sqlFilePath).getPath()));
        List<String> sql = new ArrayList<>();
        InputStream asStream = getClass().getClassLoader().getResourceAsStream(sqlFilePath);
        InputStreamReader streamReader = new InputStreamReader(asStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            sql.add(str);
        }
        bufferedReader.close();
        streamReader.close();
        asStream.close();

        List<SqlCommandParser.SqlCommandCall> calls = SqlCommandParser.parse(sql);
        for (SqlCommandParser.SqlCommandCall call : calls) {
            callCommand(call);
        }
        tEnv.execute("Flink SQL Job");
    }

    private void callCommand(SqlCommandParser.SqlCommandCall cmdCall) {
        switch (cmdCall.command) {
            case SET:
                callSet(cmdCall);
                break;
            case CREATE_TABLE:
                callCreateTable(cmdCall);
                break;
            case INSERT_INTO:
                callInsertInto(cmdCall);
                break;
            default:
                throw new RuntimeException("Unsupported command: " + cmdCall.command);
        }
    }

    private void callSet(SqlCommandParser.SqlCommandCall cmdCall) {
        String k = cmdCall.operands[0];
        String v = cmdCall.operands[1];
        tEnv.getConfig().getConfiguration().setString(k, v);
    }

    private void callCreateTable(SqlCommandParser.SqlCommandCall cmdCall) {
        String ddl = cmdCall.operands[0];
        tEnv.sqlUpdate(ddl);
    }

    private void callInsertInto(SqlCommandParser.SqlCommandCall cmdCall) {
        String dml = cmdCall.operands[0];
        tEnv.sqlUpdate(dml);
    }
}
