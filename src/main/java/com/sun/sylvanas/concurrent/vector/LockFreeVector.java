package com.sun.sylvanas.concurrent.vector;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 一个无锁的Vector实现.来源自Amino并发包
 * <p>
 * Created by sylvanasp on 2016/12/6.
 */
public class LockFreeVector<E> {
    /**
     * buckets存放了所有的内部元素.
     * 使用二维数组是为了进行动态扩展时可以更加方便.
     */
    private final AtomicReferenceArray<AtomicReferenceArray<E>> buckets;

    private final AtomicReference<Descriptor<E>> descriptor;
    private static final int N_BUCKET = 30;//二维数组中的数组数量
    private static final int FIRST_BUCKET_SIZE = 8;//每个数组的元素数量

    private static final boolean debug = false;
    private static final int zeroNumFirst = Integer.numberOfLeadingZeros(FIRST_BUCKET_SIZE);
    ;

    public LockFreeVector() {
        buckets = new AtomicReferenceArray<AtomicReferenceArray<E>>(N_BUCKET);
        buckets.set(0, new AtomicReferenceArray<E>(FIRST_BUCKET_SIZE));
        descriptor = new AtomicReference<Descriptor<E>>(new Descriptor<E>(0, null));
    }

    /**
     * 将元素压入Vector最后一个位置
     */
    public void push_back(E e) {
        Descriptor<E> desc;
        Descriptor<E> newd;
        do {
            //预防性操作,为了防止线程设置完descriptor后(58行),还没来得及执行写入(60行)
            desc = descriptor.get();
            desc.completeWrite();
            //通过当前Vector的大小(desc.size),计算新的元素应该落入哪个数组
            int pos = desc.size + FIRST_BUCKET_SIZE;
            int zeroNumPos = Integer.numberOfLeadingZeros(pos);
            int bucketInd = zeroNumFirst - zeroNumPos;

            if (buckets.get(bucketInd) == null) {
                int newLen = 2 * buckets.get(bucketInd - 1).length();
                if (debug)
                    System.out.println("New Length is:" + newLen);
                buckets.compareAndSet(bucketInd, null,
                        new AtomicReferenceArray<E>(newLen));
            }
            //获得pos的除了第一位数字1以外的其他位的数值,pos的前导零可以表示元素所在的数组,后面几位表示所在数组中的位置
            int idx = (0x80000000 >>> zeroNumPos) ^ pos;
            newd = new Descriptor<E>(desc.size + 1, new WriteDescriptor<E>(
                    buckets.get(bucketInd), idx, null, e));
        } while (!descriptor.compareAndSet(desc, newd));
        //使用descriptor将数据真正写入数组中
        descriptor.get().completeWrite();
    }

    public E get(int index) {
        int pos = index + FIRST_BUCKET_SIZE;
        int zeroNumPos = Integer.numberOfLeadingZeros(pos);
        int bucketInd = zeroNumFirst - zeroNumPos;
        int idx = (0x80000000 >>> zeroNumPos) ^ pos;
        return buckets.get(bucketInd).get(idx);
    }

    /**
     * 使用CAS操作写入新数据
     */
    static class Descriptor<E> {
        public int size;//Vector的长度
        volatile WriteDescriptor<E> writeop;

        public Descriptor(int size, WriteDescriptor<E> writeop) {
            this.size = size;
            this.writeop = writeop;
        }

        public void completeWrite() {
            WriteDescriptor<E> tmpOp = writeop;
            if (tmpOp != null) {
                tmpOp.doIt();
                writeop = null; //this is safe since all write to writeop use
                // null as r_value.
            }
        }
    }

    static class WriteDescriptor<E> {
        public E oldV;//期望值
        public E newV;//需要写入的值
        public AtomicReferenceArray<E> addr; //要修改的原子数组
        public int addr_ind;//索引

        public WriteDescriptor(AtomicReferenceArray<E> addr, int addr_ind, E oldV, E newV) {
            this.addr = addr;
            this.addr_ind = addr_ind;
            this.oldV = oldV;
            this.newV = newV;
        }

        public void doIt() {
            addr.compareAndSet(addr_ind, oldV, newV);
        }
    }
}
