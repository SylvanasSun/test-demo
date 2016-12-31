package com.sun.sylvanas.annotation;

import java.lang.annotation.*;

/**
 * Created by sylvanasp on 2016/12/31.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Id {
    String column();

    String type();

    String generator();
}
