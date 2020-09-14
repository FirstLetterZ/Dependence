package com.zpf.rvexpand;

import android.view.View;

public interface LoadMoreViewHolder {
    void onLoading(boolean loading);

    View getItemView();
}
