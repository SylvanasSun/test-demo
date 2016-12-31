package com.sun.sylvanas.annotation;

import java.lang.annotation.*;

/**
 * Created by sylvanasp on 2016/12/31.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Persistent {
    String table();
}
