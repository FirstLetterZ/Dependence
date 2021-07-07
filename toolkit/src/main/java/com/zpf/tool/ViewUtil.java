package com.zpf.tool;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;

public class ViewUtil {
    public static boolean isViewToTop(View view) {
        return view != null && view.getScrollY() == 0;
    }

    public static boolean isAbsListViewToTop(AbsListView absListView) {
        if (absListView == null) {
            return false;
        } else {
            int firstChildTop = 0;
            if (absListView.getChildCount() > 0) {
                View childFirst = absListView.getChildAt(0);
                firstChildTop = childFirst.getTop() - absListView.getPaddingTop();
            }
            return absListView.getFirstVisiblePosition() == 0 && firstChildTop == 0;
        }
    }

    public static boolean isAbsListViewToBottom(AbsListView absListView) {
        if (absListView != null && absListView.getAdapter() != null && absListView.getChildCount() > 0
                && absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1) {
            View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);
            return lastChild.getBottom() <= absListView.getMeasuredHeight();
        } else {
            return false;
        }
    }

    /**
     * 视图必须可以滚动
     * 即view.getChildAt(0).getHeight() 大于 view.getMeasuredHeight()
     */
    public static boolean isViewGroupToBottom(ViewGroup view) {
        return view != null && view.getChildAt(0) != null &&
                view.getScrollY() >= view.getChildAt(0).getHeight() - view.getMeasuredHeight();
    }

    public static boolean isWebViewToBottom(WebView view) {
        return view != null && (float) view.getScrollY() >= (float) view.getContentHeight() * view.getScale() - (float) view.getMeasuredHeight();
    }


    //获取虚拟按键高度
    public static int getNavigationBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen",
                "android");
        if (resourceId != 0) {
            try {
                height = context.getResources().getDimensionPixelSize(resourceId);
            } catch (Exception e) {
                //
            }
        }
        return height;
    }

    //收起软键盘
    public static boolean pickUpKeyboard(Activity activity) {
        if (activity == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && activity.getCurrentFocus() != null) {
            try {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean pickUpKeyBoard(View view) {
        if (view == null) {
            return false;
        }
        InputMethodManager mInputMethodManager = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            return true;
        }
        return false;

    }

    //弹出软键盘
    public static boolean showKeyBoard(Activity activity) {
        if (activity == null) {
            return false;
        }
        /*如果顶部视图可见高度大于2/3屏幕高度，则认定软键盘未弹出（大约计算）*/
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        if ((rect.bottom - rect.top) > (0.667 * activity.getResources().getDisplayMetrics().heightPixels)) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                return true;
            }
        }
        return false;
    }

    public static boolean showKeyBoard(View view) {
        if (view == null) {
            return false;
        }
        view.setFocusable(true);
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(view, 0);
            return true;
        }
        return false;
    }

    public static void setClampBgDrawable(final View targetView, int resId, Resources res, boolean clampX) {
        setClampBgDrawable(targetView, resId, res, clampX, true);
    }

    private static void setClampBgDrawable(final View targetView, final int resId, final Resources res,
                                           final boolean clampX, boolean tryOnPreDraw) {
        int targetWidth = targetView.getWidth();
        if (targetWidth > 0) {
            //测量图片实际大小
            BitmapFactory.Options measureOpts = new BitmapFactory.Options();
            measureOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, measureOpts);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDensity = res.getDisplayMetrics().densityDpi;
            opts.inTargetDensity = targetWidth * opts.inDensity / measureOpts.outWidth;
            Bitmap bitmap = BitmapFactory.decodeResource(res, resId, opts);
            //不能使用过时的构造方法，否则可能会不生效
            BitmapDrawable bgDrawable = new BitmapDrawable(res, bitmap);
            //设置填充模式，由于X轴充满视图，所以TileMode可以为null
            if (clampX) {
                bgDrawable.setTileModeXY(Shader.TileMode.CLAMP, null);
            } else {
                bgDrawable.setTileModeXY(null, Shader.TileMode.CLAMP);
            }
            bgDrawable.setDither(true);
            targetView.setBackground(bgDrawable);
        } else if (tryOnPreDraw) {
            targetView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    targetView.getViewTreeObserver().removeOnPreDrawListener(this);
                    setClampBgDrawable(targetView, resId, res, clampX, false);
                    return false;
                }
            });
        }
    }
}
