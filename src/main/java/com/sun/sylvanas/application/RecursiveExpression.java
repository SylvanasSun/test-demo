package com.sun.sylvanas.application;

import com.sun.sylvanas.pattern.adapter.Token;
import com.sun.sylvanas.pattern.adapter.TokenStream;
import com.sun.sylvanas.pattern.adapter.TokenStreamAdapter;

import java.io.IOException;

/**
 * 使用递归实现表达式求值:
 * expr := term (+|-) term (+|-) .... (+|-) term
 * term := factor (*|/) facotr (*|/) .... (*|/) factor
 * factor := INT | "(" expr ")"
 * <p>
 * Created by SylvanasSun on 2017/4/25.
 */
public class RecursiveExpression {

    private TokenStream ts;

    public RecursiveExpression() throws IOException {
        this.ts = new TokenStreamAdapter(System.in);
    }

    public int evalue() {
        int t = term();

        Token op = null;

        try {
            op = ts.getToken();

            while (op.tokenType == Token.TokenType.PLUS || op.tokenType == Token.TokenType.MINUS) {
                ts.consumeToken();
                int t2 = term();
                if (op.tokenType == Token.TokenType.PLUS) {
                    t += t2;
                } else {
                    t -= t2;
                }
                op = ts.getToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    private int term() {
        int t = factor();

        Token op = null;

        try {
            op = ts.getToken();

            while (op.tokenType == Token.TokenType.MULT || op.tokenType == Token.TokenType.DIV) {
                ts.consumeToken();
                int t2 = factor();
                if (op.tokenType == Token.TokenType.MULT) {
                    t *= t2;
                } else {
                    t /= t2;
                }
                op = ts.getToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    private int factor() {
        Token t = null;

        try {
            t = ts.getToken();

            if (t.tokenType == Token.TokenType.INT) {
                //如果为一个数字,直接返回
                ts.consumeToken();
                Integer r = Integer.valueOf(String.valueOf(t.value));
                return r;
            } else if (t.tokenType == Token.TokenType.LPAR) {
                // 如果为左括号则先递归计算括号内的表达式
                ts.consumeToken();
                int v = evalue();
                match(ts.getToken(), Token.TokenType.RPAR);
                return v;
            } else if (t.tokenType == Token.TokenType.MINUS) {
                ts.consumeToken();
                return 0 - factor();
            } else {
                throw new IOException("Illegal Expression!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void match(Token t, Token.TokenType tokenType) {
        assert t.tokenType == tokenType;
        ts.consumeToken();
    }

    public static void main(String[] args) throws IOException {
        RecursiveExpression e = new RecursiveExpression();
        System.out.println("result is " + e.evalue());
    }

}
