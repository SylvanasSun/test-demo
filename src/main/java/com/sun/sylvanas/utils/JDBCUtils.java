package com.sun.sylvanas.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JDBC工具类
 * <p>
 * Created by sylvanasp on 2016/12/30.
 */
public class JDBCUtils {
    private final static Properties properties;
    private final static Logger logger;
    private final static String DRIVER;
    private final static String URL;
    private final static String USERNAME;
    private final static String PASSWORD;
    private static ComboPooledDataSource dataSource = null;
    private static Connection connection = null;
    private static PreparedStatement statement = null;
    private static ResultSet resultSet = null;

    /**
     * 初始化成员变量
     */
    static {
        logger = Logger.getLogger(JDBCUtils.class);
        properties = new Properties();
        try {
            properties.load(new FileInputStream("db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " properties initializer fail.", e);
        }
        DRIVER = properties.getProperty("driver");
        URL = properties.getProperty("url");
        USERNAME = properties.getProperty("username");
        PASSWORD = properties.getProperty("password");

        try {
            //初始化c3p0连接池
            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass(DRIVER);
            dataSource.setJdbcUrl(URL);
            dataSource.setUser(USERNAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setMaxPoolSize(40);
            dataSource.setMinPoolSize(2);
            dataSource.setInitialPoolSize(5);
            dataSource.setMaxStatements(180);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " dataSource initializer fail.");
        }
    }

    /**
     * 对应JDBC API中的execute方法,发生异常返回-1.
     *
     * @param sql      sql语句
     * @param pageSize 每页个数
     * @param page     要查询的页
     * @param param    参数
     * @return 如果execute为true(Query操作)返回离线的分页RowSet, 否则为所影响的行数(DML操作).
     */
    public static Object execute(String sql, Integer pageSize, Integer page, Object... param) {
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); //开启事务
            statement = connection.prepareStatement(sql);
            //设置参数
            if (param != null && param.length > 0) {
                final AtomicInteger count = new AtomicInteger(1);
                Arrays.stream(param).forEach((i) -> {
                    try {
                        statement.setObject(count.getAndIncrement(), i);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        logger.error("Error: " + JDBCUtils.class.getName() + " set sql param fail.", e);
                        try {
                            connection.rollback();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                            logger.error("Error: " + JDBCUtils.class.getName() + " transaction rollback fail.", e1);
                        }
                    }
                });
            }
            //执行sql
            boolean execute = statement.execute();
            connection.commit(); //提交事务
            if (execute) {
                //创建离线的RowSet
                CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
                //true,包装并返回RowSet
                resultSet = statement.getResultSet();
                if (resultSet != null) {
                    //判断是否使用分页
                    if (page != null && pageSize != null) {
                        cachedRowSet.populate(resultSet, (page - 1) * pageSize + 1);
                    } else {
                        cachedRowSet.populate(resultSet);
                    }
                    return cachedRowSet;
                } else {
                    return -1;
                }
            } else {
                return statement.getUpdateCount();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " sql fail.", e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                logger.error("Error: " + JDBCUtils.class.getName() + " transaction rollback fail.", e1);
            }
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Error: " + JDBCUtils.class.getName() + " close resource fail.", e);
            }
        }
        return -1;
    }

    /**
     * 对应JDBC API中的execute方法,发生异常返回-1.
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 如果execute为true(Query操作)返回离线RowSet, 否则为所影响的行数(DML操作).
     */
    public static Object execute(String sql, Object... param) {
        return JDBCUtils.execute(sql, null, null, param);
    }


}
