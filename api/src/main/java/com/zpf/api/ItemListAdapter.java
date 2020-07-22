package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface ItemListAdapter<T> {
    int getSize();

    int getDataIndex(int position);

    ItemListAdapter<T> setItemClickListener(@Nullable OnItemClickListener itemClickListener);

    ItemListAdapter<T> setItemViewClickListener(@Nullable OnItemViewClickListener itemViewClickListener);

    ItemListAdapter<T> addData(@Nullable T data);

    ItemListAdapter<T> addDataList(@Nullable List<T> list);

    ItemListAdapter<T> setDataList(@Nullable List<T> list);

    @Nullable
    T getPositionData(int position);

    @NonNull
    List<T> getDataList();

    ItemListAdapter<T> setItemTypeManager(ItemTypeManager manager);

    ItemListAdapter<T> setItemViewCreator(@Nullable ItemViewCreator creator);
}
