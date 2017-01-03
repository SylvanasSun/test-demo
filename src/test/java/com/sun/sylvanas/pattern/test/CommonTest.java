package com.sun.sylvanas.pattern.test;

import com.sun.sylvanas.utils.JDBCUtils;
import org.junit.Test;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.sql.*;

/**
 * Created by sylvanasp on 2016/12/30.
 */
public class CommonTest {
    @Test
    public void test01() throws FileNotFoundException, SQLException {
        String sql = "SELECT * FROM TEST_STUDENT";
        RowSet rowSet = JDBCUtils.executeQuery(sql, null);
        while (rowSet.next()) {
            System.out.println(rowSet.getString(1));
            System.out.println(rowSet.getString(2));
            System.out.println(rowSet.getString(3));
        }
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

    @Test
    public void test03() {
        String msg = "//admin:hello,world.";
        String s = msg.substring(2);
        String result = "%" + s.split(":")[0] + "-" + s.split(":")[1] + "%";
        System.out.println(result);
    }
}
