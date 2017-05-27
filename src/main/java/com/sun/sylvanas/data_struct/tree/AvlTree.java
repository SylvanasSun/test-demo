package com.sun.sylvanas.data_struct.tree;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

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

    public int height() {
        return height(root);
    }

    private int height(AvlNode<K, V> x) {
        return (x == null) ? 0 : x.depth;
    }

    @Override
    public V get(K key) {
        if (key == null)
            throw new IllegalArgumentException();
        return get(root, key);
    }

    @Override
    public boolean contains(K key) {
        return get(key) != null;
    }

    @Override
    public void put(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException();
        if (value == null) {
            remove(key);
            return;
        }
        put(root, key, value);
    }

    @Override
    protected void put(Node<K, V> x, K key, V value) {
        int cmp = 0;
        AvlNode<K, V> parent = null;
        AvlNode<K, V> n = (AvlNode<K, V>) x;
        while (n != null) {
            parent = n;
            cmp = key.compareTo(n.getKey());
            if (cmp < 0)
                n = (AvlNode<K, V>) n.getLeft();
            else if (cmp > 0)
                n = (AvlNode<K, V>) n.getRight();
            else {
                n.setValue(value);
                return;
            }
        }
        AvlNode<K, V> newNode = new AvlNode<>(key, value, 1, parent, null, null);
        if (parent == null)
            root = newNode;
        else {
            if (cmp < 0)
                parent.setLeft(newNode);
            else
                parent.setRight(newNode);
            parent.size = 1 + size(parent.getLeft()) + size(parent.getRight());
            balance(parent);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
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

    @Override
    public K select(int k) {
        if (k < 0 && k >= size())
            throw new IllegalArgumentException();
        Node<K, V> x = select(root, k);
        if (x == null)
            return null;
        else
            return x.getKey();
    }

    @Override
    public int rank(K key) {
        if (key == null)
            throw new IllegalArgumentException();

        return rank(root, key);
    }

    @Override
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

    @Override
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

    @SuppressWarnings("Duplicates")
    @Override
    public V removeMax() {
        if (isEmpty())
            throw new NoSuchElementException();

        V oldValue = get(max());
        if (oldValue == null)
            return null;
        removeMax(root);
        return oldValue;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public V removeMin() {
        if (isEmpty())
            throw new NoSuchElementException();

        V oldValue = get(min());
        if (oldValue == null)
            return null;
        removeMin(root);
        return oldValue;
    }

    @Override
    public K min() {
        if (isEmpty())
            throw new NoSuchElementException();

        AvlNode<K, V> x = root;
        while (x.getLeft() != null)
            x = (AvlNode<K, V>) x.getLeft();
        return x.getKey();
    }

    @Override
    public K max() {
        if (isEmpty())
            throw new NoSuchElementException();

        AvlNode<K, V> x = root;
        while (x.getRight() != null)
            x = (AvlNode<K, V>) x.getRight();
        return x.getKey();
    }

    @Override
    protected void removeSingleNode(Node<K, V> x, Node<K, V> successor) {
        if (successor != null) {
            Node<K, V> xParent = x.getParent();
            successor.setParent(xParent);
            if (xParent == null)
                root = (AvlNode<K, V>) successor;
            else {
                if (x == xParent.getLeft())
                    xParent.setLeft(successor);
                else
                    xParent.setRight(successor);
                xParent.size = 1 + size(xParent.getLeft()) + size(xParent.getRight());
            }
            x.setParent(null);
            x.setLeft(null);
            x.setRight(null);
            x = null;
            balance((AvlNode<K, V>) successor);
        } else if (x.getParent() == null) {
            root = null;
        } else {
            Node<K, V> xParent = x.getParent();
            if (x == xParent.getLeft())
                xParent.setLeft(null);
            else
                xParent.setRight(null);
            xParent.size = 1 + size(xParent.getLeft()) + size(xParent.getRight());
            x.setParent(null);
            x = null;
            balance((AvlNode<K, V>) xParent);
        }
    }

    private int calculateDepth(AvlNode<K, V> x) {
        int depth = 0;
        if (x == null)
            return depth;
        AvlNode<K, V> subLeft = (AvlNode<K, V>) x.getLeft();
        AvlNode<K, V> subRight = (AvlNode<K, V>) x.getRight();
        if (subLeft != null)
            depth = subLeft.depth;
        if (subRight != null && subRight.depth > depth)
            depth = subRight.depth;
        depth++;
        return depth;
    }

    private int calculateBalance(AvlNode<K, V> x) {
        int leftDepth = 0;
        int rightDepth = 0;
        AvlNode<K, V> subLeft = (AvlNode<K, V>) x.getLeft();
        AvlNode<K, V> subRight = (AvlNode<K, V>) x.getRight();
        if (subLeft != null)
            leftDepth = subLeft.depth;
        if (subRight != null)
            rightDepth = subRight.depth;
        return leftDepth - rightDepth;
    }

    private AvlNode<K, V> rotateLeft(AvlNode<K, V> x) {
        AvlNode<K, V> t = (AvlNode<K, V>) x.getRight();
        x.setRight(t.getLeft());
        t.setLeft(x);
        AvlNode<K, V> xParent = (AvlNode<K, V>) x.getParent();
        t.setParent(xParent);
        if (xParent == null)
            root = t;
        else {
            if (x == xParent.getLeft())
                xParent.setLeft(t);
            else
                xParent.setRight(t);
        }
        x.setParent(t);
        xParent = null;
        x.depth = calculateDepth(x);
        x.balance = calculateBalance(x);
        x.size = 1 + size(x.getLeft()) + size(x.getRight());
        t.depth = calculateDepth(t);
        t.balance = calculateBalance(t);
        t.size = 1 + size(t.getLeft()) + size(t.getRight());
        return t;
    }

    private AvlNode<K, V> rotateRight(AvlNode<K, V> x) {
        AvlNode<K, V> t = (AvlNode<K, V>) x.getLeft();
        x.setLeft(t.getRight());
        t.setRight(x);
        AvlNode<K, V> xParent = (AvlNode<K, V>) x.getParent();
        t.setParent(xParent);
        if (xParent == null)
            root = t;
        else {
            if (x == xParent.getLeft())
                xParent.setLeft(t);
            else
                xParent.setRight(t);
        }
        x.setParent(t);
        xParent = null;
        x.depth = calculateDepth(x);
        x.balance = calculateBalance(x);
        x.size = 1 + size(x.getLeft()) + size(x.getRight());
        t.depth = calculateDepth(t);
        t.balance = calculateBalance(t);
        t.size = 1 + size(t.getLeft()) + size(t.getRight());
        return t;
    }

    private void balance(AvlNode<K, V> x) {
        while (x != null) {
            x.depth = calculateDepth(x);
            x.balance = calculateBalance(x);
            // left subtree high
            if (x.balance >= 2) {
                AvlNode<K, V> subLeft = (AvlNode<K, V>) x.getLeft();
                if (subLeft != null && subLeft.balance <= -1)
                    subLeft = rotateLeft(subLeft);
                x = rotateRight(x);
            }
            // right subtree high
            if (x.balance <= -2) {
                AvlNode<K, V> subRight = (AvlNode<K, V>) x.getRight();
                if (subRight != null && subRight.balance >= 1)
                    subRight = rotateRight(subRight);
                x = rotateLeft(x);
            }
            x.size = 1 + size(x.getLeft()) + size(x.getRight());
            x = (AvlNode<K, V>) x.getParent();
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new TreeIterator(root);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AvlTree<String, Integer> tree = new AvlTree<>();
        int count = 0;
        System.out.println("Please input specified command.");
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if ("exit".equalsIgnoreCase(command))
                break;
            else if ("forEach".equalsIgnoreCase(command)) {
                Iterator<String> iterator = tree.iterator();
                System.out.println("avl tree height: " + tree.height());
                System.out.println("avl tree size: " + tree.size());
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Integer value = tree.get(key);
                    System.out.println(key + "-" + value);
                }
            } else if ("min".equalsIgnoreCase(command)) {
                String min = tree.min();
                System.out.printf("execute min result %s-%d\n", min, tree.get(min));
            } else if ("max".equalsIgnoreCase(command)) {
                String max = tree.max();
                System.out.printf("execute max result %s-%d\n", max, tree.get(max));
            } else if ("put".equalsIgnoreCase(command.substring(0, 3))) {
                String key = command.substring(4);
                System.out.printf("execute put %s-%d\n", key, count++);
                tree.put(key, count);
            } else if ("get".equalsIgnoreCase(command.substring(0, 3))) {
                String key = command.substring(4);
                System.out.printf("execute get %s result %d\n", key, tree.get(key));
            } else if ("remove".equalsIgnoreCase(command.substring(0, 6))) {
                String key = command.substring(7);
                System.out.printf("execute remove %s-%d\n", key, tree.remove(key));
            } else if ("select".equalsIgnoreCase(command.substring(0, 6))) {
                String s = command.substring(7);
                Integer index = Integer.valueOf(s);
                System.out.printf("execute select index: %d,key: %s\n", index, tree.select(index));
            } else if ("rank".equalsIgnoreCase(command.substring(0, 4))) {
                String key = command.substring(5);
                System.out.printf("execute rank index: %d,key: %s\n", tree.rank(key), key);
            } else if ("floor".equalsIgnoreCase(command.substring(0, 5))) {
                String key = command.substring(6);
                System.out.printf("execute floor key:%s,result:%s\n", key, tree.floor(key));
            } else if ("ceiling".equalsIgnoreCase(command.substring(0, 7))) {
                String key = command.substring(8);
                System.out.printf("execute ceiling key:%s,result:%s\n", key, tree.ceiling(key));
            } else {
                System.out.println("Illegal command,please enter again!");
            }
        }
    }

}
