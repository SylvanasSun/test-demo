package com.sun.sylvanas.annotation.bean;

import com.sun.sylvanas.annotation.Id;
import com.sun.sylvanas.annotation.Persistent;
import com.sun.sylvanas.annotation.Property;

/**
 * 一个普通的JavaBean,使用@Persistent、@Id、@Property三个注解修饰表和字段
 * Created by sylvanasp on 2016/12/31.
 */
@Persistent(table = "test_person")
public class Person {
    @Id(column = "person_id", type = "integer", generator = "identity")
    private int id;
    @Property(column = "person_name", type = "string")
    private String name;
    @Property(column = "person_age", type = "integer")
    private int age;

    public Person() {
    }

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
