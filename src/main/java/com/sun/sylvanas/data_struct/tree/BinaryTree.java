package com.sun.sylvanas.data_struct.tree;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

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

    /**
     * Returns the smallest key in the symbol table.
     *
     * @return the smallest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public K min() {
        if (isEmpty())
            throw new NoSuchElementException();

        Node<K, V> x = root;
        while (x.getLeft() != null)
            x = x.getLeft();
        return x.getKey();
    }

    /**
     * Returns the largest key in the symbol table.
     *
     * @return the largest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public K max() {
        if (isEmpty())
            throw new NoSuchElementException();

        Node<K, V> x = root;
        while (x.getRight() != null)
            x = x.getRight();
        return x.getKey();
    }

    /**
     * Removes the smallest key and associated value from the symbol table.
     * and return old value.
     *
     * @return @return the old value (if return {@code null} symbol table no contain the key)
     * @throws NoSuchElementException if the symbol table is empty
     */
    public V removeMin() {
        if (isEmpty())
            throw new NoSuchElementException();

        V oldValue = get(min());
        if (oldValue == null)
            return null;
        removeMin(root);
        return oldValue;
    }

    protected void removeMin(Node<K, V> x) {
        while (x != null) {
            if (x.getLeft() != null)
                x = x.getLeft();
            else {
                Node<K, V> successor = x.getRight();
                removeSingleNode(x, successor);
                x = null;
            }
        }
    }

    /**
     * Removes the largest key and associated value from the symbol table.
     * and return old value.
     *
     * @return @return the old value (if return {@code null} symbol table no contain the key)
     * @throws NoSuchElementException if the symbol table is empty
     */
    public V removeMax() {
        if (isEmpty())
            throw new NoSuchElementException();

        V oldValue = get(max());
        if (oldValue == null)
            return null;
        removeMax(root);
        return oldValue;
    }

    protected void removeMax(Node<K, V> x) {
        while (x != null) {
            if (x.getRight() != null)
                x = x.getRight();
            else {
                Node<K, V> successor = x.getLeft();
                removeSingleNode(x, successor);
                x = null;
            }
        }
    }

    /**
     * Returns the largest key in the symbol table less than or equals to {@code key}.
     *
     * @param key the key
     * @return the largest key in the symbol table less than or equals to {@code key}
     * @throws IllegalArgumentException if {@code key} is {@code null}
     * @throws NoSuchElementException   if there is no such key
     */
    public K floor(K key) {
        if (key == null)
            throw new IllegalArgumentException();
        if (isEmpty())
            throw new NoSuchElementException();

        Node<K, V> x = floor(root, key);
        if (x == null)
            return null;
        else
            return x.getKey();
    }

    protected Node<K, V> floor(Node<K, V> x, K key) {
        while (x != null) {
            int cmp = key.compareTo(x.getKey());
            if (cmp > 0) {
                if (x.getRight() != null)
                    x = x.getRight();
                else
                    return x;
            } else if (cmp < 0) {
                if (x.getLeft() != null)
                    x = x.getLeft();
                else {
                    Node<K, V> p = x.getParent();
                    Node<K, V> t = x;
                    while (p != null && t == p.getLeft()) {
                        t = p;
                        p = p.getParent();
                    }
                    return p;
                }
            } else {
                return x;
            }
        }
        return null;
    }

    /**
     * Returns the smallest key in the symbol table greater than or equals to {@code key}.
     *
     * @param key the key
     * @return the smallest key in the symbol table greater than or equals to {@code key}
     * @throws IllegalArgumentException if {@code key} is {@code null}
     * @throws NoSuchElementException   if there is no such key
     */
    public K ceiling(K key) {
        if (key == null)
            throw new IllegalArgumentException();
        if (isEmpty())
            throw new NoSuchElementException();

        Node<K, V> x = ceiling(root, key);
        if (x == null)
            return null;
        else
            return x.getKey();
    }

    protected Node<K, V> ceiling(Node<K, V> x, K key) {
        while (x != null) {
            int cmp = key.compareTo(x.getKey());
            if (cmp < 0) {
                if (x.getLeft() != null)
                    x = x.getLeft();
                else
                    return x;
            } else if (cmp > 0) {
                if (x.getRight() != null)
                    x = x.getRight();
                else {
                    Node<K, V> t = x;
                    Node<K, V> p = x.getParent();
                    while (p != null && t == x.getRight()) {
                        t = p;
                        p = p.getParent();
                    }
                    return p;
                }
            } else {
                return x;
            }
        }
        return null;
    }

    /**
     * Return the kth smallest key in the symbol table.
     *
     * @param k the order statistic
     * @return the {@code k}th smallest key in the symbol table
     * @throws IllegalArgumentException unless {@code k} is between 0 and <em>n</em> - 1
     */
    public K select(int k) {
        if (k < 0 || k >= size())
            throw new IllegalArgumentException();

        Node<K, V> x = select(root, k);
        if (x == null)
            return null;
        else
            return x.getKey();
    }

    protected Node<K, V> select(Node<K, V> x, int k) {
        while (x != null) {
            int t = size(x.getLeft());
            if (t > k)
                x = x.getLeft();
            else if (t < k) {
                x = x.getRight();
                k = k - t - 1;
            } else {
                return x;
            }
        }
        return null;
    }

    /**
     * Return the number of keys in the symbol table strictly less than {@code key}.
     *
     * @param key the key
     * @return the number of keys in the symbol table strictly less than {@code key}
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public int rank(K key) {
        if (key == null)
            throw new IllegalArgumentException();

        return rank(root, key);
    }

    protected int rank(Node<K, V> x, K key) {
        if (x == null)
            return 0;
        int cmp = key.compareTo(x.getKey());
        if (cmp < 0)
            return rank(x.getLeft(), key);
        else if (cmp > 0)
            return 1 + size(x.getLeft()) + rank(x.getRight(), key);
        else
            return size(x.getLeft());
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

    protected class TreeIterator implements Iterator<K> {

        private Queue<K> queue;
        private Node<K, V> root;

        public TreeIterator(Node<K, V> root) {
            this.root = root;
            this.queue = new ArrayDeque<K>();
            inorder(root);
        }

        protected void inorder(Node<K, V> x) {
            if (x.getLeft() != null)
                inorder(x.getLeft());
            queue.add(x.getKey());
            if (x.getRight() != null)
                inorder(x.getRight());
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public K next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return queue.remove();
        }

    }

    @Override
    public Iterator<K> iterator() {
        return new TreeIterator(root);
    }

}
