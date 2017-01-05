package com.sun.sylvanas.utils;

import org.apache.log4j.Logger;

import java.io.*;
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
     * 向目标文件末尾处追加源文件的内容,如果目标文件不存在,则copyFile
     *
     * @param srcPath  源文件
     * @param destPath 目标文件
     * @return 返回boolean.
     */
    public static boolean appendFile(String srcPath, String destPath) {
        if (!validateString(srcPath, destPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " appendFile param is null or empty!");
            return false;
        }
        File srcFile = new File(srcPath);
        //判断源文件是否存在并且为文件
        if (!srcFile.exists()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " appendFile srcFile is not exists!");
            return false;
        }
        if (!srcFile.isFile()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " appendFile srcFile is not file!");
            return false;
        }
        File destFile = new File(destPath);
        //判断目标文件是否存在
        if (!destFile.exists()) {
            //不存在则copyFile
            return FileUtils.copyFile(srcPath, destPath, false);
        } else {
            //开启一条线程执行追加文件
            Future<Boolean> future = threadPool.submit(new AppendFileThread(srcPath, destPath));
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " appendFile future get result fail!", e);
            }
        }
        return false;
    }

    /**
     * 追加内容到目标文件,如果目标文件不存在,则生成文件.
     *
     * @param destPath 目标文件路径
     * @param bytes    内容字节
     * @return 返回boolean
     */
    public static boolean appendContentTo(String destPath, byte[] bytes) {
        if (!validateString(destPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " appendContentTo param is null or empty!");
            return false;
        }
        File destFile = new File(destPath);
        //判断目标文件是否存在,如果不存在则生成文件
        if (!destFile.exists()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " appendContentTo destFile is not exists,execute createFile!");
            return FileUtils.createFile(destPath, bytes, false);
        }
        if (!destFile.isFile()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " appendContentTo destFile is not file!");
            return false;
        }
        //开启追加内容到文件线程
        Future<Boolean> future = threadPool.submit(new AppendContentToThread(destFile, bytes));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.error("ERROR: " + FileUtils.class.getName()
                    + " appendContentTo future get result fail!", e);
        }
        return false;
    }

    /**
     * 追加字符串内容到目标文件,如果目标文件不存在,则生成文件.
     *
     * @param destPath 目标文件路径
     * @param content  字符串内容
     * @return 返回boolean
     */
    public static boolean appendContentTo(String destPath, String content) {
        if (!validateString(destPath, content)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " appendContentTo param is null or empty!");
            return false;
        }
        return FileUtils.appendContentTo(destPath, content.getBytes());
    }

    /**
     * 追加内容到文件的线程
     */
    private static class AppendContentToThread implements Callable<Boolean> {
        private File destFile = null;
        private byte[] bytes = null;

        private AppendContentToThread(File destFile, byte[] bytes) {
            this.destFile = destFile;
            this.bytes = bytes;
        }

        @Override
        public Boolean call() throws Exception {
            FileUtils.lock.tryLock();
            Thread.currentThread().setName("AppendContentToThread-" + new Random().nextInt(101));
            try (FileChannel channel = new FileOutputStream(destFile).getChannel();) {
                //将指针指向最后
                channel.position(destFile.length());
                //写入追加内容
                int len = channel.write(ByteBuffer.wrap(bytes));
                if (len > 0) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName() +
                        " " + Thread.currentThread().getName() + " caught IOException!", e);
            } finally {
                FileUtils.lock.unlock();
            }
            return false;
        }
    }

    /**
     * 向目标文件(指定指针处)插入源文件的内容,如果目标文件不存在,则执行CopyFile
     *
     * @param srcPath  源文件路径
     * @param destPath 目标文件路径
     * @param tempPath 临时文件路径
     * @param point    文件指针
     * @return 返回boolean
     */
    public static boolean insertFile(String srcPath, String destPath, String tempPath, int point) throws ExecutionException {
        if (!validateString(srcPath, destPath, tempPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " insertFile param is null or empty!");
            return false;
        }
        File srcFile = new File(srcPath);
        //判断源文件是否存在,并且为一个文件
        if (!srcFile.exists() || !srcFile.isFile()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " insertFile srcFile is not exists or is not file!");
            return false;
        }
        File destFile = new File(destPath);
        //判断目标文件是否存在,如果不存在则copyFile
        if (!destFile.exists()) {
            return FileUtils.copyFile(srcPath, destPath, false);
        } else {
            File tempFile = new File(tempPath);
            //判断临时文件是否存在,如果存在则返回false
            if (tempFile.exists()) {
                logger.debug("DEBUG: " + FileUtils.class.getName()
                        + " insertFile tempFile is exists!");
                return false;
            }
            Future<Boolean> future = threadPool.submit(new InsertFileThread(srcFile, destFile, tempFile, point));
            try {
                return future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " insertFile future get result fail!", e);
            }
            return false;
        }
    }

    /**
     * 插入文件线程,使用将后续(被覆盖)的内容写入临时文件,最后再从临时文件写入丢失的后续内容的策略.
     */
    private static class InsertFileThread implements Callable<Boolean> {
        private File srcFile = null;
        private File destFile = null;
        private File tempFile = null;
        private int point = 0;

        private InsertFileThread(File srcFile, File destFile, File tempFile, int point) {
            this.srcFile = srcFile;
            this.destFile = destFile;
            this.tempFile = tempFile;
            this.point = point;
        }

        @Override
        public Boolean call() throws Exception {
            FileUtils.lock.tryLock();
            Thread.currentThread().setName("InsertFileThread-" + new Random().nextInt(101));
            FileChannel tempChannel = null;
            try (FileChannel srcChannel = new RandomAccessFile(srcFile, "rw").getChannel();
                 FileChannel destChannel = new RandomAccessFile(destFile, "rw").getChannel()) {
                //创建临时文件
                if (!tempFile.createNewFile()) {
                    logger.debug("DEBUG: " + FileUtils.class.getName() +
                            " " + Thread.currentThread().getName() + " tempFile create fail!");
                    return false;
                }
                tempChannel = new FileOutputStream(tempFile).getChannel();
                //将指针设置到insert位置
                destChannel.position(point);
                //将会被覆盖的内容存入临时文件
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while ((destChannel.read(buffer)) != -1) {
                    buffer.flip();
                    tempChannel.write(buffer);
                    buffer.clear();
                }
                //将指针重新设置回insert位置
                destChannel.position(point);
                //追加源文件的内容
                while ((srcChannel.read(buffer)) != -1) {
                    buffer.flip();
                    destChannel.write(buffer);
                    buffer.clear();
                }
                //追加临时文件里暂存的内容
                while ((tempChannel.read(buffer)) != -1) {
                    buffer.flip();
                    destChannel.write(buffer);
                    buffer.clear();
                }
                logger.debug("DEBUG: " + Thread.currentThread().getName() +
                        " insert " + srcFile.getName() + " to " + destFile.getName() + " success!");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName() +
                        " " + Thread.currentThread().getName() + " caught IOException!", e);
            } finally {
                if (tempChannel != null) tempChannel.close();
                tempFile.delete(); //删除临时文件
                FileUtils.lock.unlock();
            }
            return false;
        }
    }

    /**
     * 向目标文件(指定指针处)插入内容,如果目标文件不存在,则生成指定内容的文件
     *
     * @param destPath 目标文件路径
     * @param tempPath 临时文件路径
     * @param bytes    内容
     * @param point    指针
     * @return 返回boolean
     */
    public static boolean insertContentTo(String destPath, String tempPath, byte[] bytes, int point) {
        if (!validateString(destPath, tempPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " insertContentTo param is null or empty!");
            return false;
        }
        File destFile = new File(destPath);
        //判断目标文件是否存在,如果不存在则生成指定内容的文件
        if (!destFile.exists()) {
            return FileUtils.createFile(destPath, bytes, false);
        } else {
            File tempFile = new File(tempPath);
            //判断临时文件是否存在
            if (tempFile.exists()) {
                logger.debug("DEBUG: " + FileUtils.class.getName()
                        + " insertContentTo tempFile is exists!");
                return false;
            }
            //创建临时文件
            try {
                if (!tempFile.createNewFile()) {
                    logger.debug("DEBUG: " + FileUtils.class.getName()
                            + " insertContentTo tempFile create fail!");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Future<Boolean> future = threadPool.submit(new InsertContentToThread(destFile, tempFile, bytes, point));
            //删除临时文件
            if (!tempFile.delete()) {
                logger.debug("DEBUG: " + FileUtils.class.getName()
                        + " insertContentTo tempFile delete fail!");
            }
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " insertContentTo future get result fail!", e);
            }
            return false;
        }
    }

    /**
     * 向目标文件(指定指针处)插入字符串内容,如果目标文件不存在,则生成指定内容的文件
     *
     * @param destPath 目标文件路径
     * @param tempPath 临时文件路径
     * @param content  字符串内容
     * @param point    指针
     * @return 返回boolean
     */
    public static boolean insertContentTo(String destPath, String tempPath, String content, int point) {
        if (!validateString(destPath, tempPath, content)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " insertContentTo param is null or empty!");
            return false;
        }
        return FileUtils.insertContentTo(destPath, tempPath, content.getBytes(), point);
    }

    private static class InsertContentToThread implements Callable<Boolean> {
        private File destFile = null;
        private File tempFile = null;
        private byte[] bytes = null;
        private int point = 0;

        private InsertContentToThread(File destFile, File tempFile, byte[] bytes, int point) {
            this.destFile = destFile;
            this.tempFile = tempFile;
            this.bytes = bytes;
            this.point = point;
        }

        @Override
        public Boolean call() throws Exception {
            FileUtils.lock.tryLock();
            Thread.currentThread().setName("InsertContentToThread-" + new Random().nextInt(101));
            try (FileChannel destChannel = new RandomAccessFile(destFile, "rw").getChannel();
                 FileChannel tempChannel = new RandomAccessFile(tempFile, "rw").getChannel()) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                //将指针设置到指定位置
                destChannel.position(point);
                //将要被覆盖的内容写入临时文件
                while ((destChannel.read(buffer)) != -1) {
                    buffer.flip();
                    tempChannel.write(buffer);
                    buffer.clear();
                }
                //将指针重新设置回指定位置
                destChannel.position(point);
                //写入追加内容
                int len = destChannel.write(ByteBuffer.wrap(bytes));
                if (len < 0) {
                    //写入失败
                    return false;
                }
                //将临时文件寄存的内容重新写回
                while ((tempChannel.read(buffer)) != -1) {
                    buffer.flip();
                    destChannel.write(buffer);
                    buffer.clear();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + Thread.currentThread().getName()
                        + " caught IOException!", e);
            } finally {
                FileUtils.lock.unlock();
            }
            return false;
        }
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
     * 生成一个指定内容的文件
     *
     * @param destPath 目标文件路径
     * @param bytes    内容字节
     * @param overlay  是否覆盖文件
     * @return 返回boolean
     */
    public static boolean createFile(String destPath, byte[] bytes, boolean overlay) {
        if (!validateString(destPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " createFile param is null or empty!");
            return false;
        }
        File destFile = new File(destPath);
        //判断目标文件是否存在
        if (destFile.exists()) {
            if (overlay) {
                if (!destFile.delete()) {
                    logger.debug("DEBUG: " + FileUtils.class.getName()
                            + " createFile destFile overlay fail!");
                    return false;
                }
            } else {
                logger.debug("DEBUG: " + FileUtils.class.getName()
                        + " createFile destFile is exitis!");
                return false;
            }
        }
        //判断字节数组是否为空,如果为空则生成一个空文件
        if (bytes == null || bytes.length <= 0) {
            try {
                return destFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " createFile caught IOException!", e);
            }
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(destFile);
            //创建目标文件
            if (!destFile.createNewFile()) {
                logger.debug("DEBUG: " + FileUtils.class.getName()
                        + " createFile destFile create fail!");
                return false;
            }
            //向目标文件输出内容
            outputStream.write(bytes != null ? bytes : new byte[0]);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("ERROR: " + FileUtils.class.getName()
                    + " createFile caught IOException!", e);
        } finally {
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " createFile close resource fail!", e);
            }
        }
        return false;
    }

    /**
     * 生成一个指定字符串内容的文件
     *
     * @param destPath 目标文件路径
     * @param content  字符串内容
     * @param overlay  是否覆盖文件
     * @return 返回boolean
     */
    public static boolean createFile(String destPath, String content, boolean overlay) {
        return FileUtils.createFile(destPath, content.getBytes(), overlay);
    }

    /**
     * 读取目标文件的字节内容并返回
     *
     * @param destPath 目标文件路径
     * @return 字节内容
     */
    public static byte[] readFile(String destPath) {
        if (!validateString(destPath)) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " readFile param is null or empty!");
            return null;
        }
        File destFile = new File(destPath);
        //判断目标文件是否存在并且为一个文件
        if (!destFile.exists() || !destFile.isFile()) {
            logger.debug("DEBUG: " + FileUtils.class.getName()
                    + " readFile destFile is not exists or is not file!");
            return null;
        }
        //读取文件内容
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try (FileChannel channel = new FileInputStream(destFile).getChannel()) {
            int len = channel.read(buffer);
            buffer.flip();
            byte[] result = null;
            if (len > 0) {
                result = buffer.array();
            }
            buffer.clear();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("ERROR: " + FileUtils.class.getName()
                    + " readFile caught IOException!", e);
        }
        return null;
    }

    /**
     * 追加文件内容线程
     */
    private static class AppendFileThread implements Callable<Boolean> {
        private String srcPath;
        private String destPath;

        private AppendFileThread(String srcPath, String destPath) {
            this.srcPath = srcPath;
            this.destPath = destPath;
        }

        @Override
        public Boolean call() throws Exception {
            FileUtils.lock.tryLock();
            Thread.currentThread().setName("AppendFileThread-" + new Random().nextInt(101));
            FileChannel channel = null;
            FileInputStream srcIn = null;
            try {
                channel = new RandomAccessFile(destPath, "rw").getChannel();
                srcIn = new FileInputStream(srcPath);
                //将目标文件的指针设置到最后
                channel.position(new File(destPath).length());
                //读取源文件的内容并追加到目标文件
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = srcIn.read(buf, 0, buf.length)) != -1) {
                    channel.write(ByteBuffer.wrap(buf));
                }
                logger.debug("DEBUG: " + Thread.currentThread().getName()
                        + " append file " + srcPath + " to " + destPath + " success!");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " AppendFileThread caught IOException!", e);
            } finally {
                if (channel != null) channel.close();
                if (srcIn != null) srcIn.close();
                FileUtils.lock.unlock();
            }
            return false;
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
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                inChannel = new FileInputStream(new File(srcPath)).getChannel();
                outChannel = new FileOutputStream(new File(destPath)).getChannel();
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
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("ERROR: " + FileUtils.class.getName()
                        + " CopyFileThread caught IOException!", e);
            } finally {
                if (outChannel != null)
                    outChannel.close();
                if (inChannel != null)
                    inChannel.close();
                FileUtils.lock.unlock();
            }
            return false;
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
