package com.zpf.rvexpand;

import androidx.annotation.NonNull;

public interface BottomHolderListener {
    boolean shouldShowHolder(@NonNull LoadMoreViewHolder holder);
}
