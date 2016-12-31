package com.sun.sylvanas.annotation.processor;

import com.sun.sylvanas.annotation.Id;
import com.sun.sylvanas.annotation.Persistent;
import com.sun.sylvanas.annotation.Property;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Set;

/**
 * 通过处理@Persistent,@Id,@Property三个注解来生成一个Hibernate映射文件
 * 可以使用javac -processor APT处理器 需要处理的类名 来调用这个处理器
 * 例: javac -processor HibernateAnnotationProcessor Person.java
 * <p>
 * Created by sylvanasp on 2016/12/31.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"Persistent", "Id", "Property"}) //指定处理的注解
public class HibernateAnnotationProcessor extends AbstractProcessor {
    //循环处理每个需要处理的程序对象
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //定义一个文件输出流
        PrintStream printStream = null;
        try {
            //遍历每个被@Persistent修饰的class文件
            for (Element t : roundEnv.getElementsAnnotatedWith(Persistent.class)) {
                //获取正在处理的类名
                Name clazzName = t.getSimpleName();
                //获取类定义前的@Persistent Annotation
                Persistent persistent = t.getAnnotation(Persistent.class);
                //创建文件输出流
                printStream = new PrintStream(new FileOutputStream(clazzName + ".hbm.xml"));
                //进行输出
                printStream.println("<?xml version=\"1.0\"?>");
                printStream.println("<!DOCTYPE hibernate-mapping PUBLIC");
                printStream.println("   \"-//Hibernate/Hibernate "
                        + "Mapping DTD 3.0//EN\"");
                printStream.println("   \"http://www.hibernate.org/dtd/"
                        + "hibernate-mapping-3.0.dtd\">");
                printStream.println("<hibernate-mapping>");
                printStream.print("   <class name=\"" + t);
                //输出persistent注解中的table()值
                printStream.println("\" table=\"" + persistent.table() + "\">");
                for (Element f : t.getEnclosedElements()) {
                    //只处理成员变量上的Annotation
                    if (f.getKind() == ElementKind.FIELD) {
                        //获取成员变量定义前的@Id Annotation
                        Id id = f.getAnnotation(Id.class);
                        //当@Id Annotation存在时输出<id.../>
                        if (id != null) {
                            printStream.println("       <id name=\""
                                    + f.getSimpleName()
                                    + "\" column=\"" + id.column()
                                    + "\" type=\"" + id.type() + "\">");
                            printStream.println("       <generator class=\""
                                    + id.generator() + "\"/>");
                            printStream.println("       </id>");
                        }
                        //获取成员变量定义前的@Property Annotation
                        Property property = f.getAnnotation(Property.class);
                        if (property != null) {
                            printStream.println("       <property name=\""
                                    + f.getSimpleName()
                                    + "\" column=\"" + property.column()
                                    + "\" type=\"" + property.type() + "\"/>");
                        }
                    }
                    printStream.println("   </class>");
                    printStream.println("</hibernate-mapping>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printStream != null)
                printStream.close();
        }
        return true;
    }
}
