package com.sun.sylvanas.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sylvanasp on 2016/10/19.
 */
public class ClassUtils {

    public static void main(String[] args) {
        Integer i = new Integer(1);
        printConstructionMessage(i);
        printMethodMessage(i);
        printFieldMessage(i);
    }

    public static void printMethodMessage(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(obj + "is null");
        }
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
        if (obj == null) {
            throw new IllegalArgumentException(obj + "is null");
        }
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

    public static void printConstructionMessage(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(obj + "is null");
        }
        Class<?> objClass = obj.getClass();

        Constructor<?>[] constructors = objClass.getDeclaredConstructors();
        System.out.println("--------Construction Message--------");
        for (int i = 0; i < constructors.length; i++) {
            System.out.print(constructors[i].getName() + "(");

            Class<?>[] parameterTypes = constructors[i].getParameterTypes();
            for (int j = 0; j < parameterTypes.length; j++) {
                if (j != parameterTypes.length - 1) {
                    System.out.print(parameterTypes[j].getName() + ",");
                } else {
                    System.out.print(parameterTypes[j].getName() + ")");
                }
            }
            System.out.println();
        }
    }

    public static void invokeMethod(Object obj, String methodName, Object[] params)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (methodName == null || "".equals(methodName)) {
            throw new IllegalArgumentException(methodName + "is empty");
        }
        if (obj == null) {
            throw new IllegalArgumentException(obj + "is null");
        }
        Class<?> objClass = obj.getClass();
        if (params.length == 0 || params == null) {
            Method method = objClass.getDeclaredMethod(methodName);
            method.invoke(obj);
        } else {
            Class[] paramClass = new Class[params.length];
            // 将参数数组封装到参数类类型数组中
            for (int i = 0; i < params.length; i++) {
                for (int j = 0; j < paramClass.length; j++) {
                    paramClass[j] = params[i].getClass();
                }
            }
            Method method = objClass.getDeclaredMethod(methodName, paramClass);
            method.invoke(obj, paramClass);
        }
    }

}
