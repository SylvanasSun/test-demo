package com.sun.sylvanas.jdbc;

import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * RowSet默认是可滚动、可更新、可序列化的结果集,而且作为JavaBean使用.
 * 对于离线RowSet而言,程序在创建RowSet时已经把数据从底层数据库读取到了内存,
 * 因此可以充分利用计算机的内存,从而降低数据库服务器的负载,提高程序性能.
 * <p>
 * Created by sylvanasp on 2016/12/30.
 */
public class RowSetFactoryDemo {
    private String driver;
    private String url;
    private String user;
    private String pass;

    public void initParam(String paramFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(paramFile));
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        user = properties.getProperty("user");
        pass = properties.getProperty("pass");
    }

    public void update(String sql) throws ClassNotFoundException, SQLException {
        //加载驱动
        Class.forName(driver);
        //使用RowSetProvider创建RowSetFactory
        RowSetFactory factory = RowSetProvider.newFactory();
        //创建默认的JdbcRowSet实例(这里使用了try块的自动关闭连接)
        try (JdbcRowSet jdbcRowSet = factory.createJdbcRowSet()) {
            //设置连接信息
            jdbcRowSet.setUrl(url);
            jdbcRowSet.setUsername(user);
            jdbcRowSet.setPassword(pass);
            //设置SQL查询语句
            jdbcRowSet.setCommand(sql);
            //执行查询
            jdbcRowSet.execute();
            //将指针置为最后一行之后
            jdbcRowSet.afterLast();
            //向前滚动结果集
            while (jdbcRowSet.previous()) {
                System.out.println(jdbcRowSet.getString(1)
                        + "\t" + jdbcRowSet.getString(2)
                        + "\t" + jdbcRowSet.getString(3));
                if (jdbcRowSet.getInt("student_id") == 3) {
                    //修改指定记录行
                    jdbcRowSet.updateString("student_name", "sylvanas");
                    jdbcRowSet.updateRow();
                }
            }
        }
    }
}
