package com.zpf.rvexpand;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EmptyHolder extends RecyclerView.ViewHolder {
    public EmptyHolder(@NonNull Context context) {
        super(new Space(context));
    }

    public EmptyHolder(@NonNull Context context, int height, int width) {
        super(new Space(context));
        itemView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
    }

}
