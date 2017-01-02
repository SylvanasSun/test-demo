package com.sun.sylvanas.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对文件操作的工具类
 * Created by sylvanasp on 2016/12/23.
 */
public class FileUtils {
    private static Logger logger = Logger.getLogger(FileUtils.class);
    private static boolean watchFlag = true; //监控文件标志位
    private static ReentrantLock lock = new ReentrantLock();
    private static ExecutorService threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()) {
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            logger.debug("DEBUG: " + FileUtils.class.getName() + " threadPool beforeExecute: " + t);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            logger.debug("DEBUG: " + FileUtils.class.getName() + " threadPool afterExecute: " + r);
        }

        @Override
        protected void terminated() {
            logger.debug("DEBUG: " + FileUtils.class.getName() + " threadPool terminated.");
        }
    };

    /**
     * 校验字符串参数是否为空,不为空返回true,否则返回false.
     *
     * @param param 字符串参数数组
     * @return 成功为true, 否则false
     */
    private static boolean validateString(String... param) {
        for (String p : param) {
            if (p == null || "".equals(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验源路径文件与目标路径文件,由于文件与路径的校验方式不同,所以使用boolean isDir来设置校验方式.
     *
     * @param srcPath  源路径
     * @param destPath 目标路径
     * @param isDir    是否为路径
     * @param overlay  是否覆盖文件
     * @return 成功为true, 否则false
     */
    private static boolean validateFile(String srcPath, String destPath, boolean isDir, boolean overlay) {
        if (isDir) {
            File srcFile = new File(srcPath);
            if (!srcFile.exists()) {
                logger.debug("DEBUG: " + FileUtils.class.getName() + " validate srcFile(dir) not exists!");
                return false;
            }
            if (!srcFile.isDirectory()) {
                logger.debug("DEBUG: " + FileUtils.class.getName() + " validate srcFile(dir) is not directory!");
                return false;
            }
            File destFile = new File(destPath);
            if (destFile.exists()) {
                //判断是否允许覆盖
                if (overlay) {
                    if (!destFile.delete()) {
                        logger.debug("DEBUG: " + FileUtils.class.getName()
                                + " validate destFile(dir) delete fail!");
                        return false;
                    }
                } else {
                    logger.debug("DEBUG: " + FileUtils.class.getName()
                            + " validate destFile(dir) is exists(no overlay)!");
                    return false;
                }
            }
            //目标路径不存在,创建目标路径
            if (!destFile.mkdirs()) {
                logger.debug("DEBUG: " + FileUtils.class.getName()
                        + " validate destFile(dir) mkdirs fail!");
                return false;
            }
            return true;
        } else {
            File srcFile = new File(srcPath);
            if (!srcFile.exists()) {
                logger.debug("DEBUG: " + FileUtils.class.getName() + " validate srcFile not exists!");
                return false;
            }
            if (!srcFile.isFile()) {
                logger.debug("DEBUG: " + FileUtils.class.getName() + " validate srcFile is not file!");
                return false;
            }
            File destFile = new File(destPath);
            if (destFile.exists()) {
                if (overlay) {
                    if (!destFile.delete()) {
                        logger.debug("DEBUG: " + FileUtils.class.getName()
                                + " validate destFile delete fail!");
                        return false;
                    }
                } else {
                    logger.debug("DEBUG: " + FileUtils.class.getName()
                            + " validate destFile is exists(no overlay)!");
                    return false;
                }
            }
            return true;
        }
    }
}
