package com.zpf.tool.fragment;

import androidx.annotation.NonNull;

public interface IFragmentFactory<T> {
    T create(@NonNull String tag);

    int getParentId(@NonNull String tag);
}