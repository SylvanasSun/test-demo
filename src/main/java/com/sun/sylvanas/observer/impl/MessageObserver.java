package com.sun.sylvanas.observer.impl;

import com.sun.sylvanas.observer.MyObserver;
import com.sun.sylvanas.observer.MySubject;

/**
 *
 * Created by sylvanasp on 2016/8/12.
 */
public class MessageObserver implements MyObserver {

    private String observerName;

    private Object message;

    /**
     * 基于push模型
     */
    public void update(Object content) {
        if (content instanceof String) {
            this.message = (String) content;
            if (observerName != null && !"".equals(observerName)) {
                System.out.println("Content is " + this.message + " listener by " + this.observerName);
            } else {
                System.out.println("Content is " + this.message);
            }
        } else {
            System.out.println("Not find Message!");
        }
    }


    /**
     * 基于pull模型
     */
    public void update(MySubject subject) {
        this.message = ((MessageSubject) subject).getMessage();
        if (observerName != null && !"".equals(observerName)) {
            System.out.println("Content is " + this.message + " listener by " + this.observerName);
        } else {
            System.out.println("Content is " + this.message);
        }
    }

    public void registerName(String name) {
        this.setObserverName(name);
    }

    public Object getMessage() {
        return message;
    }

    private void setMessage(Object message) {
        this.message = message;
    }

    public String getObserverName() {
        return observerName;
    }

    private void setObserverName(String observerName) {
        this.observerName = observerName;
    }

}
