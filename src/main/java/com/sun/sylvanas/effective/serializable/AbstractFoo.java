package com.sun.sylvanas.effective.serializable;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 一个不可序列化但可扩展的基类
 * <p>
 * Created by sylvanasp on 2016/10/11.
 */
public class AbstractFoo {

    private int x, y;

    //状态枚举
    private enum State {
        NEW, INITIALIZING, INITIALIZED
    }

    /**
     * 使用原子引用确保对象的完整性
     */
    private final AtomicReference<State> init = new AtomicReference<State>(State.NEW);

    public AbstractFoo(int x, int y) {
        initialize(x, y);
    }

    protected AbstractFoo() {
    }

    protected final void initialize(int x, int y) {
        if (!init.compareAndSet(State.NEW, State.INITIALIZING)) {
            throw new IllegalStateException("Already initialized");
        }
        this.x = x;
        this.y = y;

        init.set(State.INITIALIZED);
    }

    protected final int getX() {
        checkInit();
        return x;
    }

    protected final int getY() {
        checkInit();
        return y;
    }

    private void checkInit() {
        if (init.get() != State.INITIALIZED) {
            throw new IllegalStateException("Uninitialized");
        }
    }

}
