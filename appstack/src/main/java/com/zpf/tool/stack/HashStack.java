package com.zpf.tool.stack;

import java.util.HashMap;

public class HashStack<A, B> {

    private final HashMap<A, StackNode<A, B>> stackMap = new HashMap<>();
    private StackNode<A, B> first;
    private StackNode<A, B> last;

    public void put(A key, B value) {
        if (key == null) {
            return;
        }
        StackNode<A, B> node;
        if (first == null) {
            node = new StackNode<>(key, value);
            node.elementState = StackElementState.STACK_TOP;
            first = node;
            last = node;
            stackMap.put(key, node);
        } else {
            node = stackMap.get(key);
            if (node == null) {
                node = new StackNode<>(key, value);
                node.elementState = StackElementState.STACK_TOP;
                if (last == first) {
                    last = node;
                    node.prev = first;
                    first.elementState = StackElementState.STACK_INSIDE;
                    first.next = node;
                } else {
                    last.next = node;
                    last.elementState = StackElementState.STACK_INSIDE;
                    node.prev = last;
                    last = node;
                }
                stackMap.put(key, node);
            } else {
                if (node.prev != null) {
                    node.prev.next = node.next;
                }
                if (node.next != null) {
                    node.next.prev = node.prev;
                }
                node.next = null;
                node.prev = last;
                last = node;
            }
        }
    }

    public B get(A key) {
        StackNode<A, B> resultNode = getNode(key);
        if (resultNode != null) {
            return resultNode.item;
        } else {
            return null;
        }
    }

    public StackNode<A, B> getNode(A key) {
        if (first == null || key == null) {
            return null;
        }
        return stackMap.get(key);
    }

    public B remove(A key) {
        if (first == null || key == null) {
            return null;
        }
        B result = null;
        if (first == last) {
            if (key.equals(first.key)) {
                first.elementState = StackElementState.STACK_OUTSIDE;
                result = first.item;
                first = null;
                last = null;
                stackMap.clear();
            }
        } else {
            StackNode<A, B> node = stackMap.remove(key);
            if (node != null) {
                node.elementState = StackElementState.STACK_OUTSIDE;
                result = node.item;
                node.removeSelf();
            }
        }
        return result;
    }

    public boolean moveAllNext(A key) {
        StackNode<A, B> node = stackMap.get(key);
        if (node != null) {
            last = node;
            StackNode<A, B> next = node.next;
            node.next = null;
            node.elementState = StackElementState.STACK_TOP;
            while (next != null) {
                next.elementState = StackElementState.STACK_OUTSIDE;
                stackMap.remove(next.key);
                next = next.next;
            }
            return true;
        }
        return false;
    }

    public void moveToLast(A key) {
        StackNode<A, B> node = stackMap.get(key);
        if (node == null || node == last) {
            return;
        }
        if (node.next == null) {
            node.elementState = StackElementState.STACK_TOP;
            last = node;
        } else {
            StackNode<A, B> nextNode = node.next;
            nextNode.prev = node.prev;
            if (node.prev != null) {
                node.prev.next = nextNode;
            }
            if (last == null) {//理论上不存在断链情况
                while (nextNode != null) {
                    last = nextNode;
                    nextNode = nextNode.next;
                }
            }
            last.next = node;
            last.elementState = StackElementState.STACK_INSIDE;
            node.next = null;
            node.prev = last;
            node.elementState = StackElementState.STACK_TOP;
            last = node;
        }
    }

    public boolean moveAllPrev(A key) {
        StackNode<A, B> node = stackMap.get(key);
        if (node != null) {
            first = node;
            StackNode<A, B> prev = node.prev;
            node.prev = null;
            if (node != last) {
                node.elementState = StackElementState.STACK_INSIDE;
            }
            while (prev != null) {
                prev.elementState = StackElementState.STACK_OUTSIDE;
                stackMap.remove(prev.key);
                prev = prev.prev;
            }
            return true;
        }
        return false;
    }

    public void moveToFirst(A key) {
        StackNode<A, B> node = stackMap.get(key);
        if (node == null || node == first) {
            return;
        }
        if (node.prev == null) {
            first = node;
            if (node == last) {
                node.elementState = StackElementState.STACK_TOP;
            } else {
                node.elementState = StackElementState.STACK_INSIDE;
            }
        } else {
            StackNode<A, B> prevNode = node.prev;
            prevNode.elementState = StackElementState.STACK_INSIDE;
            prevNode.next = node.next;
            if (node.next != null) {
                node.next.prev = prevNode;
            }
            if (first == null) {//理论上不存在断链情况
                while (prevNode != null) {
                    first = prevNode;
                    prevNode = prevNode.prev;
                }
            }
            first.prev = node;
            node.next = first;
            node.prev = null;
            node.elementState = StackElementState.STACK_INSIDE;
            first = node;
        }
    }

    public B getFirst() {
        if (first != null) {
            return first.item;
        }
        return null;
    }

    public StackNode<A, B> getFirstNode() {
        return first;
    }

    public B pollFirst() {
        B item = null;
        if (first != null) {
            first.elementState = StackElementState.STACK_OUTSIDE;
            item = first.item;
            stackMap.remove(first.key);
            if (first.next != null) {
                first.next.prev = null;
                first = first.next;
            } else {
                first = null;
                last = null;
            }
        }
        return item;
    }

    public StackNode<A, B> pollFirstNode() {
        StackNode<A, B> firstNode = first;
        if (firstNode != null) {
            firstNode.elementState = StackElementState.STACK_OUTSIDE;
            stackMap.remove(firstNode.key);
            if (firstNode.next != null) {
                firstNode.next.prev = null;
                first = firstNode.next;
            } else {
                first = null;
                last = null;
            }
        }
        return firstNode;
    }

    public B getLast() {
        if (last != null) {
            return last.item;
        }
        return null;
    }

    public StackNode<A, B> getLastNode() {
        return last;
    }

    public B pollLast() {
        B item = null;
        if (last != null) {
            last.elementState = StackElementState.STACK_OUTSIDE;
            item = last.item;
            stackMap.remove(last.key);
            if (last.prev != null) {
                last.prev.next = null;
                last = last.prev;
                last.elementState = StackElementState.STACK_TOP;
            } else {
                first = null;
                last = null;
            }
        }
        return item;
    }


    public StackNode<A, B> pollLastNode() {
        StackNode<A, B> lastNode = last;
        if (lastNode != null) {
            lastNode.elementState = StackElementState.STACK_OUTSIDE;
            stackMap.remove(lastNode.key);
            if (lastNode.prev != null) {
                lastNode.prev.next = null;
                last = lastNode.prev;
                lastNode.elementState = StackElementState.STACK_TOP;
            } else {
                first = null;
                last = null;
            }
        }
        return lastNode;
    }

    public void clear() {
        stackMap.clear();
        first = null;
        last = null;
    }

    public int getSize() {
        return stackMap.size();
    }
}
