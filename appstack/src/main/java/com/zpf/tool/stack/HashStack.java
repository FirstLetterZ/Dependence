package com.zpf.tool.stack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Created by ZPF on 2021/10/13.
 */
public class HashStack<A, B> {
    private final HashMap<A, Node<A, B>> stackMap = new HashMap<>();
    private Node<A, B> first;
    private Node<A, B> last;

    /**
     * 将数据放到队列末尾
     */
    public void add(A key, B value) {
        if (key == null) {
            return;
        }
        Node<A, B> node = stackMap.get(key);
        if (last != null && node == last) {
            node.value = value;
            return;
        }
        if (node == null) {
            node = new Node<>(key, value);
            stackMap.put(key, node);
        } else {
            node.value = value;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (first == node) {
            first = node.next;
        }
        if (last == node) {
            last = node.prev;
        }
        if (first == null || last == null) {
            first = node;
            last = node;
            stackMap.clear();
            stackMap.put(key, node);
        } else {
            node.next = null;
            last.next = node;
            node.prev = last;
            last = node;
        }
        node.changeState(true);
    }

    public boolean update(A key, B value) {
        if (key == null) {
            return false;
        }
        Node<A, B> node = stackMap.get(key);
        if (node == null) {
            return false;
        } else {
            node.value = value;
            return true;
        }
    }

    public boolean has(A key) {
        if (key == null) {
            return false;
        }
        return stackMap.containsKey(key);
    }

    public B get(A key) {
        Node<A, B> resultNode = getNode(key);
        if (resultNode != null) {
            return resultNode.value;
        } else {
            return null;
        }
    }

    public B remove(A key) {
        if (first == null || key == null) {
            return null;
        }
        Node<A, B> node = stackMap.remove(key);
        if (node == null) {
            return null;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (first == node) {
            first = node.next;
        }
        if (last == node) {
            last = node.prev;
        }
        B result = node.value;
        if (first == null || last == null) {
            first = null;
            last = null;
            stackMap.clear();
        }
        node.changeState(false);

        return result;
    }

    public B getFirst() {
        if (first == null) {
            return null;
        }
        return first.value;
    }

    public B pollFirst() {
        if (first == null) {
            return null;
        }
        B value = first.value;
        stackMap.remove(first.key);
        if (first.next != null) {
            first.next.prev = null;
        }
        first.changeState(false);
        first = first.next;
        if (first == null) {
            last = null;
            stackMap.clear();
        }
        return value;
    }

    public B getLast() {
        if (last == null) {
            return null;
        }
        return last.value;
    }

    public B pollLast() {
        if (last == null) {
            return null;
        }
        B value = last.value;
        stackMap.remove(last.key);
        if (last.prev != null) {
            last.prev.next = null;
        }
        last.changeState(false);
        last = last.prev;
        if (last == null) {
            first = null;
            stackMap.clear();
        }
        return value;
    }

    public boolean removeAfter(A key) {
        if (key == null) {
            return false;
        }
        Node<A, B> node = stackMap.get(key);
        if (node == null) {
            return false;
        }
        Node<A, B> next = node.next;
        while (next != null) {
            stackMap.remove(next.key);
            next.changeState(false);
            next = next.next;
        }
        node.next = null;
        last = node;
        return true;
    }

    public boolean removeBefore(A key) {
        if (key == null) {
            return false;
        }
        Node<A, B> node = stackMap.get(key);
        if (node == null) {
            return false;
        }
        Node<A, B> prev = node.prev;
        while (prev != null) {
            stackMap.remove(prev.key);
            prev.changeState(false);
            prev = prev.prev;
        }
        node.prev = null;
        first = node;
        return true;
    }

    public boolean moveToFirst(A key) {
        if (key == null || first == null) {
            return false;
        }
        Node<A, B> node = stackMap.get(key);
        if (node == null) {
            return false;
        }
        if (node == first) {
            return true;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        first.prev = node;
        node.next = first;
        node.prev = null;
        first = node;
        return true;
    }

    public boolean moveToLast(A key) {
        if (key == null || last == null) {
            return false;
        }
        Node<A, B> node = stackMap.get(key);
        if (node == null) {
            return false;
        }
        if (node == last) {
            return true;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        last.next = node;
        node.prev = last;
        node.next = null;
        last = node;
        return true;
    }

    public Node<A, B> getNode(A key) {
        if (first == null || key == null) {
            return null;
        }
        return stackMap.get(key);
    }

    public void clear() {
        Node<A, B> node = last;
        while (node != null) {
            stackMap.remove(node.key);
            node.changeState(false);
            node = node.prev;
        }
        stackMap.clear();
        first = null;
        last = null;
    }

    public int size() {
        return stackMap.size();
    }

    public Iterator<Node<A, B>> iterator() {
        return new StackIterator(first);
    }

    public static class Node<K, S> {
        private Node<K, S> prev;
        private Node<K, S> next;
        private S value;
        private final K key;
        private boolean inStack = false;

        private void changeState(boolean isInStackNow) {
            if (isInStackNow != inStack) {
                inStack = isInStackNow;
                if (value instanceof NodeStateListener) {
                    ((NodeStateListener) value).onStateChanged(isInStackNow);
                }
            }
        }

        private Node(K key, S value) {
            this.value = value;
            this.key = key;
        }

        public boolean isInStack() {
            return inStack;
        }

        public S getValue() {
            return value;
        }

        public K getKey() {
            return key;
        }
    }

    public class StackIterator implements Iterator<Node<A, B>> {
        private Node<A, B> nextNode;
        private Node<A, B> currentNode;

        private StackIterator(Node<A, B> first) {
            nextNode = first;
            currentNode = null;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public Node<A, B> next() {
            Node<A, B> node = nextNode;
            if (node == null) {
                throw new NoSuchElementException();
            }
            currentNode = node;
            nextNode = node.next;
            return node;
        }

        @Override
        public void remove() {
            Node<A, B> node = currentNode;
            if (node == null) {
                return;
            }
            currentNode = node.prev;
            HashStack.this.remove(node.key);
        }
    }

}