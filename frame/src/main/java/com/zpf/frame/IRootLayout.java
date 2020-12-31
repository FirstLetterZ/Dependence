package com.zpf.frame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by ZPF on 2018/6/14.
 */

public interface IRootLayout {

    @NonNull
    View getStatusBar();

    @NonNull
    ITitleBar getTitleBar();

    @Nullable
    View getContentView();

    @NonNull
    ViewGroup getLayout();

    void changeTitleBar(@NonNull ITitleBar titleBar);

    void setContentView(@NonNull View view);

    void setContentView(int layoutId);

    void addContentDecoration(@NonNull View child, int hierarchy, @Nullable ViewGroup.LayoutParams params);

    void addPageDecoration(@NonNull View child, int hierarchy, @Nullable ViewGroup.LayoutParams params);
}
