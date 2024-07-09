package com.zpf.views;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsListViewAdapter<T, V> extends BaseAdapter {
    abstract V onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    abstract void onBindViewHolder(@NonNull V holder, int position);

    abstract View getHolderRootView(@NonNull V holder);

    protected final ArrayList<T> dataList = new ArrayList<>();

    public boolean removeAt(int position) {
        if (position < 0 || position >= dataList.size()) {
            return false;
        }
        T res = dataList.remove(position);
        notifyDataSetChanged();
        return res != null;
    }
    public void addData(@Nullable T data) {
        if (data == null) {
            return;
        }
        dataList.add(data);
        notifyDataSetChanged();
    }
    public void addDataList(@Nullable List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        dataList.addAll(list);
        notifyDataSetChanged();
    }
    public void setDataList(@Nullable List<T> list) {
        dataList.clear();
        if (list != null && !list.isEmpty()) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }
    public boolean updateItem(int position, T data) {
        if (data == null || position < 0 || position >= dataList.size()) {
            return false;
        }
        dataList.set(position, data);
        return true;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }
    @Override
    public T getItem(int position) {
        if (position < 0 || position >= dataList.size()) {
            return null;
        }
        return dataList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        V holder = null;
        if (convertView != null) {
            try {
                holder = ((V) convertView.getTag());
            } catch (Exception e) {
                //
            }
        }
        if (holder == null) {
            holder = onCreateViewHolder(parent, getItemViewType(position));
        }
        onBindViewHolder(holder, position);
        View itemView = getHolderRootView(holder);
        itemView.setTag(holder);
        return itemView;
    }
}
