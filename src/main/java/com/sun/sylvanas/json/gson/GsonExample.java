package com.sun.sylvanas.json.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Gson 案例Demo
 * Created by sylvanasp on 2016/8/31.
 */
public class GsonExample {

    public static void main(String[] args) throws IOException {
        beanToJSON();
        jsonToBean();
    }

    private static void beanToJSON() {
        Animal animal = new Animal();
        animal.setName("小白");
        animal.setSex("男");
        animal.setAge(3);
        animal.setBirthday(new Date());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        Gson gson = gsonBuilder.create();

        String json = gson.toJson(animal);
        System.out.println(json);
    }

    public static void jsonToBean() throws IOException {
        File file = new File(GsonExample.class.getClassLoader().getResource("animal.json").getPath());
        String json = FileUtils.readFileToString(file, "UTF-8");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        Gson gson = gsonBuilder.create();

        Animal animal = gson.fromJson(json, Animal.class);
        System.out.println(animal);
    }

}
