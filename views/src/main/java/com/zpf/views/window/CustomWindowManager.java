package com.zpf.views.window;

import java.util.LinkedList;

/**
 * Created by ZPF on 2022/6/1.
 */
public class CustomWindowManager implements ICustomWindowManager {
    protected ICustomWindow showingWindow;
    private volatile boolean isReleased = false;
    private final LinkedList<ICustomWindow> waitShowList = new LinkedList<>();
    private boolean isOnShowing = false;

    @Override
    public boolean shouldShowImmediately(ICustomWindow window) {
        if (isReleased || window == null || window.isShowing()) {
            return false;
        }
        if (showingWindow == null || !showingWindow.isShowing()) {
            return true;
        }
        if (!waitShowList.contains(window)) {
            waitShowList.add(window);
        }
        return false;
    }

    @Override
    public void onShow(ICustomWindow window) {
        if (!isReleased && window != null) {
            isOnShowing = true;
            if (showingWindow != null && showingWindow != window) {
                showingWindow.dismiss();
            }
            showingWindow = window;
            if (waitShowList.size() > 0) {
                waitShowList.remove(window);
            }
            isOnShowing = false;
        }
    }

    @Override
    public void onClose(ICustomWindow window) {
        if (isReleased || isOnShowing || window == null) {
            return;
        }
        if (waitShowList.size() > 0) {
            waitShowList.remove(window);
        }
        if (window == showingWindow || showingWindow == null || !showingWindow.isShowing()) {
            showingWindow = null;
            window = waitShowList.poll();
            if (window != null) {
                window.show();
            }
        }
    }

    @Override
    public boolean close() {
        if (isReleased || isOnShowing || showingWindow == null) {
            return false;
        }
        showingWindow.dismiss();
        return true;
    }

    @Override
    public void release() {
        isReleased = true;
        waitShowList.clear();
        if (showingWindow != null) {
            showingWindow.dismiss();
        }
        showingWindow = null;
    }

    @Override
    public void reset() {
        waitShowList.clear();
        isReleased = false;
    }

}
