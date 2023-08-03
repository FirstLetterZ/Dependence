package com.zpf.api;

import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/2/25.
 */
public interface IViewHolder {

    void onBindData(@Nullable Object data, int position);

    View getView();
}
