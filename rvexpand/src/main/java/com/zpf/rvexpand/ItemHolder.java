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

    @Override
    public View getRoot() {
        return itemView;
    }

    @Override
    public View findById(int id) {
        return realHolder.findById(id);
    }

    @Override
    public View findByTag(String tag) {
        return realHolder.findByTag(tag);
    }
}
