package com.sun.sylvanas.application.hello_aop;

/**
 * 使用AspectJ的语法,注意这里的修饰符不是class而是aspect,并且文件后缀名为aj
 * Created by SylvanasSun on 2017/6/7.
 */
public aspect SomethingAspect {

    /**
     * 切入点,切入到Something.say()
     */
    pointcut recordLog():call(* com.sun.sylvanas.application.hello_aop.Something.say(..));

    /**
     * 在方法执行后执行
     */
    after():recordLog() {
        System.out.println("[AFTER] Record log...");
    }

}
