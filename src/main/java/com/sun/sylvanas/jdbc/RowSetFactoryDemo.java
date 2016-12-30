package com.sun.sylvanas.jdbc;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
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

    /**
     * 离线的RowSet
     * 在资源关闭的情况下可以使用以下伪代码同步修改数据:
     * Connection conn = DriverManager.getConnection(url,user,pass);
     * conn.setAutoCommit(false);
     * rowSet.acceptChanges(conn); //把对RowSet所做的修改同步到底层数据库
     * 为了防止一次读取的资源过大导致内存溢出,CachedRowSet提供了分页功能(一次只装载ResultSet里的某几条记录)
     * populate(ResultSet rs,int startRow):使用给定的ResultSet装填RowSet,从ResultSet的第startRow条记录开始
     * setPageSize(int pageSize):设置CachedRowSet每次返回多少条记录
     * previousPage():在底层ResultSet可用的情况下,让CachedRowSet读取上一页记录
     * nextPage():在底层ResultSet可用的情况下,让CachedRowSet读取下一页记录
     * 例:
     * cachedRs.setPageSize(pageSize);
     * cachedRs.populate(rs,(page - 1) * pageSize + 1);
     */
    public void offline(String sql) throws ClassNotFoundException, SQLException {
        //加载驱动
        Class.forName(driver);
        //获取数据库连接
        Connection connection = DriverManager.getConnection(url, user, pass);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        //创建RowSet
        RowSetFactory factory = RowSetProvider.newFactory();
        //创建CachedRowSet
        CachedRowSet rowSet = factory.createCachedRowSet();
        //使用ResultSet装填RowSet
        rowSet.populate(resultSet);
        //关闭资源
        resultSet.close();
        statement.close();
        connection.close();
    }
}
