package com.zpf.tool.stack;

public class StackNode<K, S> {
    StackNode(K key, S item) {
        this.item = item;
        this.key = key;
    }

    @StackElementState
    int elementState = StackElementState.STACK_OUTSIDE;
    StackNode<K, S> prev;
    StackNode<K, S> next;
    S item;
    K key;

    @StackElementState
    public int getElementState() {
        return elementState;
    }

    public StackNode<K, S> getPrev() {
        return prev;
    }

    public StackNode<K, S> getNext() {
        return next;
    }

    public S getItem() {
        return item;
    }

    public K getKey() {
        return key;
    }

    public void removeSelf() {
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
    }
}
