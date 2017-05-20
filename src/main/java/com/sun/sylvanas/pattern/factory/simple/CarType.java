package com.sun.sylvanas.pattern.factory.simple;

import java.lang.annotation.*;

/**
 * Created by SylvanasSun on 2017/5/20.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CarType {

    String typeName() default "";

}
