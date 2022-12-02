package com.zpf.wheelpicker.interfaces;

public interface ILinkageViewManager {
    void changeItemBoundary(int column, int lowerBoundaryIndex, int upperBoundaryIndex);

    void notifyItemDataChanged(int column, int selectItemIndex);
}
