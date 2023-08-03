package com.zpf.api;

import androidx.annotation.Nullable;

public interface ITypeListAdapter<T> extends ItemListAdapter<T> {
    ITypeListAdapter<T> setItemTypeManager(ItemTypeManager manager);

    ITypeListAdapter<T> setItemViewCreator(@Nullable ItemViewCreator creator);
}
