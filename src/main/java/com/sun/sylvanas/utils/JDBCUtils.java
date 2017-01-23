package com.sun.sylvanas.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
//            properties.load(new FileInputStream(new File("db.properties")));
            InputStream in = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(in);
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
            logger.error("Error: " + JDBCUtils.class.getName() + " dataSource initializer fail.", e);
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
                setParam(param);
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
            logger.error("Error: " + JDBCUtils.class.getName() + " execute fail.", e);
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

    /**
     * 对应JDBC API中的executeQuery.
     *
     * @param sql      sql语句
     * @param pageSize 每页个数
     * @param page     要查询的页
     * @param param    参数
     * @return 返回一个分页的离线RowSet.
     */
    public static RowSet executeQuery(String sql, Integer pageSize, Integer page, Object... param) {
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            //设置参数
            if (param != null && param.length > 0) {
                setParam(param);
            }
            //执行sql
            resultSet = statement.executeQuery();
            //创建并包装RowSet
            CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
            //判断是否使用分页
            if (page != null && pageSize != null) {
                rowSet.populate(resultSet, (page - 1) * pageSize + 1);
            } else {
                rowSet.populate(resultSet);
            }
            return rowSet;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " executeQuery fail.", e);
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
        return null;
    }

    /**
     * 对应JDBC API中的executeQuery.
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 返回一个离线RowSet.
     */
    public static RowSet executeQuery(String sql, Object... param) {
        return executeQuery(sql, null, null, param);
    }

    /**
     * 对应JDBC API中的executeUpdate.
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 返回影响的行数.
     */
    public static int executeUpdate(String sql, Object... param) {
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); //开启事务
            statement = connection.prepareStatement(sql);
            //设置参数
            if (param != null && param.length > 0) {
                setParam(param);
            }
            int result = statement.executeUpdate();
            connection.commit(); //提交事务
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " executeUpdate fail.", e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                logger.error("Error: " + JDBCUtils.class.getName() + " transaction rollback fail.", e1);
            }
        } finally {
            close();
        }
        return -1;
    }

    /**
     * 对应JDBC API中的executeUpdateBatch.
     * 这个方法使用了Statement,它的效率与安全性都不如PrepareStatement
     *
     * @param sql sql语句
     * @return 返回影响的行数.
     */
    @SuppressWarnings("Duplicates")
    public static int[] executeUpdateBatch(String... sql) {
        Statement stat = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); //开启事务
            stat = connection.createStatement();
            if (sql != null && sql.length > 0) {
                for (String s : sql) {
                    stat.addBatch(s);
                }
            }
            //执行查询
            int[] results = stat.executeBatch();
            connection.commit(); //提交事务
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " executeUpdateBatch fail.", e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                logger.error("Error: " + JDBCUtils.class.getName() + " transaction rollback fail.", e1);
            }
        } finally {
            try {
                if (stat != null)
                    stat.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Error: " + JDBCUtils.class.getName() + " close resource fail.", e);
            }
        }
        return null;
    }

    /**
     * 获得数据库元数据
     */
    public static DatabaseMetaData getDatabaseMetaData() {
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " getDatabaseMetaData fail.", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Error: " + JDBCUtils.class.getName() + " close resource fail.");
            }
        }
        return null;
    }


    /**
     * 根据传入的对象和参数来自动生成一条insert语句,需要对象的类名与属性名与表一致.
     *
     * @param clazz 传入的对象
     * @param param insert参数
     */
    public static int insert(Class clazz, Object... param) {
        if (param == null || param.length == 0) return -1;
        if (clazz == null) return -1;

        StringBuilder sb = new StringBuilder();
        //拼接sql语句
        sb.append("insert into ").append(clazz.getName()).append(" (");
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) return -1;

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (i != fields.length - 1) {
                sb.append(fields[i].getName()).append(",");
            } else {
                sb.append(fields[i].getName()).append(") values(");
            }
        }
        int fieldsLen = fields.length;
        if (param.length != fieldsLen) return -1; //如果传入参数个数与传入的对象属性数不一致则return-1
        for (int i = 0; i < fieldsLen; i++) {
            if (i != fieldsLen - 1) {
                sb.append("?,");
            } else {
                sb.append("?)");
            }
        }

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sb.toString());
            int result = -1;
            try {
                result = statement.executeUpdate();
            } catch (Exception e) {
                connection.rollback();
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " insert method fail.", e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 设置参数
     *
     * @param param 参数
     */
    private static void setParam(Object[] param) {
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

    @SuppressWarnings("Duplicates")
    private static void close() {
        try {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error: " + JDBCUtils.class.getName() + " close resource fail.", e);
        }
    }

}
