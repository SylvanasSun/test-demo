package com.sun.sylvanas.data_struct.tree;

import sun.plugin2.message.PrintAppletReplyMessage;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
            this.size = 1;
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
        checkKeyIsNull(key, "called get(K key) function use the key is null.");
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
        checkKeyIsNull(key, "called contains(K key) function use the key is null.");
        return getValueAssociatedWithKey(key) != null;
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
        checkKeyIsNull(key, "called put(K key,V value) function use the key is null.");
        if (value == null) {
            remove(key);
            return;
        }

        putNewNodeOrUpdate(key, value);
    }

    private void putNewNodeOrUpdate(K key, V value) {
        Node x = root;
        int cmp = 0;
        Node parent = null;
        while (x != null) {
            parent = x;
            cmp = key.compareTo(x.key);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else {
                x.value = value;
                return;
            }
        }

        Node newNode = new Node(key, value);
        setColor(newNode, RED);
        newNode.parent = parent;
        if (parent != null) {
            if (cmp < 0)
                parent.left = newNode;
            else
                parent.right = newNode;
            parent.size = 1 + size(parent.left) + size(parent.right);
            fixAfterInsertion(newNode);
        } else {
            root = newNode;
            setColor(root, BLACK);
        }
    }

    /**
     * Returns the smallest key in the symbol table.
     *
     * @return the smallest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public K min() {
        checkEmpty("called min() function the this red black tree is empty.");

        Node smallestNode = getSmallestNode();
        if (smallestNode == null)
            return null;
        else
            return smallestNode.key;
    }

    private Node getSmallestNode() {
        Node x = root;
        while (x.left != null)
            x = x.left;
        return x;
    }

    /**
     * Returns the largest key in the symbol table.
     *
     * @return the largest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public K max() {
        checkEmpty("called max() function the this red black tree is empty.");

        Node largestNode = getLargestNode();
        if (largestNode == null)
            return null;
        else
            return largestNode.key;
    }

    private Node getLargestNode() {
        Node x = root;
        while (x.right != null)
            x = x.right;
        return x;
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
        checkKeyIsNull(key, "called remove(K key) function use the key is null.");
        checkEmpty("called remove(K key) function the this red black tree is empty.");

        V result = get(key);
        if (result == null)
            return null;
        removeNodeWithKey(root, key);
        return result;
    }

    private void removeNodeWithKey(Node x, K key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else {
                if (x.left != null && x.right != null) {
                    Node successor = successor(x);
                    x.key = successor.key;
                    x.value = successor.value;
                    x.size = x.size - 1;
                    x = successor;
                }
                Node replacement = x.left != null ? x.left : x.right;
                removeSingleNode(x, replacement);
                x = null;
            }
        }
    }

    private void removeSingleNode(Node x, Node replacement) {
        if (replacement != null) {
            replacementNotNull(x, replacement);
        } else if (x.parent == null) {
            root = null;
        } else {
            replacementIsNull(x);
        }
    }

    private void replacementNotNull(Node x, Node replacement) {
        Node xParent = x.parent;
        replacement.parent = xParent;
        if (xParent == null)
            root = replacement;
        else {
            if (x == xParent.left)
                xParent.left = replacement;
            else
                xParent.right = replacement;
            xParent.size = 1 + size(xParent.left) + size(xParent.right);
        }
        x.left = x.right = x.parent = null;
        if (x.color == BLACK)
            fixAfterDeletion(replacement);
    }

    private void replacementIsNull(Node x) {
        if (x.color == BLACK)
            fixAfterDeletion(x);

        Node xParent = x.parent;
        if (x == xParent.left)
            xParent.left = null;
        else
            xParent.right = null;
        xParent.size = 1 + size(xParent.left) + size(xParent.right);
    }

    private Node successor(Node x) {
        if (x == null) return null;
        Node xRight = x.right;
        if (xRight != null) {
            while (xRight.left != null)
                xRight = xRight.left;
            return xRight;
        } else {
            Node t = x;
            Node p = x.parent;
            while (p != null && t == p.right) {
                t = p;
                p = p.parent;
            }
            return p;
        }
    }

    private void checkKeyIsNull(K key, String message) {
        if (key == null)
            throw new IllegalArgumentException(message);
    }

    private void checkEmpty(String message) {
        if (isEmpty())
            throw new NoSuchElementException(message);
    }

    private void fixAfterInsertion(Node x) {
        while (x != null && x != root && parentOf(x).color == RED) {
            if (parentOf(x) == grandpaOf(x).left) {
                x = parentIsLeftNode(x);
            } else {
                x = parentIsRightNode(x);
            }
        }
        setColor(root, BLACK);
    }

    private Node parentIsLeftNode(Node x) {
        Node xUncle = grandpaOf(x).right;
        if (xUncle.color == RED) {
            x = uncleIsRed(x, xUncle);
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
            x = uncleIsRed(x, xUncle);
        } else {
            if (x == parentOf(x).left) {
                x = parentOf(x);
                rotateRight(x);
            }
            rotateLeft(grandpaOf(x));
        }
        return x;
    }

    private Node uncleIsRed(Node x, Node xUncle) {
        setColor(parentOf(x), BLACK);
        setColor(xUncle, BLACK);
        setColor(grandpaOf(x), RED);
        x = grandpaOf(x);
        return x;
    }

    private void fixAfterDeletion(Node x) {
        while (x != null && x != root && x.color == BLACK) {
            if (x == parentOf(x).left) {
                x = successorIsLeftNode(x);
            } else {
                x = successorIsRightNode(x);
            }
        }
        setColor(x, BLACK);
    }

    private Node successorIsLeftNode(Node x) {
        Node brother = parentOf(x).right;

        if (brother.color == RED) {
            rotateLeft(parentOf(x));
            brother = parentOf(x).right;
        }

        if (brother.left.color == BLACK && brother.right.color == BLACK) {
            x = brotherChildrenIsBlack(x, brother);
        } else {
            if (brother.right.color == BLACK) {
                rotateRight(brother);
                brother = parentOf(x).right;
            }
            setColor(brother.right, BLACK);
            rotateLeft(parentOf(x));
            x = root;
        }
        return x;
    }

    private Node successorIsRightNode(Node x) {
        Node brother = parentOf(x).left;

        if (brother.color == RED) {
            rotateRight(parentOf(x));
            brother = parentOf(x).left;
        }

        if (brother.left.color == BLACK && brother.right.color == BLACK) {
            x = brotherChildrenIsBlack(x, brother);
        } else {
            if (brother.left.color == BLACK) {
                rotateLeft(brother);
                brother = parentOf(x).left;
            }
            setColor(brother.left, BLACK);
            rotateRight(parentOf(x));
            x = root;
        }
        return x;
    }

    private Node brotherChildrenIsBlack(Node x, Node brother) {
        setColor(brother, RED);
        x = parentOf(x);
        return x;
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
        boolean temp = t.color;
        t.color = x.color;
        x.color = temp;
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
