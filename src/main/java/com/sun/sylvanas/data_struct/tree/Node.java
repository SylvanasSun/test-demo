package com.sun.sylvanas.data_struct.tree;

/**
 * Created by SylvanasSun on 2017/5/23.
 */
public class Node<K, V> {

    protected K key;
    protected V value;
    protected int size = 0;
    protected Node<K, V> parent, left, right;

    public Node(K key, V value, int size, Node<K, V> parent, Node<K, V> left, Node<K, V> right) {
        this.key = key;
        this.value = value;
        this.size = size;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Node<K, V> getParent() {
        return parent;
    }

    public void setParent(Node<K, V> parent) {
        this.parent = parent;
    }

    public Node<K, V> getLeft() {
        return left;
    }

    public void setLeft(Node<K, V> left) {
        this.left = left;
    }

    public Node<K, V> getRight() {
        return right;
    }

    public void setRight(Node<K, V> right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                ", value=" + value +
                ", size=" + size +
                ", parent=" + parent +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node<?, ?> node = (Node<?, ?>) o;

        if (size != node.size) return false;
        if (key != null ? !key.equals(node.key) : node.key != null) return false;
        if (value != null ? !value.equals(node.value) : node.value != null) return false;
        if (parent != null ? !parent.equals(node.parent) : node.parent != null) return false;
        if (left != null ? !left.equals(node.left) : node.left != null) return false;
        return right != null ? right.equals(node.right) : node.right == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + size;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (left != null ? left.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

}
