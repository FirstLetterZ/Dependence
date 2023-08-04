package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface ItemListAdapter<T> {
    int getSize();

    @NonNull
    List<T> getDataList();

    @Nullable
    T getDataAt(int position);

    ItemListAdapter<T> setItemClickListener(@Nullable OnItemClickListener<T> itemClickListener);

    ItemListAdapter<T> addDataList(@Nullable List<T> list);

    ItemListAdapter<T> setDataList(@Nullable List<T> list);
}
