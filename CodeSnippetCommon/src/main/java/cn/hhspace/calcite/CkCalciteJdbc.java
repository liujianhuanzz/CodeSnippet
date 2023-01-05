package cn.hhspace.calcite;

import org.apache.calcite.util.Sources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/12/29 16:35
 * @Descriptions:
 */
public class CkCalciteJdbc {
    public static void main(String[] args) {
        Properties info = new Properties();
        info.put("model", resourcePath("clickhouse.json"));
        info.put("lex", "MYSQL");

        Connection connection = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:calcite:", info);
            stmt = connection.createStatement();

            int count = 0;
            while (++count < 10) {
                long start = System.currentTimeMillis();
                resultSet = stmt.executeQuery("select sip, dip, count(*) as cnt from sailfish_01 where sip=toIPv4('10.45.71.18') group by sip,dip");

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        String value = resultSet.getString(columnName);

                        System.out.print(columnName + ":" + value);
                        System.out.print(",");
                    }
                    System.out.println();
                }
                System.out.println("第" + count + "轮执行消耗时间为: " + (System.currentTimeMillis() - start));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resourcePath(String resourcePath) {
        return Sources.of(CkCalciteJdbc.class.getResource("/" + resourcePath)).file().getAbsolutePath();
    }
}
