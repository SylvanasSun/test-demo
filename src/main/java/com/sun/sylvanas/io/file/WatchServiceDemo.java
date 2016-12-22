package com.sun.sylvanas.io.file;

import java.io.IOException;
import java.nio.file.*;

/**
 * NIO.2的Path类提供了如下一个方法来监听文件系统的变化:
 * register(WatchService watcher,WatchEvent.Kind<?> ...events);
 * 使用watcher监听该path代表的目录下的文件变化,events参数指定要监听哪些类型的事件.
 * WatchService有以下三个方法来获取被监听目录的文件变化事件.
 * WatchKey poll():获取下一个WatchKey,如果没有WatchKey发生就立即返回null.
 * WatchKey poll(long timeout,TimeUnit unit):尝试等待timeout时间去获取下一个WatchKey.
 * WatchKey take():获取下一个WatchKey,如果没有WatchKey发生则一直等待.
 * <p>
 * Created by sylvanasp on 2016/12/22.
 */
public class WatchServiceDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        //获得文件系统的WatchService对象
        WatchService watchService = FileSystems.getDefault().newWatchService();
        //为D:/test/路径注册监听
        Paths.get("d:", "test").register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        while (true) {
            //获取下一个文件变化事件
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(event.context() + "文件发生了" + event.kind() + "事件.");
            }
            //重设WatchKey
            boolean valid = key.reset();
            //重设失败,退出监听.
            if (!valid) {
                break;
            }
        }
    }
}
