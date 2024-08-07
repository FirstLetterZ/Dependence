package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IQuerier<T> {
    @Nullable
    T query(@NonNull String condition);
}