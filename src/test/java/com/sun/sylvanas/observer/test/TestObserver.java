package com.sun.sylvanas.observer.test;

import com.sun.sylvanas.observer.MyObserver;
import com.sun.sylvanas.observer.MySubject;
import com.sun.sylvanas.observer.impl.MessageObserver;
import com.sun.sylvanas.observer.impl.MessageSubject;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Created by sylvanasp on 2016/8/12.
 */
public class TestObserver {

    private MyObserver observer;

    private MySubject subject;

    @Before
    public void initializer() {
        observer = new MessageObserver();
        subject = new MessageSubject();
        ((MessageObserver)observer).registerName("sun");
    }

    @Test
    public void test01() {
        subject.register(observer);
        ((MessageSubject)subject).sendNotify("Hello World");
    }

}
