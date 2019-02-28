package com.zpf.tool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.TextView;

import java.lang.reflect.Field;

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

    public static boolean isRecyclerViewToTop(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        } else {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null || manager.getChildCount() == 0) {
                return true;
            } else {
                if (manager instanceof LinearLayoutManager) {
                    int firstChildTop = 0;
                    if (recyclerView.getChildCount() > 0) {
                        View firstChild = recyclerView.getChildAt(0);
                        if (firstChild == null) {
                            return false;
                        }
                        // 处理item高度超过一屏幕时的情况
                        if (firstChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                            return !recyclerView.canScrollVertically(-1);
                        }
                        RecyclerView.LayoutParams childParams = (RecyclerView.LayoutParams) firstChild.getLayoutParams();
                        firstChildTop = firstChild.getTop() - childParams.topMargin -
                                getRecyclerViewItemTopInset(childParams) - recyclerView.getPaddingTop();
                    }
                    if (((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition() < 1 && firstChildTop == 0) {
                        return true;
                    }
                } else if (manager instanceof StaggeredGridLayoutManager) {
                    int[] out = ((StaggeredGridLayoutManager) manager).findFirstCompletelyVisibleItemPositions((int[]) null);
                    if (out[0] < 1) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private static int getRecyclerViewItemTopInset(RecyclerView.LayoutParams layoutParams) {
        try {
            Field field = RecyclerView.LayoutParams.class.getDeclaredField("mDecorInsets");
            field.setAccessible(true);
            Rect rect = (Rect) field.get(layoutParams);
            return rect.top;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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

    public static boolean isRecyclerViewToBottom(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager != null && manager.getItemCount() > 0) {
                if (manager instanceof LinearLayoutManager) {
                    int lastVisiblePosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    int childCount = manager.getItemCount();
                    boolean isLast = lastVisiblePosition == childCount - 1;
                    boolean cannotScroll = !recyclerView.canScrollVertically(1);
                    return isLast && cannotScroll;
                } else if (manager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
                    int[] out = layoutManager.findLastCompletelyVisibleItemPositions(null);
                    int lastPosition = layoutManager.getItemCount() - 1;
                    for (int position : out) {
                        if (position == lastPosition) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 视图必须可以滚动
     * 即view.getChildAt(0).getHeight() 大于 view.getMeasuredHeight()
     *
     * @param view
     */
    public static boolean isViewGroupToBottom(ViewGroup view) {
        return view != null && view.getChildAt(0) != null &&
                view.getScrollY() >= view.getChildAt(0).getHeight() - view.getMeasuredHeight();
    }

    public static boolean isWebViewToBottom(WebView view) {
        return view != null && (float) view.getScrollY() >= (float) view.getContentHeight() * view.getScale() - (float) view.getMeasuredHeight();
    }

    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId != 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (height == 0) {
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                    context.getResources().getDisplayMetrics());
        }
        return height;
    }

    //收起软键盘
    public static void packUpKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            if (activity.getCurrentFocus() != null) {
                try {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pickUpKeyBoard(TextView view) {
        InputMethodManager mInputMethodManager = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    //弹出软键盘
    public static void showKeyBoard(Activity activity) {
        /*如果顶部视图可见高度大于2/3屏幕高度，则认定软键盘未弹出（大约计算）*/
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        if ((rect.bottom - rect.top) > (0.667 * activity.getResources().getDisplayMetrics().heightPixels)) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    public static void showKeyBoard(TextView view) {
        view.setFocusable(true);
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(view, 0);
        }
    }
}
