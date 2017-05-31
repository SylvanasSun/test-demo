package com.sun.sylvanas.application.expression.expression_tree;

import com.sun.sylvanas.data_struct.stack.ArrayStack;
import com.sun.sylvanas.pattern.adapter.Token;
import com.sun.sylvanas.pattern.adapter.TokenStream;
import com.sun.sylvanas.pattern.adapter.TokenStreamAdapter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by SylvanasSun on 2017/5/5.
 */
public class Expression {

    private static TokenStream ts;
    private static ArrayStack<Integer> numbers = new ArrayStack<>();

    public Expression() throws IOException {
        ts = new TokenStreamAdapter(System.in);
    }

    public TreeNode<Token> create() {
        TreeNode<Token> left = term();
        Token t;

        try {
            t = ts.getToken();
            if (t.tokenType == Token.TokenType.PLUS) {
                ts.consumeToken();
                TreeNode<Token> root = new TreeNode<Token>(new Token(Token.TokenType.PLUS, "+"));
                root.left = left;
                root.right = create();
                return root;
            } else if (t.tokenType == Token.TokenType.MINUS) {
                ts.consumeToken();
                TreeNode<Token> root = new TreeNode<Token>(new Token(Token.TokenType.MINUS, "-"));
                root.left = left;
                root.right = create();
                return root;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return left;
    }

    private TreeNode<Token> term() {
        TreeNode<Token> left = factor();
        Token t;

        try {
            t = ts.getToken();
            if (t.tokenType == Token.TokenType.MULT) {
                ts.consumeToken();
                TreeNode<Token> root = new TreeNode<Token>(new Token(Token.TokenType.MULT, "*"));
                root.left = left;
                root.right = term();
                return root;
            } else if (t.tokenType == Token.TokenType.DIV) {
                ts.consumeToken();
                TreeNode<Token> root = new TreeNode<Token>(new Token(Token.TokenType.DIV, "/"));
                root.left = left;
                root.right = term();
                return root;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return left;
    }

    private TreeNode<Token> factor() {
        Token t;

        try {
            t = ts.getToken();
            if (t.tokenType == Token.TokenType.INT) {
                ts.consumeToken();
                return new TreeNode<Token>(t);
            } else if (t.tokenType == Token.TokenType.LPAR) {
                ts.consumeToken();
                // 如果为左括号,继续递归调用
                TreeNode<Token> v = create();
                if (!matchType(Token.TokenType.RPAR))
                    assert false;
                else
                    ts.consumeToken();
                return v;
            } else {
                throw new IllegalArgumentException("Expression invalid!!!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean matchType(Token.TokenType tt) throws IOException {
        if (ts.getToken().tokenType == tt) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        Expression expression = new Expression();
        BinarySearchTree<Token> bst = new BinarySearchTree<>();
        // 构建四则运算语法树
        bst.root = expression.create();
        // 使用后序遍历整个四则运算语法树并进行对应的计算
        Queue<Token> queue = bst.postOrder();
        Iterator<Token> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            Integer a, b, r;
            if (token.tokenType == Token.TokenType.INT) {
                numbers.push(token.intValue());
            } else {
                if (token.tokenType == Token.TokenType.PLUS) {
                    b = numbers.pop();
                    a = numbers.pop();
                    r = a + b;
                    numbers.push(r);
                } else if (token.tokenType == Token.TokenType.MINUS) {
                    b = numbers.pop();
                    a = numbers.pop();
                    r = a - b;
                    numbers.push(r);
                } else if (token.tokenType == Token.TokenType.MULT) {
                    b = numbers.pop();
                    a = numbers.pop();
                    r = a * b;
                    numbers.push(r);
                } else if (token.tokenType == Token.TokenType.DIV) {
                    b = numbers.pop();
                    a = numbers.pop();
                    r = a / b;
                    numbers.push(r);
                }
            }
        }
        System.out.println("result is " + numbers.pop());
    }

}
