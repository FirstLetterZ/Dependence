package com.zpf.api;

import android.view.KeyEvent;

public interface OnTouchKeyListener {

    boolean onKeyDown(int keyCode, KeyEvent event);

    boolean onKeyUp(int keyCode, KeyEvent event);
}