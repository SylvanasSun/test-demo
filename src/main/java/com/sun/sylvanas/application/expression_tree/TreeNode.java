package com.sun.sylvanas.application.expression_tree;

/**
 * Created by SylvanasSun on 2017/5/5.
 */
public class TreeNode<T> {

    T value;
    TreeNode<T> left, right, parent;
    int size = 0;
    int order_status = 0;

    public TreeNode(T value, TreeNode left, TreeNode right, TreeNode parent, int size, int order_status) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.parent = parent;
        this.size = size;
        this.order_status = order_status;
    }

    public TreeNode(T value) {
        this.value = value;
    }

}
