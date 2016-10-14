package com.sun.sylvanas.pattern.test;

import java.io.File;

/**
 * Created by sylvanasp on 2016/10/14.
 */
public class FileTest {

    private static class TFileUtils {

        /**
         * 遍历文件目录并返回一个包含文件路径的String数组
         */
        public static String[] listDirectory(File file) {
            if (file == null) {
                throw new IllegalArgumentException(file + "为null");
            }
            if (!file.exists()) {
                throw new IllegalArgumentException(file + "不存在");
            }
            File[] files = file.listFiles();
            String[] result;
            //遍历文件数组
            if (files != null && files.length > 0) {
                result = new String[files.length];
                int count = 0;
                for (File f : files) {
                    if (f.isFile()) {
                        result[count] = f.getAbsolutePath();
                        count++;
                    } else {
                        //如果为目录则递归调用
                        listDirectory(f);
                    }
                }
            } else {
                result = new String[0];
            }
            return result;
        }

    }

}
