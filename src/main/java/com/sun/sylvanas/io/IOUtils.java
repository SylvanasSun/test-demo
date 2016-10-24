package com.sun.sylvanas.io;

import java.io.*;

/**
 * Created by sylvanasp on 2016/10/14.
 */
public class IOUtils {

    /**
     * 输出16进制的字节到控制台,并每10个字节换一行
     */
    public static void printHex(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(file + "is null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(file + "不存在");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException(file + "不是文件");
        }
        FileInputStream inputStream = new FileInputStream(file);
        int b;
        int i = 1; //换行计数器
        while ((b = inputStream.read()) != -1) {
            if (b <= 0xf) {
                //单位数前面补0
                System.out.print("0");
            }
            System.out.print(Integer.toHexString(b & 0xff) + "  ");
            if (i++ % 10 == 0) {
                System.out.println();
            }
        }
        if(inputStream != null) {
            inputStream.close();
        }
    }

    /**
     * 输出16进制的字节到控制台,并每10个字节换一行
     */
    public static void printHextByByteArray(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(file + "is null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(file + "不存在");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException(file + "不是文件");
        }
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buf = new byte[20 * 1024];//字节缓存区,20k
        int bytes;
        int j = 1; //换行计数器
        while ((bytes = inputStream.read(buf, 0, buf.length)) != -1) {
            for (int i = 0; i < bytes; i++) {
                if (buf[i] <= 0xf) {
                    //单位数前面补0
                    System.out.print("0");
                }
                //byte类型8位,int类型32位
                //为了避免数据转换错误,通过&0xff将高24位清零
                System.out.print(Integer.toHexString(buf[i] & 0xff) + "  ");
                if (j++ % 10 == 0) {
                    System.out.println();
                }
            }
        }
        if(inputStream != null) {
            inputStream.close();
        }
    }

    /**
     * 拷贝文件
     */
    public static void copyFile(File srcFile, File destFile, boolean superadd) throws IOException {
        if (srcFile == null) {
            throw new IllegalArgumentException(srcFile + "is null");
        }
        if (destFile == null) {
            throw new IllegalArgumentException(destFile + "is null");
        }
        if (!srcFile.exists()) {
            throw new IllegalArgumentException(srcFile + "no exists");
        }
        if (!srcFile.isFile()) {
            throw new IllegalArgumentException(srcFile + "isn't file");
        }

        FileInputStream inputStream = new FileInputStream(srcFile);
        FileOutputStream out = new FileOutputStream(destFile, superadd);
        int bytes;
        byte[] buf = new byte[10 * 1024];
        while ((bytes = inputStream.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, bytes);
        }
        if(out != null) {
            out.close();
        }
        if(inputStream != null) {
            inputStream.close();
        }
    }

    public static void copyFileByBuffer(File srcFile, File destFile, boolean superadd) throws IOException {
        if (srcFile == null) {
            throw new IllegalArgumentException(srcFile + "is null");
        }
        if (destFile == null) {
            throw new IllegalArgumentException(destFile + "is null");
        }
        if (!srcFile.exists()) {
            throw new IllegalArgumentException(srcFile + "no exists");
        }
        if (!srcFile.isFile()) {
            throw new IllegalArgumentException(srcFile + "isn't file");
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile, superadd));
        byte[] buf = new byte[20 * 1024];
        int bytes;
        while ((bytes = bis.read(buf, 0, buf.length)) != -1) {
            bos.write(buf, 0, bytes);
        }
        bos.flush();
        if(bos != null) {
            bos.close();
        }
        if(bis != null) {
            bis.close();
        }
    }

    /**
     * 使用字符流拷贝文件
     */
    public static void copyFileByChar(File srcFile, File destFile, boolean superadd)
            throws IOException {
        if (srcFile == null) {
            throw new IllegalArgumentException(srcFile + "is null");
        }
        if (destFile == null) {
            throw new IllegalArgumentException(destFile + "is null");
        }
        if (!srcFile.exists()) {
            throw new IllegalArgumentException(srcFile + "no exists");
        }
        if (!srcFile.isFile()) {
            throw new IllegalArgumentException(srcFile + "isn't file");
        }

        BufferedReader br = new BufferedReader
                (new InputStreamReader(new FileInputStream(srcFile)));
        BufferedWriter bw = new BufferedWriter
                (new OutputStreamWriter(new FileOutputStream(destFile, superadd)));
        String s;
        while ((s = br.readLine()) != null) {
            bw.write(s);
            bw.newLine();
        }
        bw.flush();
        if(bw != null) {
            bw.close();
        }
        if(br != null) {
            br.close();
        }
    }

}
