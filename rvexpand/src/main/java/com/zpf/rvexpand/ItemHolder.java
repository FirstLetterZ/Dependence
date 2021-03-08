package com.zpf.rvexpand;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zpf.api.IHolder;

/**
 * @author Created by ZPF on 2021/2/25.
 */
public class ItemHolder extends RecyclerView.ViewHolder implements IHolder<View> {
    private final IHolder<View> realHolder;
    private SparseArray<View> viewCache;

    public ItemHolder(@NonNull IHolder<View> holder) {
        super((View) holder.getRoot());
        realHolder = holder;
    }

    public ItemHolder(@NonNull View itemView) {
        super(itemView);
        realHolder = null;
    }

    @Override
    public View getRoot() {
        return itemView;
    }

    @Override
    public View findById(int id) {
        if (realHolder != null) {
            return realHolder.findById(id);
        } else {
            View view;
            if (viewCache == null) {
                viewCache = new SparseArray<>();
                view =null;
            } else {
                view = viewCache.get(id);
            }
            if (view == null) {
                view = itemView.findViewById(id);
            }
            if (view != null) {
                viewCache.get(id, view);
            }
            return view;
        }
    }

    @Override
    public View findByTag(String tag) {
        if (realHolder != null) {
            return realHolder.findByTag(tag);
        } else {
            return itemView.findViewWithTag(tag);
        }
    }
}
