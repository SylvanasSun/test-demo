package com.sun.sylvanas.clazz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 自定义类加载器,重写了findClass()方法来实现自定义的类加载机制.
 * 这个ClassLoader可以在加载类之前先编译该类的源文件,
 * 从而实现运行Java之前先编译该程序的目标,即可通过该ClassLoader直接运行Java源文件.
 * <p>
 * Created by sylvanasp on 2017/1/5.
 */
public class CompileClassLoader extends ClassLoader {

    /**
     * 读取一个文件的内容
     */
    private byte[] getBytes(String filename) throws IOException {
        File file = new File(filename);
        long len = file.length();
        byte[] raw = new byte[(int) len];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            //一次性读取Class文件的全部二进制数据
            int read = inputStream.read(raw);
            if (read != len)
                throw new IOException("无法读取文件全部二进制数据: " + read + " != " + len);
            return raw;
        }
    }

    /**
     * 编译指定Java文件
     */
    private boolean compile(String javaFile) throws IOException {
        System.out.println("CompileClassLoader execute compile: " + javaFile + "...");
        //调用系统的javac命令
        Process process = Runtime.getRuntime().exec("javac " + javaFile);
        try {
            //其它线程等待这个线程完成
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取javac的退出值
        int exitValue = process.exitValue();
        return exitValue == 0;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        //将包路径中的(.)替换成(/)
        String fileStub = name.replace(".", "/");
        String javaFilename = fileStub + ".java";
        String classFilename = fileStub + ".class";
        File javaFile = new File(javaFilename);
        File classFile = new File(classFilename);
        /*
            当指定Java源文件存在,且Class文件不存在,
            或Java源文件的修改时间比Class文件的修改时间更晚时,重新编译
         */
        if (javaFile.exists() &&
                (!classFile.exists() || javaFile.lastModified() > classFile.lastModified())) {
            try {
                //如果编译失败或者Class文件不存在
                if (!compile(javaFilename) || !classFile.exists()) {
                    throw new ClassNotFoundException("ClassNotFoundException: " + javaFilename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //如果Class文件存在,系统负责将该文件转换成Class对象
        if (classFile.exists()) {
            try {
                //将Class文件的二进制数据读入数组
                byte[] raw = getBytes(classFilename);
                //调用ClassLoader的defineClass()将二进制数据转换成Class对象
                clazz = defineClass(name, raw, 0, raw.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //如果clazz为null,则加载失败
        if (clazz == null)
            throw new ClassNotFoundException(name);
        return clazz;
    }
}
