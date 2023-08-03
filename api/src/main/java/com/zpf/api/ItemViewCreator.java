package com.zpf.api;

import android.view.View;

public interface ItemViewCreator {
    IViewHolder onCreateView(View parent, int type);

    void onBindView(IViewHolder view, int type, int position, Object value);
}
