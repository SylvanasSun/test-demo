package com.sun.sylvanas.jvm.example.clazz.executionEngine;

import java.lang.invoke.*;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * invokedynamic指令案例,与MethodHandleTest的作用基本上一致.
 *
 * Created by sylvanasp on 2016/9/13.
 */
public class InvokeDynamicTest {

    public static void main(String[] args) throws Throwable {
        INDY_BootstrapMethod().invokeExact("sylvanas");
    }

    public static void testMethod(String s) {
        System.out.println("hello String:" + s);
    }

    /**
     * 引导方法,返回值为java.lang.invoke.CallSite对象.
     * 这个代表真正要执行的目标方法调用.
     */
    public static CallSite bootstrapMethod(MethodHandles.Lookup lookup, String name, MethodType mt)
            throws NoSuchMethodException, IllegalAccessException {
        return new ConstantCallSite(lookup.findStatic(InvokeDynamicTest.class,name,mt));
    }

    private static MethodType MT_BootstrapMethod() {
        return MethodType.fromMethodDescriptorString
                ("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)" +
                        "Ljava/lang/invoke/CallSite;",
                null);
    }

    private static MethodHandle MH_BootstrapMethod()
            throws NoSuchMethodException, IllegalAccessException {
        return lookup().findStatic(InvokeDynamicTest.class,"bootstrapMethod",MT_BootstrapMethod());
    }

    private static MethodHandle INDY_BootstrapMethod()
            throws Throwable {
        CallSite cs = (CallSite) MH_BootstrapMethod().invokeWithArguments(lookup(),"testMethod",
                MethodType.fromMethodDescriptorString("(Ljava/lang/String;)V",null));
        return cs.dynamicInvoker();
    }

}
