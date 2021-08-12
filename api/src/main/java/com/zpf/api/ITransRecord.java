package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/8/11.
 */
public interface ITransRecord {
    boolean addReader(@NonNull String name,@Nullable String token);

    int readTime();//收到事件前，已被查看了多少次

    boolean isTargetReceived(@NonNull String name);//检查指定接收人是否已接收

    void interrupt(@Nullable String operator);//事件停止传递

    boolean isInterrupted();

    @Nullable
    String whoInterrupt();

}