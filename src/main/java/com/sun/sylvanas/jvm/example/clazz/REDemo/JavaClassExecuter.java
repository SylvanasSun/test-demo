package com.sun.sylvanas.jvm.example.clazz.REDemo;

import java.lang.reflect.Method;

/**
 * JavaClass执行类.
 * 它是提供给外部调用的入口,条用前面几个支持类组装逻辑,完成类加载工作.
 * 它只有一个execute()方法,用输入的符合Class文件格式的byte[]数组替换java.lang.System的
 * 符号引用后,使用HotSwapClassLoader加载生成一个Class对象,由于每次执行execute()方法都会
 * 生成一个新的类加载器实例,因此同一个类可以实现重复加载.
 * 然后,反射调用这个Class对象的main()方法,如果期间出现任何异常,将异常信息打印到HackSystem.out中,
 * 最后把HackSystem缓冲区中的信息作为方法的结果返回.
 * <p>
 * Created by sylvanasp on 2016/9/14.
 */
public class JavaClassExecuter {

    /**
     * 执行外部传过来的符合Class文件格式的byte[]数组
     * 将byte数组中代表java.lang.System的CONSTANT_Utf8_info常量修改为劫持后的HackSystem类.
     * 执行方法为该类的static main(String[] args)方法,输出结果为该类向System.out/err输出的信息.
     *
     * @param classByte 代表一个Java类的byte数组
     * @return 执行结果
     */
    public static String execute(byte[] classByte) {
        HackSystem.clearBuffer();
        ClassModifier cm = new ClassModifier(classByte);
        byte[] modiBytes = cm.modifyUTF8Constant("java/lang/System"
                , "com/sun/sylvanas/jvm/example/clazz/REDemo/HackSystem");
        HotSwapClassLoader loader = new HotSwapClassLoader();
        Class clazz = loader.loadByte(modiBytes);
        try {
            Method method = clazz.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new String[]{null});
        } catch (Throwable e) {
            e.printStackTrace(HackSystem.out);
        }
        return HackSystem.getBufferString();
    }

}
