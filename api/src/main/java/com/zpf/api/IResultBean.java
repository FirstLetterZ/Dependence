package com.zpf.api;

public interface IResultBean<T> {
    boolean isSuccess();

    String getMessage();

    int getCode();

    T getData();
}
