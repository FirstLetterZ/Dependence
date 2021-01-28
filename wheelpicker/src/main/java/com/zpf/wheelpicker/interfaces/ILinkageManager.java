package com.zpf.wheelpicker.interfaces;

public interface ILinkageManager {
    boolean changeItemBoundary(int itemPosition, int lowerBoundary, int upperBoundary);

    boolean notifyItemDataChanged(int itemPosition, int selectItemIndex);
}
