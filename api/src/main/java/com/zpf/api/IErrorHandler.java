package com.zpf.api;

public interface IErrorHandler {
    boolean onException(Throwable throwable);

    boolean onErrorCode(int code);
}