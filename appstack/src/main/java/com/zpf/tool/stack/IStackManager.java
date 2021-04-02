package com.zpf.tool.stack;

/**
 * @author Created by ZPF on 2021/4/2.
 */
public interface IStackManager<T> {

    void push(T t);

    void pop();

    boolean popTo(String name);

    void popToRoot();

    T peek();

    boolean remove(String name);

    int size();

    T search(String name);

    void clear(boolean keepTop);
}
