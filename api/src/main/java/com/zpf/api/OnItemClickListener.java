package com.zpf.api;

import android.view.View;

import androidx.annotation.Nullable;

public interface OnItemClickListener<T> {
    void onItemClick(@Nullable View view, @Nullable T data, int position);
}