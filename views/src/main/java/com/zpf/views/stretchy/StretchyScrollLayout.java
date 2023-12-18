package com.zpf.views.stretchy;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zpf.views.R;

import java.util.HashMap;
import java.util.HashSet;

public class StretchyScrollLayout extends ViewGroup {

    public static final HashMap<Class<?>, IViewScrollChecker> viewScrollCheckerHashMap = new HashMap<>();
    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_OVER_BOUNDARY = 2;
    public static final int STATE_REVERTING = 3;

    protected final int[] boundaryWidths;
    protected final int[] boundaryStates = new int[4];
    protected final View[] boundaryViews = new View[4];
    protected View contentView = null;
    private int lastChildAction = MotionEvent.ACTION_CANCEL;
    protected float touchX = 0f;
    protected float touchY = 0f;
    protected int activePointerId = 0;
    protected int overScrollMultiple = 4;
    protected int rollbackSpeed = (int) (getContext().getResources().getDisplayMetrics().density * 1);
    protected final HashSet<IViewStateListener> stateListeners = new HashSet<>();
    protected Animator rollBackAnimator;//回滚动画
    protected final Animator.AnimatorListener rollBackListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(@NonNull Animator animation) {
        }
        @Override
        public void onAnimationEnd(@NonNull Animator animation) {
            rollBackAnimator = null;
        }
        @Override
        public void onAnimationCancel(@NonNull Animator animation) {
        }
        @Override
        public void onAnimationRepeat(@NonNull Animator animation) {
        }
    };

    public StretchyScrollLayout(Context context) {
        this(context, null, 0);
    }

    public StretchyScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public StretchyScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        boundaryWidths = new int[4];
        initWithTypedArray(context.obtainStyledAttributes(attrs, R.styleable.StretchyScrollLayout));

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StretchyScrollLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        boundaryWidths = new int[4];
        initWithTypedArray(context.obtainStyledAttributes(attrs, R.styleable.StretchyScrollLayout));
    }

    protected void initWithTypedArray(@Nullable TypedArray typedArray) {
        if (typedArray != null) {
            setBoundaryWidths(new int[]{typedArray.getDimensionPixelSize(R.styleable.StretchyScrollLayout_boundaryWidthLeft, 0), typedArray.getDimensionPixelSize(R.styleable.StretchyScrollLayout_boundaryWidthTop, 0), typedArray.getDimensionPixelSize(R.styleable.StretchyScrollLayout_boundaryWidthRight, 0), typedArray.getDimensionPixelSize(R.styleable.StretchyScrollLayout_boundaryWidthBottom, 0),});
            setOverScrollMultiple(typedArray.getInteger(R.styleable.StretchyScrollLayout_overScrollMultiple, 0));
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child == null) {
                continue;
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.slot_location == 0) {
                setContentView(child);
            } else {
                setBoundaryView(child, lp.slot_location - 1);
            }
        }
    }

    public void setContentView(@Nullable View view) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = LayoutParams.MATCH_PARENT;
                layoutParams.height = LayoutParams.MATCH_PARENT;
            }
        }
        replaceView(contentView, view);
        contentView = view;
    }

    public void setBoundaryWidths(int[] size) {
        if (size.length != boundaryWidths.length) {
            return;
        }
        for (int i = 0; i < boundaryWidths.length; i++) {
            boundaryWidths[i] = Math.max(0, size[i]);
        }
        if (rollBackAnimator == null) {
            scrollTo(getScrollX(), getScrollY());
        }
    }

    public void setBoundaryWidth(int size, int location) {
        if (location < 0 || location >= boundaryWidths.length) {
            return;
        }
        boundaryWidths[location] = Math.max(0, size);
        if (rollBackAnimator == null) {
            scrollTo(getScrollX(), getScrollY());
        }
    }

    public void setBoundaryView(View view, int location) {
        if (location < 0 || location >= boundaryViews.length) {
            return;
        }
        replaceView(boundaryViews[location], view);
        boundaryViews[location] = view;
    }

    public void setBoundaryViews(View[] views) {
        if (views.length != boundaryViews.length) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            replaceView(boundaryViews[i], views[i]);
            boundaryViews[i] = views[i];
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren(l, t, r, b);
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childMarginLeft = 0;
                int childMarginRight = 0;
                int childMarginTop = 0;
                int childMarginBottom = 0;
                final ViewGroup.LayoutParams lp = child.getLayoutParams();
                if (lp instanceof MarginLayoutParams) {
                    childMarginLeft = ((MarginLayoutParams) lp).leftMargin;
                    childMarginRight = ((MarginLayoutParams) lp).rightMargin;
                    childMarginTop = ((MarginLayoutParams) lp).topMargin;
                    childMarginBottom = ((MarginLayoutParams) lp).bottomMargin;
                }
                final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, childMarginLeft + childMarginRight, lp.width);
                final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, childMarginTop + childMarginBottom, lp.height);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    protected void layoutChildren(int l, int t, int r, int b) {
        View child = contentView;
        if (child != null) {
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            child.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight());
        }
        for (int i = 0; i < boundaryViews.length; i++) {
            child = boundaryViews[i];
            if (child == null) {
                continue;
            }
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (childHeight == 0 || childWidth == 0) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            switch (i) {
                case 0:
                    child.layout(l - childWidth - lp.rightMargin, t + lp.topMargin, l - lp.rightMargin, t + lp.topMargin + childHeight);
                    break;
                case 1:
                    child.layout(l + lp.leftMargin, t - childHeight - lp.bottomMargin, l + lp.leftMargin + childWidth, t - lp.bottomMargin);
                    break;
                case 2:
                    child.layout(r + lp.leftMargin, t + lp.topMargin, lp.leftMargin + childWidth, t + lp.topMargin + childHeight);
                    break;
                case 3:
                    child.layout(l + lp.leftMargin, b + lp.topMargin, l + lp.leftMargin + childWidth, b + childHeight + lp.topMargin);
                    break;
            }
        }
    }
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return p;
        }
        if (p instanceof MarginLayoutParams) {
            return new LayoutParams(((MarginLayoutParams) p));
        }
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int slot_location = 0;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StretchyScrollLayout_Layout);
            slot_location = a.getInt(R.styleable.StretchyScrollLayout_Layout_slot_location, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull MarginLayoutParams source) {
            super(source);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        final int actionIndex = ev.getActionIndex();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                stopRollback();
                activePointerId = ev.getPointerId(0);
                touchX = ev.getX();
                touchY = ev.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                activePointerId = ev.getPointerId(actionIndex);
                touchX = ev.getX(actionIndex);
                touchY = ev.getY(actionIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX(actionIndex);
                float y = ev.getY(actionIndex);
                int dx = (int) (touchX - x + 0.5f);
                int dy = (int) (touchY - y + 0.5f);
                touchX = x;
                touchY = y;
                boolean postToChildren = false;
                if (dx == 0 && dy == 0) {
                    postToChildren = true;
                } else {
                    int oldScrollX = getScrollX();
                    int oldScrollY = getScrollY();
                    if (dx != 0) {
                        if (oldScrollX == 0 && canContentScrollHorizontally(dx)) {
                            postToChildren = true;
                            dx = 0;
                        } else {
                            if (dx < 0) {
                                dx = (int) (dx / computeRateRatio(oldScrollX, -boundaryWidths[0]));
                            } else {
                                dx = (int) (dx / computeRateRatio(oldScrollX, boundaryWidths[2]));
                            }
                            if (oldScrollX > 0 && dx < 0) {
                                dx = Math.max(-oldScrollX, dx);
                            } else if (oldScrollX < 0 && dx > 0) {
                                dx = Math.min(-oldScrollX, dx);
                            }
                        }
                    }
                    if (dy != 0) {
                        if (oldScrollY == 0 && canContentScrollVertically(dy)) {
                            postToChildren = true;
                            dy = 0;
                        } else {
                            if (dy < 0) {
                                dy = (int) (dy / computeRateRatio(oldScrollY, -boundaryWidths[1]));
                            } else {
                                dy = (int) (dy / computeRateRatio(oldScrollY, boundaryWidths[3]));
                            }
                            if (oldScrollY > 0 && dy < 0) {
                                dy = Math.max(-oldScrollY, dy);
                            } else if (oldScrollY < 0 && dy > 0) {
                                dy = Math.min(-oldScrollY, dy);
                            }
                        }
                    }
                    if (dx != 0 || dy != 0) {
                        scrollBy(dx, dy);
                    }
                }
                if (postToChildren) {
                    if (lastChildAction != MotionEvent.ACTION_DOWN && lastChildAction != MotionEvent.ACTION_MOVE) {
                        ev.setAction(MotionEvent.ACTION_DOWN);
                    }
                } else {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startRollback();
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        lastChildAction = ev.getAction();
        return true;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        int minScrollSize = -boundaryWidths[0] * overScrollMultiple;
        int maxScrollSize = boundaryWidths[2] * overScrollMultiple;
        int offset = getScrollX();
        if (offset > minScrollSize && offset < maxScrollSize) {
            return true;
        }
        return canContentScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        int minScrollSize = -boundaryWidths[3] * overScrollMultiple;
        int maxScrollSize = boundaryWidths[1] * overScrollMultiple;
        int offset = getScrollY();
        if (offset > minScrollSize && offset < maxScrollSize) {
            return true;
        }
        return canContentScrollVertically(direction);
    }

    @Override
    public void scrollTo(int x, int y) {
        int minX = -boundaryWidths[0] * overScrollMultiple;
        int maxX = boundaryWidths[2] * overScrollMultiple;
        int minY = -boundaryWidths[1] * overScrollMultiple;
        int maxY = boundaryWidths[3] * overScrollMultiple;
        int realX = Math.max(Math.min(maxX, x), minX);
        int realY = Math.max(Math.min(maxY, y), minY);
        super.scrollTo(realX, realY);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        updateBoundaryStates(l, t);
    }

    public void addStateListener(IViewStateListener listener) {
        if (listener == null) {
            return;
        }
        stateListeners.add(listener);
    }

    public void removeStateListener(IViewStateListener listener) {
        if (listener == null) {
            return;
        }
        stateListeners.remove(listener);
    }

    public void setOverScrollMultiple(int overScrollMultiple) {
        this.overScrollMultiple = Math.max(1, overScrollMultiple);
    }

    public int getOverScrollMultiple() {
        return overScrollMultiple;
    }

    protected void replaceView(@Nullable View oldeView, @Nullable View newView) {
        ViewParent parent = null;
        if (oldeView != null) {
            parent = oldeView.getParent();
        }
        if (parent == this) {
            removeView(oldeView);
        }
        if (newView == null) {
            return;
        }
        parent = newView.getParent();
        if (parent == this) {
            return;
        }
        if (parent == null) {
            addView(newView);
        } else {
            ((ViewGroup) parent).removeView(newView);
            addView(newView);
        }
    }

    protected void stopRollback() {
        if (rollBackAnimator != null) {
            rollBackAnimator.cancel();
            rollBackAnimator = null;
            updateBoundaryStates(getScrollX(), getScrollY());
        }
    }

    protected void startRollback() {
        if (rollBackAnimator != null) {
            rollBackAnimator.cancel();
        }
        final int startX = getScrollX();
        final int startY = getScrollY();
        if (startX == 0 && startY == 0) {
            updateBoundaryStates(startX, startY);
            return;
        }
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addListener(rollBackListener);
        rollBackAnimator = animator;
        updateBoundaryStates(startX, startY);
        final int endX = computeHorizontallyRollbackEnd(startX);
        final int endY = computeVerticallyRollbackEnd(startY);
        long rollbackTime = Math.max(Math.abs(startX - endX), Math.abs(startY - endY)) / rollbackSpeed;
        long duration = Math.min(Math.max(48L, rollbackTime), 200L);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            float fv = (float) animator.getAnimatedValue();
            int x = (int) (startX + (endX - startX) * fv);
            int y = (int) (startY + (endY - startY) * fv);
            scrollTo(x, y);
        });
        animator.start();
    }

    protected void updateBoundaryStates(int scrollX, int scrollY) {
        int olsStateLeft = boundaryStates[0];
        int olsStateTop = boundaryStates[1];
        int olsStateRight = boundaryStates[2];
        int olsStateBottom = boundaryStates[3];
        boundaryStates[0] = computeState(0, Math.abs(Math.min(0, scrollX)), boundaryWidths[0]);
        boundaryStates[1] = computeState(1, Math.abs(Math.min(0, scrollY)), boundaryWidths[1]);
        boundaryStates[2] = computeState(2, Math.max(0, scrollX), boundaryWidths[2]);
        boundaryStates[3] = computeState(3, Math.max(0, scrollY), boundaryWidths[3]);
        if (olsStateLeft != boundaryStates[0] || olsStateTop != boundaryStates[1] || olsStateRight != boundaryStates[2] || olsStateBottom != boundaryStates[3]) {
            onStateChanged(boundaryStates[0], boundaryStates[1], boundaryStates[2], boundaryStates[3]);
        }
    }

    protected void onStateChanged(int left, int top, int right, int bottom) {
        for (IViewStateListener stateListener : stateListeners) {
            stateListener.onStateChanged(this, left, top, right, bottom);
        }
    }

    protected boolean canContentScrollVertically(int direction) {
        final View view = contentView;
        if (view == null) {
            return false;
        }
        IViewScrollChecker checker = viewScrollCheckerHashMap.get(view.getClass());
        if (checker != null) {
            return checker.canScrollVertically(view, direction);
        }
        if (view instanceof AbsListView) {
            ((AbsListView) view).canScrollList(direction);
        }
        return view.canScrollVertically(direction);
    }

    public boolean canContentScrollHorizontally(int direction) {
        if (direction == 0) {
            return false;
        }
        final View view = contentView;
        if (view == null) {
            return false;
        }
        IViewScrollChecker checker = viewScrollCheckerHashMap.get(view.getClass());
        if (checker != null) {
            return checker.canScrollHorizontally(view, direction);
        }
        return view.canScrollHorizontally(direction);
    }

    protected int computeHorizontallyRollbackEnd(int currentScroll) {
        return 0;
    }

    protected int computeVerticallyRollbackEnd(int currentScroll) {
        return 0;
    }

    private double computeRateRatio(int a, int b) {
        if (b == 0) {
            return 1;
        }
        float f = a * 1.0f / b;
        if (f <= 0f) {
            return 1;
        }
        return 1.0 + 3.0 * f / overScrollMultiple;
    }

    protected int computeState(int location, int scrollValue, int maxScrollValue) {
        int newState;
        if (scrollValue <= 0) {
            newState = STATE_IDLE;
        } else if (rollBackAnimator == null) {
            if (scrollValue < maxScrollValue) {
                newState = STATE_DRAGGING;
            } else {
                newState = STATE_OVER_BOUNDARY;
            }
        } else {
            newState = STATE_REVERTING;
        }
        return newState;
    }

    private void onPointerUp(MotionEvent ev) {
        final int actionIndex = ev.getActionIndex();
        if (ev.getPointerId(actionIndex) == activePointerId) {
            // Pick a new pointer to pick up the slack.
            final int newIndex = actionIndex == 0 ? 1 : 0;
            activePointerId = ev.getPointerId(newIndex);
            touchX = ev.getX(newIndex);
            touchY = ev.getY(newIndex);
        }
    }

}
