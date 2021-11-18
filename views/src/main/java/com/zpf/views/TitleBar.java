package com.zpf.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.zpf.views.type.ITitleBar;

/**
 * Created by ZPF on 2018/6/14.
 */
public class TitleBar extends FrameLayout implements ITitleBar {
    private final LinearLayout leftLayout;
    private final LinearLayout titleLayout;
    private final LinearLayout rightLayout;
    private final IconTextView tvLeft;
    private final IconTextView ivLeft;
    private final IconTextView title;
    private final IconTextView subtitle;
    private final IconTextView ivRight;
    private final IconTextView tvRight;

    public TitleBar(Context context) {
        this(context, null, 0);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int minWidth = (int) (30 * metrics.density);

        leftLayout = new LinearLayout(context);
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);
        leftLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        ivLeft = new IconTextView(context);
        ivLeft.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ivLeft.setMinWidth(minWidth);
        ivLeft.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        tvLeft = new IconTextView(context);
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        leftLayout.addView(ivLeft);
        leftLayout.addView(tvLeft);

        rightLayout = new LinearLayout(context);
        rightLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams rightLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        rightLayoutParams.gravity = Gravity.RIGHT;
        rightLayout.setLayoutParams(rightLayoutParams);

        ivRight = new IconTextView(context);
        ivRight.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ivRight.setMinWidth(minWidth);
        ivRight.setGravity(Gravity.RIGHT | Gravity.CENTER);

        tvRight = new IconTextView(context);
        tvRight.setGravity(Gravity.CENTER);
        tvRight.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

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
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        subtitle = new IconTextView(context);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        subtitle.setEllipsize(TextUtils.TruncateAt.END);
        subtitle.setSingleLine();
        subtitle.setMaxLines(1);
        subtitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        subtitle.setVisibility(GONE);

        titleLayout.addView(title);
        titleLayout.addView(subtitle);

        addView(titleLayout);
        addView(leftLayout);
        addView(rightLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ivLeft.checkViewShow();
        tvLeft.checkViewShow();
        ivRight.checkViewShow();
        tvRight.checkViewShow();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
    public ViewGroup getLayout() {
        return this;
    }

}