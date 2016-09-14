package com.sun.sylvanas.jvm.example.clazz.REDemo;

/**
 * 为了多次载入执行类而加入的加载器
 * 把defineClass方法开放出来,只有外部显式调用的时候才会使用到loadByte方法
 * 由虚拟机调用时,仍然按照原有的双亲委派规则使用loadClass方法进行类加载.
 * 这个支持类的作用为: 为了实现同一个类的代码可以被多次加班.
 * <p>
 * Created by sylvanasp on 2016/9/14.
 */
public class HotSwapClassLoader extends ClassLoader {

    /**
     * 在构造函数中,指定为当前类(HotSwapClassLoader)的类加载器作为父类加载器.
     * 这一步是实现提交的执行代码可以访问服务端引用类库的关键.
     */
    public HotSwapClassLoader() {
        super(HotSwapClassLoader.class.getClassLoader());
    }

    /**
     * 用于公开父类中的protected方法defineClass().
     * 这个方法会把提交执行的Java类的byte[]数组转变为Class对象.
     */
    public Class loadByte(byte[] classByte) {
        return defineClass(null, classByte, 0, classByte.length);
    }

}
