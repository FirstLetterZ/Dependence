package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface ItemListAdapter<T> {
    int getSize();

    ItemListAdapter<T> setItemClickListener(@Nullable OnItemClickListener itemClickListener);

    ItemListAdapter<T> setItemViewClickListener(@Nullable OnItemViewClickListener itemViewClickListener);

    ItemListAdapter<T> addData(@Nullable T data);

    @Nullable
    T getDataAt(int position);

    ItemListAdapter<T> addDataList(@Nullable List<T> list);

    ItemListAdapter<T> setDataList(@Nullable List<T> list);

    @NonNull
    List<T> getDataList();

    ItemListAdapter<T> setItemTypeManager(ItemTypeManager manager);

    ItemListAdapter<T> setItemViewCreator(@Nullable ItemViewCreator creator);
}
