package com.sun.sylvanas.pattern.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TokenStream的适配器,将传入的InputStream与TokenStream一起协同工作.
 * <p>
 * Created by SylvanasSun on 2017/4/23.
 */
public class TokenStreamAdapter implements TokenStream {

    private byte[] buffer = new byte[1024];

    private char[] chars = new char[1024];

    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    private Charset charset = Charset.forName("utf-8");

    private static final String REGEX_NUMBER = "^[0-9]$";

    private InputStream in;

    private int point = 0;

    public TokenStreamAdapter(InputStream in) throws IOException {
        this.in = in;
        // 读取输入的数据并转换成字符数组
        in.read(buffer);
        byteBuffer.put(buffer);
        byteBuffer.flip();
        chars = charset.decode(byteBuffer).array();
    }

    @Override
    public Token getToken() throws IOException {
        // 判断当前指针指向的Token类型并创建返回Token
        char c = chars[point];
        if (c == '+') {
            return new Token(Token.TokenType.PLUS, c);
        } else if (c == '-') {
            return new Token(Token.TokenType.MINUS, c);
        } else if (c == '*') {
            return new Token(Token.TokenType.MULT, c);
        } else if (c == '/') {
            return new Token(Token.TokenType.DIV, c);
        } else if (c == '(') {
            return new Token(Token.TokenType.LPAR, c);
        } else if (c == ')') {
            return new Token(Token.TokenType.RPAR, c);
        } else {
            // 判断是否为数字
            boolean flag = matchNumber(c);
            if (flag) {
                return new Token(Token.TokenType.INT, c);
            } else {
                return new Token(Token.TokenType.NONE, c);
            }
        }
    }

    @Override
    public void consumeToken() {
        point++;
    }

    private boolean matchNumber(char c) {
        String s = String.valueOf(c);
        Pattern pattern = Pattern.compile(REGEX_NUMBER);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}
