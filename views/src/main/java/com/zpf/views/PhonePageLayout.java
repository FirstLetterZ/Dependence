package com.zpf.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.views.type.IDecorative;
import com.zpf.views.type.ITopBar;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/11/23.
 */
public class PhonePageLayout extends ViewGroup implements IDecorative<View>, ViewGroup.OnHierarchyChangeListener {
    private final ViewNode decorationNodes = new ViewNode(null, 0);
    private final ITopBar topBar;
    private View contentView;
    private boolean contentBelowTitle;

    public PhonePageLayout(Context context) {
        this(context, null, 0);
    }

    public PhonePageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhonePageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        topBar = createTopBar(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PhonePageLayout);
        contentBelowTitle = array.getBoolean(R.styleable.PhonePageLayout_contentBelowTitle, true);
        array.recycle();
        setOnHierarchyChangeListener(this);
    }

    protected ITopBar createTopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        if (topBar != null) {
            return topBar;
        }
        return new TopBar(context, attrs, defStyleAttr);
    }

    public void setContentView(int viewResId) {
        View view = LayoutInflater.from(getContext()).inflate(viewResId, this, false);
        setContentView(view);
    }

    public void setContentView(View contentView) {
        if (contentView != null) {
            removeView(contentView);
        }
        this.contentView = contentView;
        if (contentView != null) {
            addView(contentView);
        }
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentBelowTitle(boolean contentBelowTitle) {
        if (this.contentBelowTitle != contentBelowTitle) {
            this.contentBelowTitle = contentBelowTitle;
            requestLayout();
        }
    }

    @Override
    public void addDecoration(View view, int hierarchy) {
        if (view == null || view.getParent() != null) {
            return;
        }
        ViewGroup.LayoutParams vlp = view.getLayoutParams();
        LayoutParams lp;
        if (vlp instanceof LayoutParams) {
            lp = ((LayoutParams) vlp);
        } else if (vlp == null) {
            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp = new LayoutParams(vlp);
        }
        lp.hierarchy = hierarchy;
        lp.isContentView = false;
        super.addView(view, lp);
    }

    @Override
    public List<View> queryDecoration(int hierarchy) {
        List<View> nodeList = new LinkedList<>();
        ViewNode node = decorationNodes.next;
        while (node != null) {
            if (node.hierarchy == hierarchy) {
                nodeList.add(node.view);
            } else if (node.hierarchy > hierarchy) {
                break;
            }
            node = node.next;
        }
        return nodeList;
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.top = 0;
        return super.fitSystemWindows(insets);
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        if (child == null || child == topBar.getView()) {
            return;
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.isContentView) {
            if (contentView != null) {
                removeView(contentView);
            }
            contentView = child;
        } else {
            boolean addToLast = true;
            ViewNode node = decorationNodes.next;
            ViewNode lastNode = decorationNodes;
            ViewNode insertNode = new ViewNode(child, lp.hierarchy);
            while (node != null) {
                lastNode = node;
                if (node.hierarchy > lp.hierarchy) {
                    addToLast = false;
                    if (node.prev != null) {
                        node.prev.next = insertNode;
                    }
                    insertNode.prev = node.prev;
                    node.prev = insertNode;
                    insertNode.next = node;
                    break;
                }
                node = node.next;
            }
            if (addToLast) {
                lastNode.next = insertNode;
                insertNode.prev = lastNode;
            }
        }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if (child == null || child == topBar.getView()) {
            return;
        }
        if (child == contentView) {
            contentView = null;
            return;
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.isContentView) {
            return;
        }
        ViewNode node = decorationNodes.next;
        while (node != null) {
            if (node.view == child) {
                node.remove();
                break;
            }
            node = node.next;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthUsed = getPaddingLeft() + getPaddingRight();
        int heightUsed = getPaddingTop() + getPaddingBottom();
        measureChild(topBar.getView(), widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        if (contentBelowTitle) {
            heightUsed = heightUsed + topBar.getView().getMeasuredHeight();
        }
        if (contentView != null && contentView.getVisibility() != View.GONE) {
            measureChildWithMargins(contentView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        }
        ViewNode node = decorationNodes.next;
        while (node != null) {
            measureChildWithMargins(node.view, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
            node = node.next;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int direction = getLayoutDirection();
        final int realLeft = l + getPaddingStart();
        final int realTop = t + getPaddingTop();
        final int realRight = r - getPaddingEnd();
        final int realBottom = b - getPaddingBottom();
        int topBarHeight = layoutChild(topBar.getView(), realLeft, realTop, realRight, realBottom, direction);
        int contentStatTop;
        if (contentBelowTitle) {
            contentStatTop = realTop + topBarHeight;
        } else {
            contentStatTop = realTop;
        }
        if (contentView != null && contentView.getVisibility() != View.GONE) {
            layoutChild(contentView, realLeft, contentStatTop, realRight, realBottom, direction);
        }
        ViewNode node = decorationNodes.next;
        while (node != null) {
            layoutChild(node.view, realLeft, contentStatTop, realRight, realBottom, direction);
            node = node.next;
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child != null && child.getVisibility() == View.VISIBLE) {
            return super.drawChild(canvas, child, drawingTime);
        }
        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final long drawTime = getDrawingTime();
        drawChild(canvas, contentView, drawTime);
        ViewNode node = decorationNodes.next;
        while (node != null) {
            drawChild(canvas, node.view, drawTime);
            node = node.next;
        }
        drawChild(canvas, topBar.getView(), drawTime);
    }

    private void measureChild(View child, int parentWidthMeasureSpec, int widthUsed,
                              int parentHeightMeasureSpec, int heightUsed) {
        if (child == null || child.getVisibility() == View.GONE) {
            return;
        }
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof MarginLayoutParams) {
            MarginLayoutParams mlp = (MarginLayoutParams) lp;
            widthUsed = widthUsed + mlp.leftMargin + mlp.rightMargin;
            heightUsed = heightUsed + mlp.topMargin + mlp.bottomMargin;
        }
        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(
                parentWidthMeasureSpec, widthUsed, lp.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
                parentHeightMeasureSpec, heightUsed, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private int layoutChild(View child, int left, int top, int right, int bottom, int layoutDirection) {
        if (child == null || child.getVisibility() == View.GONE) {
            return 0;
        }
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        final int width = child.getMeasuredWidth();
        final int height = child.getMeasuredHeight();
        int childLeft;
        int childTop;
        int childMarginLeft = 0;
        int childMarginRight = 0;
        int childMarginTop = 0;
        int childMarginBottom = 0;
        int gravity = -1;
        if (lp instanceof LayoutParams) {
            gravity = ((LayoutParams) lp).gravity;
            childMarginLeft = ((LayoutParams) lp).leftMargin;
            childMarginRight = ((LayoutParams) lp).rightMargin;
            childMarginTop = ((LayoutParams) lp).topMargin;
            childMarginBottom = ((LayoutParams) lp).bottomMargin;
        }
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = left + (right - left - width) / 2 +
                        childMarginLeft - childMarginRight;
                break;
            case Gravity.RIGHT:
                childLeft = right - width - childMarginRight;
                break;
            case Gravity.LEFT:
            default:
                childLeft = left + childMarginLeft;
        }

        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = top + (bottom - top - height) / 2 +
                        childMarginTop - childMarginBottom;
                break;
            case Gravity.BOTTOM:
                childTop = bottom - height - childMarginBottom;
                break;
            default:
                childTop = top + childMarginTop;
        }
        child.layout(childLeft, childTop, childLeft + width, childTop + height);
        return height + childMarginTop + childMarginBottom;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return (LayoutParams) p;
        }
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public int hierarchy;
        public boolean isContentView = false;

        @SuppressLint("CustomViewStyleable")
        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PhonePageLayout);
            hierarchy = a.getInt(R.styleable.PhonePageLayout_hierarchy, 1);
            isContentView = a.getBoolean(R.styleable.PhonePageLayout_isContentView, false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
            if (source instanceof LayoutParams) {
                this.hierarchy = ((LayoutParams) source).hierarchy;
                this.isContentView = ((LayoutParams) source).isContentView;
            }
        }
    }

    private static class ViewNode {
        View view;
        int hierarchy;
        ViewNode prev;
        ViewNode next;

        ViewNode(View view, int hierarchy) {
            this.view = view;
            this.hierarchy = hierarchy;
        }

        void remove() {
            if (prev != null) {
                prev.next = next;
            }
        }
    }

}