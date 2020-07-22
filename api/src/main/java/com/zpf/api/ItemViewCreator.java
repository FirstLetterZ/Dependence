package com.zpf.api;

import android.content.Context;
import android.view.View;

public interface ItemViewCreator {
    View onCreateView(Context context, int type);

    void onBindView(View view, int position);
}
