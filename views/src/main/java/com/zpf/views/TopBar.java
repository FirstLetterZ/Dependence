package com.zpf.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.views.type.ITopBar;

/**
 * @author Created by ZPF on 2021/11/23.
 */
public class TopBar extends ViewGroup implements ITopBar {

    private final StatusBar statusBar;
    private final LinearLayout leftLayout;
    private final LinearLayout titleLayout;
    private final LinearLayout rightLayout;
    private final IconTextView tvLeft;
    private final IconTextView ivLeft;
    private final IconTextView title;
    private final IconTextView subtitle;
    private final IconTextView ivRight;
    private final IconTextView tvRight;
    private Drawable bottomLineDrawable;
    private final int defTitleBarHeight;
    private int statusBarHeight;
    private int titleBarHeight;
    private int bottomLineHeight;

    public TopBar(Context context) {
        this(context, null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int minWidth = (int) (30 * metrics.density);
        defTitleBarHeight = (int) (44 * metrics.density);
        titleBarHeight = defTitleBarHeight;
        statusBar = new StatusBar(context, attrs, defStyleAttr);

        leftLayout = new LinearLayout(context);
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftLayout.setMinimumWidth(minWidth);
        leftLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));

        ivLeft = new IconTextView(context);
        ivLeft.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));
        ivLeft.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        tvLeft = new IconTextView(context);
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));

        leftLayout.addView(ivLeft);
        leftLayout.addView(tvLeft);

        rightLayout = new LinearLayout(context);
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);
        rightLayout.setMinimumWidth(minWidth);
        rightLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));

        ivRight = new IconTextView(context);
        ivRight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));
        ivRight.setMinWidth(minWidth);
        ivRight.setGravity(Gravity.RIGHT | Gravity.CENTER);

        tvRight = new IconTextView(context);
        tvRight.setGravity(Gravity.CENTER);
        tvRight.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT));

        rightLayout.addView(tvRight);
        rightLayout.addView(ivRight);

        titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        titleLayout.setGravity(Gravity.CENTER);
        titleLayout.setPadding(3 * minWidth, 0, 3 * minWidth, 0);

        title = new IconTextView(context);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setSingleLine();
        title.setMaxLines(1);
        title.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        subtitle = new IconTextView(context);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        subtitle.setEllipsize(TextUtils.TruncateAt.END);
        subtitle.setSingleLine();
        subtitle.setMaxLines(1);
        subtitle.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        subtitle.setVisibility(GONE);

        titleLayout.addView(title);
        titleLayout.addView(subtitle);

        addView(statusBar);
        addView(titleLayout);
        addView(leftLayout);
        addView(rightLayout);
        super.setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ivLeft.checkViewShow();
        tvLeft.checkViewShow();
        ivRight.checkViewShow();
        tvRight.checkViewShow();
        subtitle.checkViewShow();
        if (statusBar.getVisibility() == View.GONE) {
            statusBarHeight = 0;
        } else {
            statusBarHeight = statusBar.getMinimumHeight();
        }
        int titleBarHeightMeasureSpec = MeasureSpec.makeMeasureSpec(titleBarHeight, MeasureSpec.EXACTLY);
        int size = Math.max(0, MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight());
        int totalWidthMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST);
        int showTitleBarHeight = 0;
        if (titleLayout.getVisibility() != View.GONE) {
            titleLayout.measure(totalWidthMeasureSpec, titleBarHeightMeasureSpec);
            showTitleBarHeight = titleBarHeight;
        }
        if (leftLayout.getVisibility() != View.GONE) {
            leftLayout.measure(totalWidthMeasureSpec, titleBarHeightMeasureSpec);
            showTitleBarHeight = titleBarHeight;
        }
        if (rightLayout.getVisibility() != View.GONE) {
            rightLayout.measure(totalWidthMeasureSpec, titleBarHeightMeasureSpec);
            showTitleBarHeight = titleBarHeight;
        }
        int totalHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                statusBarHeight + showTitleBarHeight + bottomLineHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, totalHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (statusBar.getVisibility() != View.GONE) {
            statusBar.layout(l, t, r, t + statusBarHeight);
        }
        if (titleLayout.getVisibility() != View.GONE) {
            titleLayout.layout(l + getPaddingLeft(), t + statusBarHeight,
                    r - getPaddingRight(), t + statusBarHeight + titleBarHeight);
        }
        if (leftLayout.getVisibility() != View.GONE) {
            leftLayout.layout(l + getPaddingLeft(), t + statusBarHeight,
                    l + getPaddingLeft() + leftLayout.getMeasuredWidth(), t + statusBarHeight + titleBarHeight);
        }
        if (rightLayout.getVisibility() != View.GONE) {
            rightLayout.layout(r - getPaddingRight() - rightLayout.getMeasuredWidth(),
                    t + statusBarHeight, r - getPaddingRight(), t + statusBarHeight + titleBarHeight);
        }
        if (bottomLineDrawable != null) {
            bottomLineDrawable.setBounds(l + getPaddingLeft(), b - bottomLineHeight,
                    r - getPaddingRight(), b);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (bottomLineHeight > 0 && bottomLineDrawable != null) {
            bottomLineDrawable.draw(canvas);
        }
    }

    @Override
    public void setLayoutParams(LayoutParams params) {
        if (params != null) {
            params.width = LayoutParams.MATCH_PARENT;
        }
        super.setLayoutParams(params);
    }

    @Override
    public LayoutParams getLayoutParams() {
        LayoutParams layoutParams = super.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.height = statusBarHeight + titleBarHeight + bottomLineHeight;
            if (layoutParams.height == 0) {
                layoutParams.height = LayoutParams.MATCH_PARENT;
            }
        }
        return layoutParams;
    }

    @Override
    public StatusBar getStatusBar() {
        return statusBar;
    }

    @Override
    public IconTextView getLeftImage() {
        return ivLeft;
    }

    @Override
    public IconTextView getLeftText() {
        return tvLeft;
    }

    @Override
    public ViewGroup getLeftLayout() {
        return leftLayout;
    }

    @Override
    public IconTextView getRightImage() {
        return ivRight;
    }

    @Override
    public IconTextView getRightText() {
        return tvRight;
    }

    @Override
    public ViewGroup getRightLayout() {
        return rightLayout;
    }

    @Override
    public IconTextView getTitle() {
        return title;
    }

    @Override
    public IconTextView getSubTitle() {
        return subtitle;
    }

    @Override
    public ViewGroup getTitleLayout() {
        return titleLayout;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setBottomLine(@Nullable Drawable drawable, int height) {
        bottomLineDrawable = drawable;
        if (drawable != null && height > 0) {
            bottomLineHeight = height;
        } else {
            bottomLineHeight = 0;
        }
    }

    @Override
    public void setTitleBarHeight(int height) {
        if (height >= 0) {
            this.titleBarHeight = height;
        } else {
            this.titleBarHeight = defTitleBarHeight;
        }
    }

}