package com.zpf.rvexpand;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface BottomHolderListener {
    boolean shouldShowHolder(@NonNull RecyclerView.ViewHolder holder);
}
