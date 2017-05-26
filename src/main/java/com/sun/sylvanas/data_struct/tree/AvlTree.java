package com.sun.sylvanas.data_struct.tree;

import java.io.Serializable;

/**
 * Created by SylvanasSun on 2017/5/26.
 */
public class AvlTree<K extends Comparable<K>, V> extends BinaryTree<K, V> implements Serializable, Iterable<K> {

    private static final long serialVersionUID = 8168921537293164615L;

    private AvlNode<K, V> root;

    private class AvlNode<K, V> extends Node<K, V> {
        private int depth = 1;
        private int balance = 0;

        public AvlNode(K key, V value, int size, Node<K, V> parent, Node<K, V> left, Node<K, V> right) {
            super(key, value, size, parent, left, right);
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }



}
