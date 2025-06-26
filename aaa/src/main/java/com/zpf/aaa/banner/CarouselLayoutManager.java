package com.zpf.aaa.banner;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class CarouselLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private int itemWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private int itemHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    private int itemSpace = 0;
    private int mOrientation = RecyclerView.HORIZONTAL;
    private int mCurrent = -1;
    private static final int DIRECTION_FORWARD = 1;
    private static final int DIRECTION_REVERSE = -1;
    private static final int DIRECTION_CENTER = 0;
    private final float anchorZ = Float.MAX_VALUE / 4f;
    private OnChangeListener mListener;

    public void setOrientation(int orientation) {
        if (orientation == mOrientation) {
            return;
        }
        this.mOrientation = orientation;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setItemSize(int width, int height) {
        itemWidth = width;
        itemHeight = height;
    }

    public Size getItemSize() {
        return new Size(itemWidth, itemHeight);
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public void setItemSpace(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    public int getCurrentIndex() {
        return mCurrent;
    }

    public void setOnChangeListener(OnChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void scrollToPosition(int position) {
        if (mCurrent != position) {
            int old = mCurrent;
            mCurrent = position;
            requestLayout();
            onSelectChanged(old, position);
        }
    }

    public void smoothScrollTo(Context context, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(context) {
            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                return ((boxStart + boxEnd) - (viewStart + viewEnd)) / 2;
            }
        };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == RecyclerView.HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == RecyclerView.VERTICAL;
    }

    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        View firstView = getChildAt(0);
        if (firstView == null) {
            return null;
        }
        int firstChildPos = getPosition(firstView);
        float direction;
        if (targetPosition < firstChildPos) {
            direction = -1f;
        } else {
            direction = 1f;
        }
        if (mOrientation == RecyclerView.HORIZONTAL) {
            return new PointF(direction, 0f);
        } else {
            return new PointF(0f, direction);
        }
    }

    @Override
    public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = itemWidth;
            layoutParams.height = itemHeight;
        }
        super.measureChildWithMargins(child, widthUsed, heightUsed);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state == null || recycler == null || state.isPreLayout()) {
            return;
        }
        int size = getItemCount();
        if (size <= 0) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        Rect parentRect = new Rect(0, 0, getWidth(), getHeight());
        int oldPosition = mCurrent;
        int anchorPosition;
        if (mCurrent < 0) {
            anchorPosition = 0;
        } else {
            anchorPosition = mCurrent % size;
        }
        mCurrent = anchorPosition;
        View anchorView = recycler.getViewForPosition(anchorPosition);
        measureChildWithMargins(anchorView, 0, 0);
        if (mOrientation == RecyclerView.HORIZONTAL) {
            int i = 0;
            Rect anchorRect = layoutChild(anchorView, parentRect, DIRECTION_CENTER, DIRECTION_CENTER);
            addView(anchorView);
            anchorView.setZ(anchorZ - i);
            fillHorizontal(anchorPosition, size, new Rect(0, 0, anchorRect.left, getHeight()), DIRECTION_REVERSE, recycler);
            fillHorizontal(anchorPosition, size, new Rect(anchorRect.right, 0, getWidth(), getHeight()), DIRECTION_FORWARD, recycler);
        } else {
            int i = 0;
            Rect anchorRect = layoutChild(anchorView, parentRect, DIRECTION_CENTER, DIRECTION_CENTER);
            addView(anchorView);
            anchorView.setZ(anchorZ - i);
            fillVertical(anchorPosition, size, new Rect(0, 0, getWidth(), anchorRect.top), DIRECTION_REVERSE, recycler);
            fillVertical(anchorPosition, size, new Rect(0, anchorRect.bottom, getWidth(), getHeight()), DIRECTION_FORWARD, recycler);
        }
        if (oldPosition != anchorPosition) {
            onSelectChanged(oldPosition, anchorPosition);
        }
    }
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
        if (recyclerView == null) {
            return;
        }
        smoothScrollTo(recyclerView.getContext(), position);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            scrollToCenterView();
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mOrientation == RecyclerView.VERTICAL || dx == 0 || getChildCount() == 0 || recycler == null) {
            return 0;
        }
        int itemCount = getItemCount();
        if (itemCount <= 0) {
            return 0;
        }
        offsetChildrenHorizontal(-dx);
        Rect containerRect = new Rect(0, 0, getWidth(), getHeight());
        if (dx < 0) {
            View firstView = getChildAt(0);
            if (firstView == null) {
                return 0;
            }
            ViewGroup.LayoutParams firstLp = firstView.getLayoutParams();
            if (firstLp instanceof ViewGroup.MarginLayoutParams) {
                containerRect.right = firstView.getLeft() - ((ViewGroup.MarginLayoutParams) firstLp).leftMargin;
            } else {
                containerRect.right = firstView.getLeft();
            }
            int anchorPosition = getPosition(firstView);
            fillHorizontal(anchorPosition, itemCount, containerRect, DIRECTION_REVERSE, recycler);
        } else {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView == null) {
                return 0;
            }
            ViewGroup.LayoutParams firstLp = lastView.getLayoutParams();
            if (firstLp instanceof ViewGroup.MarginLayoutParams) {
                containerRect.left = lastView.getRight() + ((ViewGroup.MarginLayoutParams) firstLp).rightMargin;
            } else {
                containerRect.left = lastView.getRight();
            }
            int anchorPosition = getPosition(lastView);
            fillHorizontal(anchorPosition, itemCount, containerRect, DIRECTION_FORWARD, recycler);
        }
        recyclerChildView(recycler);
        return dx;
    }
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mOrientation == RecyclerView.HORIZONTAL || dy == 0 || getChildCount() == 0 || recycler == null) {
            return 0;
        }
        int itemCount = getItemCount();
        if (itemCount <= 0) {
            return 0;
        }
        offsetChildrenVertical(-dy);
        Rect containerRect = new Rect(0, 0, getWidth(), getHeight());
        if (dy < 0) {
            View firstView = getChildAt(0);
            if (firstView == null) {
                return 0;
            }
            ViewGroup.LayoutParams firstLp = firstView.getLayoutParams();
            if (firstLp instanceof ViewGroup.MarginLayoutParams) {
                containerRect.bottom = firstView.getTop() - ((ViewGroup.MarginLayoutParams) firstLp).topMargin;
            } else {
                containerRect.bottom = firstView.getTop();
            }
            int anchorPosition = getPosition(firstView);
            fillVertical(anchorPosition, itemCount, containerRect, DIRECTION_REVERSE, recycler);
        } else {
            View lastView = getChildAt(getChildCount() - 1);
            if (lastView == null) {
                return 0;
            }
            ViewGroup.LayoutParams firstLp = lastView.getLayoutParams();
            if (firstLp instanceof ViewGroup.MarginLayoutParams) {
                containerRect.top = lastView.getBottom() + ((ViewGroup.MarginLayoutParams) firstLp).bottomMargin;
            } else {
                containerRect.top = lastView.getBottom();
            }
            int anchorPosition = getPosition(lastView);
            fillVertical(anchorPosition, itemCount, containerRect, DIRECTION_FORWARD, recycler);
        }
        recyclerChildView(recycler);
        return dy;
    }

    private void onSelectChanged(int oldPosition, int newPosition) {
        if (mListener != null) {
            mListener.onSelectChanged(oldPosition, newPosition);
        }
    }

    private void scrollToCenterView() {
        int itemCount = getItemCount();
        if (itemCount <= 0) {
            return;
        }
        float middleX = getWidth() / 2f;
        float middleY = getHeight() / 2f;
        View centerView = null;
        float diffTemp = Float.MAX_VALUE;
        final int orientation = mOrientation;
        for (int i = 0; i < itemCount; i++) {
            View child = getChildAt(i);
            if (child != null) {
                float itemDiff;
                if (orientation == RecyclerView.HORIZONTAL) {
                    itemDiff = Math.abs((child.getRight() + child.getLeft()) / 2f - middleX);
                } else {
                    itemDiff = Math.abs((child.getBottom() + child.getTop()) / 2f - middleY);
                }
                if (itemDiff < diffTemp) {
                    centerView = child;
                    diffTemp = itemDiff;
                }
            }
        }
        if (centerView == null) {
            return;
        }
        int anchorPosition = getPosition(centerView);
        if (diffTemp > 1f) {
            smoothScrollTo(centerView.getContext(), anchorPosition);
        } else {
            int oldPosition = mCurrent;
            mCurrent = anchorPosition;
            if (oldPosition != anchorPosition) {
                onSelectChanged(oldPosition, anchorPosition);
            }
        }
    }

    private void fillHorizontal(int anchorPosition, int itemCount, Rect containerRect, int layoutDirection, RecyclerView.Recycler recycler) {
        if (itemCount <= 0) {
            return;
        }
        int i = anchorPosition;
        while (containerRect.width() > itemSpace) {
            if (layoutDirection < 0) {
                i--;
                if (i < 0) {
                    i = i + itemCount;
                }
            } else if (layoutDirection > 0) {
                i++;
                if (i >= itemCount) {
                    i = i - itemCount;
                }
            } else {
                break;
            }
            View itemView = recycler.getViewForPosition(i);
            measureChildWithMargins(itemView, 0, 0);
            Rect childRect = layoutChild(itemView, containerRect, layoutDirection, DIRECTION_CENTER);
            addView(itemView);
            itemView.setZ(anchorZ - Math.abs(i - anchorPosition));
            if (layoutDirection < 0) {
                containerRect.right = childRect.left;
            } else {
                containerRect.left = childRect.right;
            }
        }
    }

    private void fillVertical(int anchorPosition, int itemCount, Rect containerRect, int layoutDirection, RecyclerView.Recycler recycler) {
        if (itemCount <= 0) {
            return;
        }
        int i = anchorPosition;
        while (containerRect.height() > itemSpace) {
            if (layoutDirection < 0) {
                i--;
                if (i < 0) {
                    i = i + itemCount;
                }
            } else if (layoutDirection > 0) {
                i++;
                if (i >= itemCount) {
                    i = i - itemCount;
                }
            }
            View itemView = recycler.getViewForPosition(i);
            measureChildWithMargins(itemView, 0, 0);
            Rect childRect = layoutChild(itemView, containerRect, DIRECTION_CENTER, layoutDirection);
            addView(itemView);
            itemView.setZ(anchorZ - Math.abs(i - anchorPosition));
            if (layoutDirection < 0) {
                containerRect.bottom = childRect.top;
            } else if (layoutDirection > 0) {
                containerRect.top = childRect.bottom;
            } else {
                break;
            }
        }
    }

    private Rect layoutChild(View view, Rect containerRect, int horizontalDirection, int verticalDirection) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int marginLeft = 0;
        int marginTop = 0;
        int marginRight = 0;
        int marginBottom = 0;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLeft = lp.leftMargin;
            marginTop = lp.topMargin;
            marginRight = lp.rightMargin;
            marginBottom = lp.bottomMargin;
        }
        int viewWidth = getDecoratedMeasuredWidth(view);
        int viewHeight = getDecoratedMeasuredHeight(view);
        int left;
        int top;
        int right;
        int bottom;
        if (horizontalDirection > 0) {
            left = containerRect.left + marginLeft + itemSpace;
            right = left + viewWidth;
        } else if (horizontalDirection < 0) {
            right = containerRect.right - marginRight - itemSpace;
            left = right - viewWidth;
        } else {
            left = (containerRect.width() - viewWidth) / 2 + containerRect.left;
            right = left + viewWidth;
        }
        if (verticalDirection > 0) {
            top = containerRect.top + marginTop + itemSpace;
            bottom = top + viewHeight;
        } else if (verticalDirection < 0) {
            bottom = containerRect.bottom - marginBottom - itemSpace;
            top = bottom - viewHeight;
        } else {
            top = (containerRect.height() - viewHeight) / 2 + containerRect.top;
            bottom = top + viewHeight;
        }
        layoutDecorated(view, left, top, right, bottom);
        return new Rect(left - marginLeft, top - marginTop, right + marginRight, bottom + marginBottom);
    }

    private void recyclerChildView(RecyclerView.Recycler recycler) {
        Rect containerRect = new Rect(0, 0, getWidth(), getHeight());
        int i = getChildCount() - 1;
        while (i >= 0) {
            View view = getChildAt(i);
            if (view != null) {
                if (view.getLeft() > containerRect.right || view.getTop() > containerRect.bottom || view.getRight() < containerRect.left || view.getBottom() < containerRect.top) {
                    removeAndRecycleView(view, recycler);
                }
            }
            i--;
        }
    }

    public interface OnChangeListener {
        void onSelectChanged(int oldPosition, int newPosition);
    }
}