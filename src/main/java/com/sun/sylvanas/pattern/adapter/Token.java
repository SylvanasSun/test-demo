package com.sun.sylvanas.pattern.adapter;

/**
 * TokenStream中的Token对象,这个对象记录了游标所指向的类型与值.
 * <p>
 * Created by SylvanasSun on 2017/4/23.
 */
public class Token {

    public enum TokenType {
        LPAR, //左括号
        RPAR,  //右括号
        PLUS, //加
        MINUS, //减
        MULT, //乘
        DIV, //除
        INT, //整型数字
        NONE //空值
    }

    public TokenType tokenType;
    public Object value;

    public Token(TokenType tokenType, Object value) {
        this.tokenType = tokenType;
        this.value = value;
    }

}
