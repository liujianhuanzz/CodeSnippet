package cn.hhspace.calcite;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaPlus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/12/26 16:05
 * @Descriptions:
 */
public class HelloCalcite {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.calcite.jdbc.Driver");
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:calcite:", info);
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            SchemaPlus rootSchema = calciteConnection.getRootSchema();
            Schema schema = new ReflectiveSchema(new HrSchema());
            rootSchema.add("hr", schema);
            statement = calciteConnection.createStatement();
            resultSet = statement.executeQuery(
                    "select * from hr.emps"
            );

            while (resultSet.next()) {
                System.out.println(resultSet.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (null != resultSet) {
                resultSet.close();
            }

            if (null != statement) {
                statement.close();
            }

            if (null != connection) {
                connection.close();
            }
        }
    }

    public static class HrSchema {
        public Employee[] emps = null;
        public Department[] depts = null;

        public HrSchema() {
            Employee emp1 = new Employee();
            emp1.empid = 1;
            emp1.deptno = 1;

            Employee emp2 = new Employee();
            emp2.empid = 2;
            emp2.deptno = 1;

            Department dep1 = new Department();
            dep1.deptno = 1;
            dep1.deptname = "AAA";

            Department dep2 = new Department();
            dep2.deptno = 2;
            dep2.deptname = "BBB";

            emps = new Employee[]{emp1,emp2};
            depts = new Department[]{dep1, dep2};
        }
    }
}

class Employee {
    int empid;
    int deptno;
}

class Department {
    int deptno;
    String deptname;
}


