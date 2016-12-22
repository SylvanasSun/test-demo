package com.sun.sylvanas.io.file;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 使用FileVisitor遍历文件和目录.
 * Files类提供如下两个方法遍历文件和子目录
 * walkFileTree(Path start,FileVisitor<? super Path> visitor); 遍历start路径下的所有文件和子目录.
 * walkFileTree(Path start,Set<FileVisitOption> options,int maxDepth,FileVisitor<? super Path>)
 * 与第一个方法类似,该方法最多遍历maxDepth深度的文件.
 * <p>
 * Created by sylvanasp on 2016/12/22.
 */
public class FileVisitorDemo {
    public static void main(String[] args) throws IOException {
        //遍历d:\test目录下的所有文件和子目录
        Files.walkFileTree(Paths.get("d:", "test"), new SimpleFileVisitor<Path>() {
            // 访问file文件时触发该方法
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("正在访问" + file + "文件");
                //找到a.txt文件
                if (file.endsWith("a.txt")) {
                    System.out.println("找到目标文件.");
                    return FileVisitResult.TERMINATE; //代表中止访问的后续行为.
                }
                return FileVisitResult.CONTINUE; //代表继续访问的后续行为.
            }

            //开始访问目录时触发该方法
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("正在访问" + dir + "路径");
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
