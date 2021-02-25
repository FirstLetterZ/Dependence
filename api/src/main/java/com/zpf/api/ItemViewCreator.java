package com.zpf.api;

import android.content.Context;
import android.view.View;

public interface ItemViewCreator {
    IHolder<View> onCreateView(Context context, int type);

    void onBindView(IHolder<View> view, int position);
}
