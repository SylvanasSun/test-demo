package com.sun.sylvanas.data_struct.tree;

import java.util.Iterator;

/**
 * The {@code RedBlackTree} class represents an ordered symbol table of generic
 * key-value pairs.
 * This implements uses a left-leaning red-black Binary Search Tree.
 *
 * Created by SylvanasSun on 2017/6/1.
 *
 * @author SylvanasSun
 */
public class RedBlackTree<K extends Comparable<K>, V> implements Iterable<K> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private Node root;

    private class Node {
        private int size = 0;
        private boolean color = true;
        private Node parent, left, right;
        private int orderStatus = 0;
        private K key;
        private V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Return the number of key-value pairs of the this red black tree.
     */
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        return x != null ? x.size : 0;
    }

    /**
     * This red black tree is empty?
     *
     * @return if {@code true} represent is empty,{@code false} otherwise
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Returns the number of this red black tree height.
     *
     * @return the number of this red black tree height
     */
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null)
            return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key
     * @return the value associated with the given key if the key is in the symbol table
     * and {@code null} if  the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public V get(K key) {
        checkKeyIsNull(key, "called get() function use the key is null.");
        return getValueAssociatedWithKey(key);
    }

    private V getValueAssociatedWithKey(K key) {
        Node x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else
                return x.value;
        }
        return null;
    }

    /**
     * Does this symbol table contain the given key?
     *
     * @param key the key
     * @return {@code true} if this symbol table contains {@code key}
     * and {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(K key) {
        checkKeyIsNull(key, "called contains() function use the key is null.");
        return getValueAssociatedWithKey(key) != null;
    }

    private void checkKeyIsNull(K key, String message) {
        if (key == null)
            throw new IllegalArgumentException(message);
    }

    private void fixAfterInsertion(Node x) {
        while (x != null && x != root && parentOf(x).color == RED) {
            if (parentOf(x) == grandpaOf(x).left) {
                x = parentIsLeftNode(x);
            } else {
                x = parentIsRightNode(x);
            }
        }
    }

    private Node parentIsLeftNode(Node x) {
        Node xUncle = grandpaOf(x).right;
        if (xUncle.color == RED) {
            x = brotherNodeIsRed(x, xUncle);
        } else {
            if (x == parentOf(x).right) {
                x = parentOf(x);
                rotateLeft(x);
            }
            rotateRight(grandpaOf(x));
        }
        return x;
    }

    private Node parentIsRightNode(Node x) {
        Node xUncle = grandpaOf(x).left;
        if (xUncle.color == RED) {
            x = brotherNodeIsRed(x, xUncle);
        } else {
            if (x == parentOf(x).left) {
                x = parentOf(x);
                rotateRight(x);
            }
            rotateLeft(grandpaOf(x));
        }
        return x;
    }

    private Node brotherNodeIsRed(Node x, Node xUncle) {
        xUncle.color = BLACK;
        parentOf(x).color = BLACK;
        grandpaOf(x).color = RED;
        return grandpaOf(x);
    }

    private void setColor(Node x, boolean color) {
        if (x != null)
            x.color = color;
    }

    private Node rotateLeft(Node x) {
        Node t = x.right;
        x.right = t.left;
        t.left = x;
        swapParent(x, t);
        swapColorAndSize(x, t);
        return t;
    }

    private Node rotateRight(Node x) {
        Node t = x.left;
        x.left = t.right;
        t.right = x;
        swapParent(x, t);
        swapColorAndSize(x, t);
        return t;
    }

    private void swapColorAndSize(Node x, Node t) {
        t.color = x.color;
        x.color = RED;
        t.size = x.size;
        x.size = 1 + size(x.left) + size(x.right);
    }

    private void swapParent(Node x, Node t) {
        Node xParent = x.parent;
        t.parent = xParent;
        if (xParent == null)
            root = t;
        else {
            if (x == xParent.left)
                xParent.left = t;
            else
                xParent.right = t;
        }
        x.parent = t;
    }

    private Node parentOf(Node x) {
        return x == null ? null : x.parent;
    }

    private Node grandpaOf(Node x) {
        return x == null ? null : x.parent.parent;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }

}
