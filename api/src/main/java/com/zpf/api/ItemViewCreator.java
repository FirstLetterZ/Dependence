package com.zpf.api;

import android.view.View;

import com.zpf.api.IHolder;

public interface ItemViewCreator {
    IHolder<View> onCreateView(View parent, int type);

    void onBindView(IHolder<View> view, int type, int position, Object value);
}
