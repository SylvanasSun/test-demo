package com.sun.sylvanas.data_struct.tree;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
     * Inserts the specified key-value pair into the symbol table, overwriting the old
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is {@code null}.
     *
     * @param key   the key
     * @param value the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException();
        if (value == null) {
            remove(key);
            return;
        }

        put(root, key, value);
    }

    protected void put(Node<K, V> x, K key, V value) {
        Node<K, V> t = null;
        int cmp = 0;
        while (x != null) {
            t = x;
            cmp = key.compareTo(x.getKey());
            if (cmp < 0)
                x = x.getLeft();
            else if (cmp > 0)
                x = x.getRight();
            else {
                x.setValue(value);
                return;
            }
        }
        Node<K, V> n = new Node<>(key, value, 1, t, null, null);
        if (t != null) {
            if (cmp < 0)
                t.setLeft(n);
            else
                t.setRight(n);
            t.setSize(1 + t.getLeft().getSize() + t.getRight().getSize());
        } else {
            root = n;
        }
    }

    /**
     * Removes the specified key and its associated value from this symbol table
     * (if the key is is in this symbol table) and return old value.
     *
     * @param key the key
     * @return the old value (if return {@code null} symbol table no contain the key)
     * @throws IllegalArgumentException if {@code key} is {@code null}
     * @throws NoSuchElementException   if the symbol table is empty
     */
    public V remove(K key) {
        if (key == null)
            throw new IllegalArgumentException();
        if (isEmpty())
            throw new NoSuchElementException();

        V oldValue = get(key);
        if (oldValue == null)
            return null;
        remove(root, key);
        return oldValue;
    }

    protected void remove(Node<K, V> x, K key) {
        while (x != null) {
            int cmp = key.compareTo(x.getKey());
            if (cmp < 0)
                x = x.getLeft();
            else if (cmp > 0)
                x = x.getRight();
            else {
                if (x.getLeft() != null && x.getRight() != null) {
                    Node<K, V> successor = successor(x);
                    x.setKey(successor.getKey());
                    x.setValue(successor.getValue());
                    x = successor;
                }
                Node<K, V> replacement = (x.getLeft() != null) ? x.getLeft() : x.getRight();
                removeSingleNode(x, replacement);
                x = null;
            }
        }
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
