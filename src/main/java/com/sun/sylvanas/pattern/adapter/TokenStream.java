package com.sun.sylvanas.pattern.adapter;

import java.io.IOException;

/**
 * Created by SylvanasSun on 2017/4/23.
 */
public interface TokenStream {

    Token getToken() throws IOException; // 返回当前指针指向的Token

    void consumeToken(); // 使当前指针指向下一位

}
