package com.zpf.views.window;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.views.BottomMenuView;
import com.zpf.views.R;

public class BottomDialog extends AbsCustomDialog {
    protected BottomMenuView menuView;

    public BottomDialog(@NonNull Context context) {
        super(context);
    }

    public BottomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BottomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void initView() {
        menuView = new BottomMenuView(getContext());
        setContentView(menuView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
    }

    public BottomMenuView getMenuView() {
        return menuView;
    }

    @Override
    protected void initWindow(@NonNull Window window) {
        super.initWindow(window);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialogAnim); // 设置显示动画
    }
}