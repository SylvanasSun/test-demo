package com.sun.sylvanas.utils.model;

/**
 * 用于过滤提取出来的URL
 *
 * Created by sylvanasp on 2016/10/27.
 */
public interface LinkFilter {

    public boolean accept(String url);

}
