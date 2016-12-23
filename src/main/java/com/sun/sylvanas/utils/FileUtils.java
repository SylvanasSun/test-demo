package com.sun.sylvanas.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 对文件操作的工具类
 * Created by sylvanasp on 2016/12/23.
 */
public class FileUtils {

    private static boolean watchFlag = true; //监控文件标记

    /**
     * 检查参数是否有效.
     */
    private static void validateParam(String... param) {
        for (int i = 0; i < param.length; i++) {
            if ("".equals(param[i]) || param[i] == null)
                throw new IllegalArgumentException("param is empty or null.");
        }
    }

    /**
     * 在一个目录中搜索文件.
     *
     * @param srcPath  源路径
     * @param fileName 目标文件名
     * @return 目标文件的路径
     */
    public static Path searchFile(String srcPath, String fileName) throws IOException {
        validateParam(srcPath, fileName);

        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            System.out.println("search fail: " + srcPath + " is no exists.");
            return null;
        } else if (!srcFile.isDirectory()) {
            System.out.println("search fail: " + srcPath + " is no directory.");
            return null;
        }

        final boolean[] flag = {false};
        final Path[] result = {null};
        Files.walkFileTree(Paths.get(srcPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.endsWith(fileName)) {
                    flag[0] = true;
                    result[0] = file;
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (!flag[0]) {
            System.out.println("search fail: Not found " + fileName + ".");
            return null;
        } else {
            System.out.println("search success: " + result[0].toAbsolutePath() + ".");
            return result[0];
        }
    }

    /**
     * 监控目标路径下的文件状态.
     *
     * @param srcPath 目标路径
     */
    public static void watch(String srcPath) throws IOException, InterruptedException {
        validateParam(srcPath);

        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            System.out.println("watch fail: " + srcPath + " no exists.");
            return;
        } else if (!srcFile.isDirectory()) {
            System.out.println("watch fail: " + srcPath + " no directory.");
            return;
        }

        //注册监听事件.
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Paths.get(srcPath).register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.OVERFLOW);

        while (watchFlag) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(event.context() + " is " + event.kind());
            }
            //重置key
            boolean valid = key.reset();
            if (!valid)
                break;
        }
    }

    /**
     * 拷贝文件,使用了NIO实现.
     *
     * @param srcPath  源路径
     * @param destPath 目标路径
     * @param overlay  是否允许覆盖
     * @return 成功为true, 失败为false.
     */
    public static boolean copyFile(String srcPath, String destPath, boolean overlay) throws IOException {
        validateParam(srcPath, destPath);

        File srcFile = new File(srcPath);
        //判断源文件是否存在,以及是否为文件
        if (!srcFile.exists()) {
            System.out.println("copy file fail: " + srcPath + " is no exists.");
            return false;
        } else if (!srcFile.isFile()) {
            System.out.println("copy file fail: " + srcPath + " is no file.");
            return false;
        }

        File destFile = new File(destPath);
        //判断目标文件是否存在,以及是否允许覆盖
        if (destFile.exists()) {
            //允许覆盖则直接删除目标原文件
            if (overlay) {
                if (!destFile.delete()) {
                    System.out.println("copy file fail: " + destPath + " is exists.");
                    return false;
                }
            } else {
                System.out.println("copy file fail: " + destPath + " is exists.");
                return false;
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        boolean flag;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(srcFile).getChannel();
            outChannel = new FileOutputStream(destFile).getChannel();
            while (inChannel.read(buffer) != -1) {
                buffer.flip(); //锁定空白区
                outChannel.write(buffer);
                buffer.clear(); //为下一次读写做准备.
            }
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (outChannel != null) outChannel.close();
            if (inChannel != null) inChannel.close();
        }
        if (!flag) {
            System.out.println("copy file failed.");
            return false;
        } else {
            System.out.println("copy file success.");
            return true;
        }
    }

    /**
     * 拷贝整个文件夹以及目录下的所有文件
     *
     * @param srcPath  源路径
     * @param destPath 目标路径
     * @param overlay  是否覆盖
     * @return 成功返回true, 失败返回false.
     */
    public static boolean copyDirs(String srcPath, String destPath, boolean overlay) throws IOException {
        validateParam(srcPath, destPath);

        File srcFile = new File(srcPath);
        //判断源文件是否存在以及是否为一个目录
        if (!srcFile.exists()) {
            System.out.println("copy dirs fail: " + srcPath + " is no exists.");
            return false;
        } else if (!srcFile.isDirectory()) {
            System.out.println("copy dirs fail: " + srcPath + " is no directory.");
            return false;
        }

        File destFile = new File(destPath);
        //判断目标文件是否存在以及是否允许覆盖
        if (destFile.exists()) {
            if (overlay) {
                if (!destFile.delete()) {
                    System.out.println("copy dirs fail: " + destPath + " is exists.");
                    return false;
                }
            } else {
                System.out.println("copy dirs fail: " + destPath + " is exists.");
                return false;
            }
        } else {
            //不存在则创建该目录
            if (!destFile.mkdirs()) {
                System.out.println("copy dirs fail: " + destPath + " create failed.");
                return false;
            }
        }

        //遍历源目录并根据文件和目录的类型进行递归调用
        File[] files = srcFile.listFiles();
        boolean flag = false;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = copyFile(files[i].getAbsolutePath(), destPath + files[i].getName(), overlay);
                if (!flag) return false;
            }
            if (files[i].isDirectory()) {
                flag = copyDirs(files[i].getAbsolutePath(), destPath + files[i].getName(), overlay);
                if (!flag) return false;
            }
        }
        if (flag) {
            return true;
        } else {
            System.out.println("copy dirs failed.");
            return false;
        }
    }

    public static void stopWatch() {
        watchFlag = false;
    }
}
