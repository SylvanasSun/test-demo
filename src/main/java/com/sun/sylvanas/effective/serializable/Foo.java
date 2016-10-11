package com.sun.sylvanas.effective.serializable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * AbstractFoo的序列化子类
 * <p>
 * Created by sylvanasp on 2016/10/11.
 */
public class Foo extends AbstractFoo implements Serializable {

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        int x = s.readInt();
        int y = s.readInt();
        initialize(x, y);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        s.writeInt(getX());
        s.writeInt(getY());
    }

    public Foo(int x, int y) {
        super(x, y);
    }

    private static final long serialVersionUID = 1856835860954L;

}
