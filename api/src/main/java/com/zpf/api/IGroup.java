package com.zpf.api;

import android.support.annotation.NonNull;

public interface IGroup<T> {

    void remove(@NonNull T t);

    void add(@NonNull T t);

    int size();

}
