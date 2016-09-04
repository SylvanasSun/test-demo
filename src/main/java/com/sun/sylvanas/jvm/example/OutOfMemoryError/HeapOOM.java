package com.sun.sylvanas.jvm.example.OutOfMemoryError;

import java.util.ArrayList;
import java.util.List;

/**
 * Java堆内存溢出,Java堆内存的OOM异常是实际应用中常见的内存溢出异常情况.
 *
 * 堆内存用于存储对象实例,只要不断地创建对象,并且保证GC Roots到对象之间有可达路径来避免
 * 垃圾回收机制清除这些对象,这样就可以在对象数量到达最大堆的容量限制后产生内存溢出异常.
 *
 * 以下启动参数中,-Xms20 -Xmx20m 将堆的最小值与最大值设置为一样的,既可避免自动扩展.
 * -XX:+HeapDumpOnOutOfMemoryError可以让虚拟机在出现内存溢出异常时Dump出当前的内存堆转储快照以便分析.
 *
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * <p>
 * Created by sylvanasp on 2016/9/4.
 */
public class HeapOOM {

    static class OOMObject {

    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<OOMObject>();

        while (true) {
            list.add(new OOMObject());
        }
    }

}
