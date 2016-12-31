package com.sun.sylvanas.pattern.test;

import org.junit.Test;

import java.sql.*;

/**
 * Created by sylvanasp on 2016/12/30.
 */
public class CommonTest {
    @Test
    public void test01() {
        String[] strings = {};
        System.out.println(strings.length);
    }

    @Test
    public void test02() throws ClassNotFoundException, SQLException {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String pass = "root";

        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url, user, pass);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet test_student = metaData.getColumns(null, null, "test_student", "%");

        ResultSetMetaData rsmd = test_student.getMetaData();
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            System.out.print(rsmd.getColumnName(i + 1) + "\t");
        }
        System.out.print("\n");
        while (test_student.next()) {
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                System.out.print(test_student.getString(i + 1) + "\t");
            }
            System.out.print("\n");
        }
        test_student.close();
        connection.close();
    }
}
