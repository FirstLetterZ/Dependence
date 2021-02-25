package com.zpf.api;

import android.view.View;

public interface ItemViewCreator {
    IHolder<View> onCreateView(View parent, int position, int type);

    void onBindView(IHolder<View> view, int position, Object value);
}
