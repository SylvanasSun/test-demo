package com.sun.sylvanas.utils;

import org.apache.log4j.Logger;

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
            logger.error("Error: " + JDBCUtils.class.getName() + " class initializer fail.", e);
        }
        DRIVER = properties.getProperty("driver");
        URL = properties.getProperty("url");
        USERNAME = properties.getProperty("username");
        PASSWORD = properties.getProperty("password");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " load driver fail.");
        }
    }

    /**
     * 对应JDBC API中的execute方法,发生异常返回-1.
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 如果execute为true(Query操作)返回结果集, 否则为所影响的行数(DML操作).
     */
    public static Object execute(String sql, Object... param) {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.prepareStatement(sql);
            //设置参数
            if (param != null && param.length >= 0) {
                final AtomicInteger count = new AtomicInteger(1);
                Arrays.stream(param).forEach((i) -> {
                    try {
                        statement.setObject(count.getAndIncrement(), i);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        logger.error("Error: " + JDBCUtils.class.getName() + " set sql param fail.", e);
                    }
                });
            }
            //执行
            boolean execute = statement.execute();
            if (execute) {
                //true,返回ResultSet
                resultSet = statement.getResultSet();
                if (resultSet != null)
                    return resultSet;
                else
                    return -1;
            } else {
                return statement.getUpdateCount();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " sql fail.", e);
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Error: " + JDBCUtils.class.getName() + " close resource fail.", e);
            }
        }
        return -1;
    }
}
