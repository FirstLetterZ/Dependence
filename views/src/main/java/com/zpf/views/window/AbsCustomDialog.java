package com.zpf.views.window;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.views.R;

/**
 * Created by ZPF on 2018/3/22.
 */
public abstract class AbsCustomDialog extends Dialog implements ICustomWindow {
    protected ICustomWindowManager manager;

    public AbsCustomDialog(@NonNull Context context) {
        this(context, R.style.customDialog);
        init();
    }

    public AbsCustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected AbsCustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            initWindow(dialogWindow);
        }
        initView();
    }

    protected abstract void initView();

    protected abstract void initWindow(@NonNull Window window);

    @Override
    protected void onStart() {
        super.onStart();
        if (manager != null) {
            manager.onShow(this);
        }
    }

    @Override
    public void show() {
        if (manager == null || manager.shouldShowImmediately(this)) {
            super.show();
        }
    }

    @Override
    public ICustomWindow setManager(ICustomWindowManager manager) {
        this.manager = manager;
        return this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (manager != null) {
            manager.onClose(this);
        }
    }

}