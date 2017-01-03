package com.sun.sylvanas.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对文件操作的工具类
 * Created by sylvanasp on 2016/12/23.
 */
public class FileUtils {
    private static final Logger logger = Logger.getLogger(FileUtils.class);
    private static boolean watchFlag = true; //监控文件标志位
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ExecutorService threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
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

    /**
     * 遍历目标路径并根据文件名搜索到文件.
     *
     * @param srcPath  目标路径
     * @param destName 目标文件名
     * @return 目标文件的Path对象.
     */
    public static Path searchFile(String srcPath, String destName) {
        if (!validateString(srcPath, destName)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " searchFile param is null or empty!");
            //字符串校验未通过,返回null
            return null;
        }
        File srcFile = new File(srcPath);
        //校验目标路径是否存在并且为一个路径
        if (!srcFile.exists()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " searchFile srcFile is not exists!");
            return null;
        }
        if (!srcFile.isDirectory()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " searchFile srcFile is not directory!");
            return null;
        }

        AtomicReference<Path> result = new AtomicReference<>(null);
        //使用FileVisitor完成对路径的遍历
        try {
            Files.walkFileTree(Paths.get(srcPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //找到目标
                    if (file.endsWith(destName)) {
                        result.compareAndSet(null, file);
                        return FileVisitResult.TERMINATE;
                    }
                    //没有找到目标,继续搜索
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("ERROR: " + FileUtils.class.getName()
                    + " searchFile visitFile failed!", e);
            return null;
        }
        return result.get();
    }

    /**
     * 监控目标路径下的所有文件状态变化(开启一个守护进程).
     *
     * @param srcPath 目标路径
     */
    public static void watch(String srcPath) {
        //校验字符串参数
        if (!validateString(srcPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " watch param is null or empty!");
            return;
        }
        File srcFile = new File(srcPath);
        //校验源路径是否存在并且为路径
        if (!srcFile.exists()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " watch srcFile is not exists!");
            return;
        }
        if (!srcFile.isDirectory()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " watch srcFile is not directory!");
            return;
        }
        //开启一条守护进程用于监控文件状态
        threadPool.submit(new WatchDaemon(srcPath));
    }

    /**
     * 使用NIO拷贝文件.
     *
     * @param srcPath  源文件路径
     * @param destPath 目标文件路径
     * @param overlay  是否覆盖
     * @return 返回boolean.
     */
    public static boolean copyFile(String srcPath, String destPath, boolean overlay) {
        if (!validateString(srcPath, destPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " copyFile param is null or empty!");
            return false;
        }
        //校验文件
        if (!validateFile(srcPath, destPath, false, overlay)) {
            return false;
        }
        //开启一条线程执行IO操作
        Future<Boolean> future = threadPool.submit(new CopyFileThread(srcPath, destPath));
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.error("ERROR: " + FileUtils.class.getName()
                    + " copyFile future get result fail!", e);
        }
        return false;
    }

    /**
     * 拷贝路径(包括所有的子文件)
     *
     * @param srcPath  源路径
     * @param destPath 目标路径
     * @param overlay  是否覆盖
     * @return 返回boolean.
     */
    public static boolean copyDir(String srcPath, String destPath, boolean overlay) {
        if (!validateString(srcPath, destPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " copyDir param is null or empty!");
            return false;
        }
        //校验文件有效性
        if (!validateFile(srcPath, destPath, true, overlay)) {
            return false;
        }
        //判断目标路径是否以 "\" 结尾.
        if (!destPath.endsWith("\\") || !destPath.endsWith("/")) {
            destPath = destPath + "/";
        }
        //获得源路径的文件集合
        File[] listFiles = new File(srcPath).listFiles();
        boolean flag = false;
        if (listFiles != null) {
            //遍历文件集合
            for (File file : listFiles) {
                //是文件,调用copyFile
                if (file.isFile()) {
                    flag = FileUtils.copyFile(file.getAbsolutePath(), destPath + file.getName(), overlay);
                    if (!flag) break;
                }
                //是文件夹,递归调用
                if (file.isDirectory()) {
                    flag = FileUtils.copyDir(file.getAbsolutePath(), destPath + file.getName(), overlay);
                    if (!flag) break;
                }
            }
        }
        return flag;
    }

    /**
     * 关闭文件状态监控
     */
    public static void stopWatch() {
        FileUtils.lock.tryLock();
        try {
            FileUtils.watchFlag = false;
            logger.info("<INFO> WatchFileState stop!");
        } finally {
            FileUtils.lock.unlock();
        }
    }

    /**
     * 拷贝文件线程
     */
    private static class CopyFileThread implements Callable<Boolean> {
        private String srcPath;
        private String destPath;

        private CopyFileThread(String srcPath, String destPath) {
            this.srcPath = srcPath;
            this.destPath = destPath;
        }

        @Override
        public Boolean call() throws Exception {
            FileUtils.lock.tryLock();
            Thread.currentThread().setName("CopyFileThread-" + new Random().nextInt(101));
            try {
                FileChannel inChannel = new FileInputStream(new File(srcPath)).getChannel();
                FileChannel outChannel = new FileOutputStream(new File(destPath)).getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                //执行文件拷贝
                while (inChannel.read(buffer) > 0) {
                    buffer.flip();
                    outChannel.write(buffer);
                    buffer.clear();
                }
                logger.debug("DEBUG: " + FileUtils.class.getName()
                        + " copy file " + srcPath + " to " + destPath + " success!");
                return true;
            } finally {
                FileUtils.lock.unlock();
            }
        }
    }

    /**
     * 监控文件状态的守护进程
     */
    private static class WatchDaemon implements Runnable {
        private final String srcPath;

        private WatchDaemon(String srcPath) {
            this.srcPath = srcPath;
        }

        @Override
        public void run() {
            Random random = new Random();
            //将本线程更改为守护进程
            Thread.currentThread().setDaemon(true);
            Thread.currentThread().setName("WatchFileStatusThread-" + random.nextInt(101) + "(daemon)");
            //获得WatchServer
            WatchService watchService = null;
            try {
                watchService = FileSystems.getDefault().newWatchService();
                //注册监听事件
                Paths.get(srcPath).register(watchService,
                        StandardWatchEventKinds.OVERFLOW,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE);

                while (watchFlag) {
                    //遍历watchService的key
                    WatchKey watchKey = null;
                    try {
                        watchKey = watchService.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error("ERROR: " + FileUtils.class.getName()
                                + " WatchThread caught InterruptedException!", e);
                    }
                    if (watchKey != null) {
                        for (WatchEvent<?> event : watchKey.pollEvents()) {
                            logger.info("<INFO> WatchThread: " + event.context() + " is " + event.kind());
                        }
                        //重置key
                        boolean valid = watchKey.reset();
                        if (!valid) break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " WatchThread caught IOException!", e);
            }
        }
    }

    /**
     * 关闭线程池(正在进行的任务会进行到结束才关闭)
     */
    public static void shutdown() {
        FileUtils.lock.tryLock();
        try {
            FileUtils.threadPool.shutdown();
            logger.info("<INFO> " + FileUtils.class.getName() + " ThreadPool shutdown!");
        } finally {
            FileUtils.lock.unlock();
        }
    }

    /**
     * 关闭线程池(立即停止所有任务)
     */
    public static void shutdownNow() {
        FileUtils.lock.tryLock();
        try {
            FileUtils.threadPool.shutdownNow();
            logger.info("<INFO> " + FileUtils.class.getName() + " ThreadPool shutdownNow!");
        } finally {
            FileUtils.lock.unlock();
        }
    }
}
