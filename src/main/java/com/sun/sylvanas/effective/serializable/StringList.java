package com.sun.sylvanas.effective.serializable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 自定义的序列化形式,只包含链表中字符串的数目,然后紧跟着这些字符串即可.
 * 这样就构成了StringList所表示的逻辑数据,与它的物理表示细节脱离.
 * <p>
 * Created by sylvanasp on 2016/10/11.
 */
public final class StringList implements Serializable {

    /**
     * transient关键字表明这个实例域将从一个类的默认序列化形式中省略掉
     */
    private transient int size = 0;
    private transient Entry head = null;

    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    public final void add(String s) {
        // 添加规定的字符串到列表中....
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(size);

        for (Entry e = head; e != null; e = e.next) {
            s.writeObject(e.data);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numElements = s.readInt();

        for (int i = 0; i < numElements; i++) {
            add((String) s.readObject());
        }
    }

}
