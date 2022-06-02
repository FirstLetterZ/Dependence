package com.zpf.api;

/**
 * 日志打印
 * Created by ZPF on 2019/2/28.
 */
public interface ILogger {
    void log(int priority, String tag, String content);
}