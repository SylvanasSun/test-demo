package com.sun.sylvanas.application.expression.expression_tree;

import com.sun.sylvanas.data_struct.stack.ArrayStack;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Created by SylvanasSun on 2017/5/5.
 */
public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {

    TreeNode<T> root;

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size(root);
    }

    private int size(TreeNode n) {
        if (n == null)
            return 0;
        else
            return n.size;
    }

    public T min() {
        TreeNode t = root;
        while (t.left != null) {
            t = t.left;
        }
        return (T) t.value;
    }

    public T max() {
        TreeNode t = root;
        while (t.right != null) {
            t = t.right;
        }
        return (T) t.value;
    }

    public void add(T t) {
        if (t == null)
            throw new IllegalArgumentException(this.getClass().getName() + " called add(T t) arguments is null.");

        TreeNode x = root;
        int cmp = 0;
        while (x != null) {
            cmp = t.compareTo((T) x.value);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else
                x.value = t;
        }
        TreeNode<T> n = new TreeNode<>(t, null, null, x, 1, 0);
        if (cmp < 0)
            x.left = n;
        else if (cmp > 0)
            x.right = n;
        x.size = size(x.left) + size(x.right) + 1;
    }

    public T remove(T t) {
        if (t == null)
            throw new IllegalArgumentException(this.getClass().getName() + " called remove(T t) arguments is null.");
        if (isEmpty())
            throw new NoSuchElementException(this.getClass().getName() + " binary search tree is empty.");

        TreeNode x = root;
        T value = null;
        int cmp = 0;
        while (x != null) {
            cmp = t.compareTo((T) x.value);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else {
                value = (T) x.value;
                if (x.left != null && x.right != null) {
                    TreeNode successor = successor(x);
                    x.value = successor.value;
                    x = successor;
                }
                TreeNode replacement = (x.left != null) ? x.left : x.right;
                removeSingleNode(x, replacement);
            }
        }
        return value;
    }

    private void removeSingleNode(TreeNode x, TreeNode replacement) {
        if (replacement == null) {
            TreeNode p = x.parent;
            if (x == p.left)
                p.left = null;
            else
                p.right = null;
            computeSize(p);
        } else if (x.parent == null) {
            root = null;
        } else {
            TreeNode p = x.parent;
            replacement.parent = p;
            if (x == root)
                root = replacement;
            else if (x == p.left)
                p.left = replacement;
            else
                p.right = replacement;
            computeSize(replacement);
        }
    }

    private TreeNode successor(TreeNode x) {
        TreeNode right = x.right;
        if (right != null) {
            while (right.left != null)
                right = right.left;
            return right;
        } else {
            TreeNode p = x.parent;
            TreeNode t = x;
            while (p != null && t != p.left) {
                t = p;
                p = p.parent;
            }
            return p;
        }
    }

    private void computeSize(TreeNode x) {
        while (x != null) {
            x.size = 1 + size(x.left) + size(x.right);
            x = x.parent;
        }
    }

    public Queue<T> postOrder() {
        if (root == null)
            return null;

        ArrayStack<TreeNode<T>> stack = new ArrayStack<>();
        ArrayDeque<T> deque = new ArrayDeque<>();
        TreeNode<T> current;
        stack.push(root);

        while (!stack.isEmpty()) {
            current = stack.peek();
            if (current.order_status == 0) {
                if (current.left != null)
                    stack.push(current.left);
                current.order_status = 1;
            } else if (current.order_status == 1) {
                if (current.right != null)
                    stack.push(current.right);
                current.order_status = 2;
            } else if (current.order_status == 2) {
                deque.add(current.value);
                current.order_status = 3;
            } else if (current.order_status == 3) {
                stack.pop();
                current.order_status = 0;
            }
        }
        return deque;
    }

    @Override
    public Iterator<T> iterator() {
        return new BSTIterator();
    }

    private class BSTIterator implements Iterator<T> {
        private TreeNode<T> x = root;

        public BSTIterator() {
            while (x.left != null)
                x = x.left;
        }

        @Override
        public boolean hasNext() {
            return x != null;
        }

        @Override
        public T next() {
            T value = x.value;
            x = successor(x);
            return value;
        }
    }
}
