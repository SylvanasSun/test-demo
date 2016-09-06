package com.sun.sylvanas.jvm.example.GC;

/**
 * 内存分配及回收策略案例
 *
 * Created by sylvanasp on 2016/9/6.
 */
public class MemoryAllocationGC {

    private static final int _1MB = 1024 * 1024;

    /**
     * VM Args : -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     * -Xms20M -Xmx20M -Xmn10M 这3个参数限制了Java堆的大小为20MB,并且不可扩展,其中10MB分配给新生代,剩下的10MB分配给老年代.
     * -XX:SurvivorRatio=8 设置了新生代中 Eden区与Survivor区的空间比例为 8 : 1.
     * -XX:+PrintGCDetails 参数会告诉虚拟机在发生垃圾收集行为时打印内存回收日志。
     *
     * 新生代总可用空间为 9216KB (Eden区 + 一个Survivor区的总容量).
     *
     * 这个方法执行到allocation4对象时会发生一次Minor GC.
     * 发生的原因为:到给allocation4分配内存时,Eden已经被占用了6MB,
     * 剩余空间不足以分配allocation4所需的4MB内存,因此发生Minor GC.
     * 在GC期间,虚拟机又发现已有的3个2MB大小的对象全部无法放入Survivor空间,所以通过分配担保机制提前转移到老年代.
     * GC结束后,allocation4顺利分配在Eden中,而1、2、3则再老年代中.
     * 所以这个方法执行后结果为: Eden被占用4MB,Survivor空闲,老年代被占用6MB.
     *
     */
    public static void testAllocation() {
        byte[] allocation1,allocation2,allocation3,allocation4;

        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation4 = new byte[4 * _1MB]; // 会出现一次Minor GC
    }

    /**
     * VM Args : -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     * -XX:PretenureSizeThreshold=3145728
     *
     * -XX:PretenureSizeThreshold参数设置的值可以令大于这个值的对象直接在老年代中分配.
     * 这样做的目的是避免在Eden区及两个Survivor区之间发生大量的内存复制.
     * 这个参数不能直接写为3MB,需要写成3145728(即3MB)
     * 这个参数只对Serial和ParNew垃圾收集器有效.
     * 由于这里设置为3145728(3MB),所以allocation会被直接在老年代中进行分配.
     *
     */
    public static void testPretenureSizeThreshold() {
        byte[] allocation;
        allocation = new byte[4 * _1MB]; // 直接分配在老年代中
    }

    /**
     * VM Args : -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     * -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution
     *
     * 虚拟机给每个对象定义了一个对象年龄计数器,用于判断哪些对象放入新生代,哪些对象放入老年代中.
     * 如果对象在Eden出生并经过一次Minor GC后仍然存活,并且能被Survivor容纳的话,将被移动到Survivor空间,年龄设为1.
     * 对象在Survivor空间每经过一次Minor GC,年龄就会增加1岁,当年龄达到一定程度(默认为15)时就会晋升到老年代中.
     *
     * 虚拟机提供了-XX:MaxTenuringThreshold参数用于设置晋升老年代的年龄条件.
     *
     * allocation1需要256KB内存,由于当前Survivor(1MB)可以容纳,所以当第二次GC发生后,allocation1会晋升到老年代.
     * 如果把-XX:MaxTenuringThreshold设置为15,则第二次GC发生后,allocation1扔留在新生代Survivor空间中.
     *
     */
    public static void testTenuringThreshold() {
        byte[] allocation1,allocation2,allocation3;

        allocation1 = new byte[_1MB / 4];

        allocation2 = new byte[4 * _1MB];
        allocation3 = new byte[4 * _1MB];
        allocation3 = null;
        allocation3 = new byte[4 * _1MB];
    }

    /**
     * VM Args : -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     * -XX:MaxTenuringThreshold=15 -XX:+PrintTenuringDistribution
     *
     * 为了能更好地适应不同程序的内存状况,虚拟机并不是永远地要求对象的年龄必须达到了MaxTenuringThreshold才能晋升老年代.
     * 如果在SUrvivor空间中相同年龄所有对象的大小总和大于Survivor空间的一半,年龄大于或等于该年龄的对象就可以直接进入老年代.
     * 无须等到MaxTenuringThreshold中设置的年龄.
     *
     * 由于allocation1+allocation2的总和大于survivor空间的一半.
     * 所以在survivor空间中所有同龄的对象都会直接进入到老年代.
     *
     */
    public static void testTenuringThreshold2() {
        byte[] allocation1,allocation2,allocation3,allocation4;

        allocation1 = new byte[_1MB / 4];
        // allocation1 + allocation2 大于survivor空间的一半
        allocation2 = new byte[_1MB / 4];
        allocation3 = new byte[4 * _1MB];
        allocation4 = new byte[4 * _1MB];
        allocation4 = null;
        allocation4 = new byte[4 * _1MB];
    }

    /**
     * VM Args : -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     * -XX:+HandlePromotionFailure
     *
     * 在发生Minor GC之前,虚拟机会先检查老年代最大可用的连续空间是否大于新生代所有对象总空间.
     * 如果这个条件成立,那么Minor GC可以确保是安全的.
     * 如果不成立,则虚拟机会查看HandlePromotionFailure设置值是否允许担保失败.
     * 如果允许,那么会继续检查老年代最大可用的连续空间是否大于历次晋升到老年代对象的平均大小.
     * 如果大于,将尝试进行一次Minor GC,如果小于,或者HandlePromotionFailure设置为false.
     * 那这时则会进行一次Full GC.
     * 大部分情况下会把HandlePromotionFailure开关打开,避免Full GC过于频繁.
     *
     * 在JDK 6 Update 24之后,HandlePromotionFailure参数不会再影响到空间分配担保策略.
     * JDK 6 Update 24之后的规则变为只要老年代的连续空间大于新生代对象总大小
     * 或者历次晋升的平均大小就会进行Minor GC,否则将进行 Full GC.
     *
     */
    public static void testHandlePromotion() {
        byte[] allocation1,allocation2,allocation3,allocation4,allocation5,allocation6,allocation7;

        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation1 = null;
        allocation4 = new byte[2 * _1MB];
        allocation5 = new byte[2 * _1MB];
        allocation6 = new byte[2 * _1MB];
        allocation4 = null;
        allocation5 = null;
        allocation6 = null;
        allocation7 = new byte[2 * _1MB];
    }

}
