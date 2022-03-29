package cn.hhspace.flink;

import cn.hhspace.flink.cli.CliOptions;
import cn.hhspace.flink.cli.sql.SqlCommandParser;
import cn.hhspace.flink.udf.ReflectionUtils;
import com.google.common.base.Preconditions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.SqlParserException;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.module.hive.HiveModule;
import org.apache.flink.table.shaded.org.reflections.Reflections;
import org.apache.flink.table.shaded.org.reflections.scanners.SubTypesScanner;
import org.apache.flink.table.shaded.org.reflections.util.ClasspathHelper;
import org.apache.flink.table.shaded.org.reflections.util.ConfigurationBuilder;
import org.apache.flink.table.shaded.org.reflections.util.FilterBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions: Flink SQL 提交入口
 * @Date: 2022/2/21 3:38 下午
 * @Package: cn.hhspace.flink
 */
public class FlinkSqlSubmit {

    private TableEnvironment tEnv;
    private StreamExecutionEnvironment bsEnv;
    private String sqlFilePath;
    private HiveCatalog hiveCatalog;

    public FlinkSqlSubmit(CliOptions options) {
        this.sqlFilePath = options.getSqlFile();
    }

    public void run() throws Exception {
        bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        this.tEnv = StreamTableEnvironment.create(bsEnv, settings);

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
            case UDF:
                callRegisterUDF(cmdCall);
                break;
            case CACHE_FILE:
                callRegisterCachedFile(cmdCall);
                break;
            case SET:
                callSet(cmdCall);
                break;
            case SET_HIVE_OR_MAPRED:
                callSetHiveVars(cmdCall);
                break;
            case CREATE_TABLE:
                callCreateTable(cmdCall);
                break;
            case CREATE_FUNCTION:
                callCreateFunction(cmdCall);
                break;
            case INSERT_INTO:
                callInsertInto(cmdCall);
                break;
            case USE_CATALOG:
            case USE_DATABASE:
                callUse(cmdCall);
                break;
            case DROP_TABLE:
                dropTable(cmdCall);
                break;
            case ENABLE_HIVE:
                enableHive();
                break;
            default:
                throw new RuntimeException("Unsupported command: " + cmdCall.command);
        }
    }

    private void enableHive(){
        tEnv.getConfig().setSqlDialect(SqlDialect.HIVE);
        if (hiveCatalog == null) {
            hiveCatalog = new HiveCatalog(
                    "hive",
                    "default",
                    "",
                    "1.2.1");
        }
        tEnv.registerCatalog("hive", hiveCatalog);
        tEnv.useCatalog("hive");
        tEnv.loadModule("hive", new HiveModule("1.2.1"));
    }


    private void callSetHiveVars(SqlCommandParser.SqlCommandCall cmdCall) {
        if (hiveCatalog == null) {
            throw new IllegalArgumentException("You should ENABLE HIVE before you set Hive/Map Vars");
        }
        String key = cmdCall.operands[0];
        String value = cmdCall.operands[1];
        tEnv.getConfig().getConfiguration().setString(key, value);
    }

    private void dropTable(SqlCommandParser.SqlCommandCall cmdCall){
        String dropSql = cmdCall.operands[0];
        try {
            tEnv.sqlUpdate(dropSql);
        } catch (SqlParserException e) {
            throw new RuntimeException("SQL parse failed:\n" + dropSql + "\n", e);
        }
    }

    private void callUse(SqlCommandParser.SqlCommandCall cmdCall) {
        String useSql = cmdCall.operands[0];
        try {
            tEnv.sqlUpdate(useSql);
        } catch (SqlParserException e) {
            throw new RuntimeException("SQL parse failed:\n" + useSql + "\n", e);
        }
    }


    private void callCreateFunction(SqlCommandParser.SqlCommandCall cmdCall){
        String ddl = cmdCall.operands[0];
        try {
            tEnv.sqlUpdate(ddl);
        } catch (SqlParserException e) {
            throw new RuntimeException("SQL parse failed:\n" + ddl + "\n", e);
        }
    }


    private void callRegisterCachedFile(SqlCommandParser.SqlCommandCall cmdCall){
        String fileName = cmdCall.operands[0];
        String filePath = cmdCall.operands[1];
        bsEnv.registerCachedFile(filePath,fileName);
    }


    private void callRegisterUDF(SqlCommandParser.SqlCommandCall cmdCall) {
        String importUDF = cmdCall.operands[0];
        Preconditions.checkNotNull(importUDF, "udf class name can not be empty");

        importUDF = importUDF.trim();
        if (importUDF.endsWith(".*")) {
            importUDF = importUDF.substring(0,importUDF.length()-2);
            List<ClassLoader> classLoadersList = new LinkedList<>();
            classLoadersList.add(ClasspathHelper.contextClassLoader());
            classLoadersList.add(ClasspathHelper.staticClassLoader());
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setScanners(new SubTypesScanner(true))
                    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(importUDF))));
            for(String clazz : reflections.getStore().get("SubTypesScanner").values()) {
                registerUDF(ReflectionUtils.newInstance(clazz));
            }

        } else {
            registerUDF(ReflectionUtils.newInstance(importUDF));
        }
    }

    private void registerUDF(Object udf) {
        if (udf instanceof ScalarFunction) {
            tEnv.registerFunction(udf.getClass().getSimpleName(), (ScalarFunction) udf);
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
