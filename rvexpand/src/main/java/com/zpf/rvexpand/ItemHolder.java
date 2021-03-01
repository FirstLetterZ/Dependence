package com.zpf.rvexpand;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zpf.api.IHolder;

/**
 * @author Created by ZPF on 2021/2/25.
 */
public class ItemHolder extends RecyclerView.ViewHolder implements IHolder<View> {
    private final IHolder<View> realHolder;

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
            return itemView.findViewById(id);
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
