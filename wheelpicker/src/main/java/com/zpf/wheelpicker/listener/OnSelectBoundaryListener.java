package com.zpf.wheelpicker.listener;

import com.zpf.wheelpicker.view.WheelView;

public abstract class OnSelectBoundaryListener implements OnItemSelectedListener, OnBoundaryChangedListener {

    protected abstract void changeNextItemBoundary(int nextPosition, int lowerBoundary, int upperBoundary);

    protected abstract void OnBoundaryChanged(WheelView view, int position);

    private int currentIndex;
    private int itemState = 999;
    private final int itemPosition;
    private int lowerOnLowerBoundary;
    private int upperOnLowerBoundary;
    private int lowerOnUpperBoundary;
    private int upperOnUpperBoundary;
    private int lowerInMiddleBoundary;
    private int upperInMiddleBoundary;

    public OnSelectBoundaryListener(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public void setNextItemBoundary(int lowerOnLowerBoundary, int upperOnLowerBoundary, int lowerOnUpperBoundary,
                                    int upperOnUpperBoundary, int lowerInMiddleBoundary, int upperInMiddleBoundary) {
        this.lowerOnLowerBoundary = lowerOnLowerBoundary;
        this.upperOnLowerBoundary = upperOnLowerBoundary;
        this.lowerOnUpperBoundary = lowerOnUpperBoundary;
        this.upperOnUpperBoundary = upperOnUpperBoundary;
        this.lowerInMiddleBoundary = lowerInMiddleBoundary;
        this.upperInMiddleBoundary = upperInMiddleBoundary;
    }

    @Override
    public void onChanged(WheelView wheelView, int currentIndex, int lowerBoundary, int upperBoundary) {
        checkState(wheelView, currentIndex, lowerBoundary, upperBoundary);
        OnBoundaryChanged(wheelView, itemPosition);
    }

    @Override
    public void onItemSelected(WheelView view, int itemIndex) {
        checkState(view, itemIndex, view.getLowerBoundary(), view.getUpperBoundary());
        currentIndex = itemIndex;
    }

    private void checkState(WheelView view, int currentIndex, int lowerBoundary, int upperBoundary) {
        int newState;
        if (currentIndex >= upperBoundary) {
            newState = 1;
        } else if (currentIndex <= lowerBoundary) {
            newState = -1;
        } else {
            newState = 0;
        }
        if (newState != itemState) {
            itemState = newState;
            if (newState < 0) {
                changeNextItemBoundary(itemPosition + 1, lowerOnLowerBoundary, upperOnLowerBoundary);
            } else if (newState > 0) {
                changeNextItemBoundary(itemPosition + 1, lowerOnUpperBoundary, upperOnUpperBoundary);
            } else {
                changeNextItemBoundary(itemPosition + 1, lowerInMiddleBoundary, upperInMiddleBoundary);
            }
        }
    }

    public void resetState() {
        itemState = 999;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
