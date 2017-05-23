package com.sun.sylvanas.data_struct.tree;

import java.io.Serializable;
import java.util.Iterator;

/**
 * The {@code BinaryTree} class represents an ordered symbol table of generic
 * key-value pairs.
 * This implementation uses an (unbalanced) binary search tree.
 * It requires that the key type implements the {@code Comparable} interface and
 * calls the {@code compareTo()} and method to compare two keys.
 * It does not call either {@code equals()} or {@code hashCode()}.
 * <p>
 * Created by SylvanasSun on 2017/5/23.
 */
public class BinaryTree<K extends Comparable<K>, V> implements Serializable, Iterable<K> {

    private static final long serialVersionUID = -712550370847248659L;

    private Node<K, V> root;

    public BinaryTree() {
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return size(root);
    }

    protected int size(Node<K, V> x) {
        return (x == null) ? 0 : x.getSize();
    }

    /**
     * This symbol table is empty?
     *
     * @return {@code true} if this symbol table table is empty and {@code false} otherwise
     */
    public boolean isEmpty() {
        return root == null;
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
        if (key == null)
            throw new IllegalArgumentException();
        return get(root, key);
    }

    protected V get(Node<K, V> x, K key) {
        while (x != null) {
            int cmp = key.compareTo(x.getKey());
            if (cmp < 0)
                x = x.getLeft();
            else if (cmp > 0)
                x = x.getRight();
            else
                return x.getValue();
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
        return get(key) != null;
    }

    protected void removeSingleNode(Node<K, V> x, Node<K, V> successor) {
        if (successor != null) {
            Node<K, V> xParent = x.getParent();
            successor.setParent(xParent);
            if (xParent == null)
                root = successor;
            else {
                if (x == xParent.getLeft())
                    xParent.setLeft(successor);
                else
                    xParent.setRight(successor);
                xParent.setSize(xParent.getLeft().getSize() + xParent.getRight().getSize() + 1);
            }
            x.setLeft(null);
            x.setRight(null);
            x.setParent(null);
        } else if (x.getParent() == null) {
            root = null;
        } else {
            Node<K, V> parent = x.getParent();
            if (x == parent.getLeft())
                parent.setLeft(null);
            else
                parent.setRight(null);
            parent.setSize(parent.getLeft().getSize() + parent.getRight().getSize() + 1);
            x.setParent(null);
        }
    }

    /**
     * Returns the successor of the node x or null if no such.
     * successor is the right subtree the leftmost node.
     */
    protected Node<K, V> successor(Node<K, V> x) {
        if (x == null)
            return null;
        Node<K, V> right = x.getRight();
        if (right != null) {
            while (right.getLeft() != null)
                right = right.getLeft();
            return right;
        } else {
            Node<K, V> t = x;
            Node<K, V> p = x.getParent();
            while (p != null && t == p.getRight()) {
                t = p;
                p = p.getParent();
            }
            return p;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }

}
