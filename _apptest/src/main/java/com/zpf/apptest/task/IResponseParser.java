package com.zpf.apptest.task;

public interface IResponseParser<T> {
    T parse(Object data, Class<T> type);
}
