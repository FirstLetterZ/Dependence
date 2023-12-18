package com.zpf.apptest.task;

public interface IRequestInterceptor {
    boolean shouldIntercept(IRequest request);
}
