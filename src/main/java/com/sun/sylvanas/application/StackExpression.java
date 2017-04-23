package com.sun.sylvanas.application;

import com.sun.sylvanas.data_struct.ArrayStack;
import com.sun.sylvanas.pattern.adapter.Token;
import com.sun.sylvanas.pattern.adapter.TokenStreamAdapter;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 使用Stack来实现表达式求值.
 * <p>
 * Created by SylvanasSun on 2017/4/23.
 */
public class StackExpression {

    private static Charset charset = Charset.forName("utf-8");
    private static ArrayStack<Integer> numbers = new ArrayStack<>(); // 操作数值栈
    private static ArrayStack<Token> operators = new ArrayStack<>(); // 操作符栈

    public static void main(String[] args) throws IOException {
        TokenStreamAdapter ts = new TokenStreamAdapter(System.in);

        // 判断输入字符的Token类型
        // 如果遇到数字就压入到值栈中;
        // 如果是操作符,则需要看前面一个的操作符的优先级是否比当前要高.
        // 如果前面优先级高则执行前一个操作符的操作;如果后面优先级高,则只需要把后面的操作符压入操作符栈即可
        while (ts.getToken().tokenType != Token.TokenType.NONE) {
            if (ts.getToken().tokenType == Token.TokenType.INT) {
                char v = (char) ts.getToken().value;
                String s = String.valueOf(v);
                numbers.push(Integer.valueOf(s));
                ts.consumeToken();
            } else {
                if (operators.peek() == null || preOrder(operators.peek().tokenType, ts.getToken().tokenType) < 0) {
                    // 如果左操作符的优先级低于右操作符(当前)
                    operators.push(ts.getToken());
                    ts.consumeToken();
                } else {
                    // 左操作符优先级较高,先进行左操作符的运算
                    binaryCalc(numbers, operators);
                    operators.push(ts.getToken());
                    ts.consumeToken();
                }
            }
        }

        while (!operators.isEmpty()) {
            binaryCalc(numbers, operators);
        }

        System.out.println("result is " + numbers.peek());
    }

    private static void binaryCalc(ArrayStack<Integer> numbers, ArrayStack<Token> operators) {
        int a = numbers.pop();
        int b = numbers.pop();

        Token oprt = operators.pop();

        int d = 0;
        if (oprt.tokenType == Token.TokenType.PLUS) {
            d = b + a;
        } else if (oprt.tokenType == Token.TokenType.MULT) {
            d = a * b;
        } else if (oprt.tokenType == Token.TokenType.MINUS) {
            d = b - a;
        } else if (oprt.tokenType == Token.TokenType.DIV) {
            d = b / a;
        } else if (oprt.tokenType == Token.TokenType.LPAR || oprt.tokenType == Token.TokenType.RPAR) {
            // 当操作符栈弹出左右括号时,将数值重新压回数栈中(只弹出括号不进行计算)
            numbers.push(b);
            numbers.push(a);
            return;
        }

        numbers.push(d);
    }

    /**
     * 判断操作符的优先级
     *
     * @return 1代表左边操作符优先级较高,-1代表右边操作符优先级较高
     */
    private static int preOrder(Token.TokenType left, Token.TokenType right) {
        // 当遇见括号时:
        // 将左括号表示为一个无穷大的操作符(括号内的操作符都将先入栈);
        // 右括号则为无穷小的操作符(将操作数出栈并开始进行计算)
        if (right == Token.TokenType.LPAR) {
            return -1;
        } else if (right == Token.TokenType.RPAR) {
            return 1;
        } else if (left == Token.TokenType.PLUS || left == Token.TokenType.MINUS) {
            if (right == Token.TokenType.MULT || right == Token.TokenType.DIV) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

}
