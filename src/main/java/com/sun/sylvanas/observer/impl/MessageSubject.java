package com.sun.sylvanas.observer.impl;

import com.sun.sylvanas.observer.MyObserver;
import com.sun.sylvanas.observer.MySubject;

/**
 *
 * Created by sylvanasp on 2016/8/12.
 */
public class MessageSubject extends MySubject {

    private Object message;

    /**
     * 推送消息
     */
    @Override
    public void sendNotify(Object o) {
        this.setMessage(o);
    }

    public Object getMessage() {
        return message;
    }

    private void setMessage(Object message) {
        this.message = message;
        //当set消息时,同步更新到观察者
        for(MyObserver observer : super.observerList) {
            observer.update(this);
        }
    }
}
