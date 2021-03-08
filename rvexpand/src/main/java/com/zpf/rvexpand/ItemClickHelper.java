package com.zpf.rvexpand;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.zpf.api.OnItemClickListener;
import com.zpf.api.OnItemViewClickListener;

import java.lang.ref.SoftReference;

public class ItemClickHelper {
    public OnItemClickListener itemClickListener;
    public OnItemViewClickListener itemViewClickListener;
    private final SparseArray<SoftReference<View.OnClickListener>> clickCache = new SparseArray<>();

    public void bindItemClick(View view, final int position) {
        View targetView = view;
        while (targetView instanceof ViewGroup) {
            if (((ViewGroup) view).getChildCount() == 1) {
                targetView = ((ViewGroup) view).getChildAt(0);
            } else {
                break;
            }
        }
        if (targetView == null) {
            return;
        }
        if (itemViewClickListener == null && itemClickListener == null) {
            targetView.setOnClickListener(null);
        } else {
            View.OnClickListener clickListener = null;
            SoftReference<View.OnClickListener> weakCache = clickCache.get(position);
            if (weakCache != null) {
                clickListener = weakCache.get();
            }
            if (clickListener == null) {
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemViewClickListener != null) {
                            itemViewClickListener.onItemViewClick(position, v);
                        } else if (itemClickListener != null) {
                            itemClickListener.onItemClick(position);
                        }
                    }
                };
                weakCache = new SoftReference<>(clickListener);
                clickCache.put(position, weakCache);
            }
            targetView.setOnClickListener(clickListener);
        }
    }
}
