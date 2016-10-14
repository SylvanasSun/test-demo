package com.sun.sylvanas.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
        inputStream.close();
    }

    /**
     * 输出16进制的字节到控制台,并每10个字节换一行
     */
    public static void printHextByByteArray(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException(file + "is null");
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
        inputStream.close();
    }

}
