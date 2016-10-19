package com.sun.sylvanas.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by sylvanasp on 2016/10/19.
 */
public class ClassUtils {

    public static void main(String[] args) {
        printMethodMessage(new Integer(1));
        printFieldMessage(new Integer(1));
    }

    public static void printMethodMessage(Object obj) {
        Class<?> objClass = obj.getClass();

        Method[] methods = objClass.getDeclaredMethods();
        System.out.println("--------Method Message--------");
        for (int i = 0; i < methods.length; i++) {
            //获得返回值信息
            Class<?> returnType = methods[i].getReturnType();
            System.out.print(returnType.getName() + " ");
            System.out.print(methods[i].getName() + "(");

            //获得参数列表
            Class<?>[] parameterTypes = methods[i].getParameterTypes();
            for (int j = 0; j < parameterTypes.length; j++) {
                if (j == parameterTypes.length - 1) {
                    System.out.print(parameterTypes[j].getName() + ")");
                } else {
                    System.out.print(parameterTypes[j].getName() + ",");
                }
            }
            System.out.println();
        }
    }

    public static void printFieldMessage(Object obj) {
        Class<?> objClass = obj.getClass();

        Field[] fields = objClass.getDeclaredFields();
        System.out.println("--------Field Message--------");
        for (int i = 0; i < fields.length; i++) {
            if (i != fields.length - 1) {
                System.out.print(fields[i].getName() + ",");
            } else {
                System.out.print(fields[i].getName() + ".");
            }
            System.out.println();
        }
    }


}
