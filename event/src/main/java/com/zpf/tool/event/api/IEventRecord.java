package com.zpf.tool.event.api;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public interface IEventRecord {
    int readTime();//收到事件前，已被接收多少次

    void interrupt();//事件停止传递

    boolean checkReceived(String receiver);//检查指定接收人是否已接收
}
