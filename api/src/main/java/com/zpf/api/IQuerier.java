package com.zpf.api;

import androidx.annotation.Nullable;

public interface IQuerier<T> {
    @Nullable
    T query(String condition);
}