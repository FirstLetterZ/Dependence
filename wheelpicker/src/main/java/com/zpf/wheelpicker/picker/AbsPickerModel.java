package com.zpf.wheelpicker.picker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.interfaces.ILinkageViewManager;
import com.zpf.wheelpicker.interfaces.IWheelDataModel;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnColumnChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;
import com.zpf.wheelpicker.model.WheelColumnHolder;
import com.zpf.wheelpicker.model.WheelItemStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsPickerModel<T, R> implements IWheelDataModel<R> {
    protected final ArrayList<WheelColumnHolder<T>> holderList = new ArrayList<>();
    protected R initSelectInfo;
    protected R boundaryLower;
    protected R boundaryUpper;
    protected ILinkageViewManager linkageManager;
    protected OnColumnChangedListener<T> changedListener;
    protected boolean isRestoreItem = false; //切换时，还原第一项
    private int size = 0;
    protected volatile boolean ignoreColumnSelectChanged = false;
    protected boolean overstepRollback = false;
    protected boolean linkage = false;

    protected void resetSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0!");
        }
        this.size = size;
        holderList.clear();
        for (int i = 0; i < size; i++) {
            final WheelColumnHolder<T> holder = new WheelColumnHolder<>(i);
            holder.setColumnChangedListener((column, oldValue, newValue) -> {
                if (!ignoreColumnSelectChanged) {
                    if (changedListener != null) {
                        changedListener.onColumnDataChanged(column, oldValue, newValue);
                    }
                    if (linkage && column < size - 1) {
                        checkNextColumn(column + 1, holderList.get(column + 1), holder);
                    }
                }
            });
            holderList.add(holder);
        }
    }

    @Override
    public void refreshDataList() {
        ignoreColumnSelectChanged = true;
        WheelColumnHolder<T> lastHolder = null;
        for (int i = 0; i < holderList.size(); i++) {
            WheelColumnHolder<T> holder = holderList.get(i);
            holder.rest();
            updateColumnData(i, holder, lastHolder);
            updateColumnBoundary(i, holder, lastHolder);
            updateSelect(i, holder, getColumnInitValue(i));
            lastHolder = holder;
        }
        ignoreColumnSelectChanged = false;
    }

    protected void checkNextColumn(int nextColumn, @NonNull WheelColumnHolder<T> nextHolder, @NonNull WheelColumnHolder<T> currentHolder) {
        updateColumnData(nextColumn, nextHolder, currentHolder);
        updateColumnBoundary(nextColumn, nextHolder, currentHolder);
        if (isRestoreItem) {
            updateSelect(nextColumn, nextHolder, null);
        } else {
            updateSelect(nextColumn, nextHolder, nextHolder.selectData);
        }
    }

    protected abstract void updateColumnData(int column, @NonNull WheelColumnHolder<T> holder, @Nullable WheelColumnHolder<T> lastHolder);

    protected void updateSelect(int column, @NonNull WheelColumnHolder<T> holder, @Nullable T selectValue) {
        int selectIndex = holder.updateSelect(selectValue, isRestoreItem);
        ILinkageViewManager manager = getLinkageViewManager(column);
        if (manager == null) {
            return;
        }
        manager.notifyItemDataChanged(column, selectIndex);
    }

    protected void updateColumnBoundary(int column, @NonNull WheelColumnHolder<T> holder, @Nullable WheelColumnHolder<T> lastHolder) {
        int lowerIndex;
        int upperIndex;
        T lowerValue = getColumnBoundaryValue(column, boundaryLower);
        T upperValue = getColumnBoundaryValue(column, boundaryUpper);
        if (lastHolder == null) {
            lowerIndex = holder.adapter.indexOf(lowerValue);
            upperIndex = holder.adapter.indexOf(upperValue);
        } else {
            if (lastHolder.isOnLowerBoundary()) {
                lowerIndex = holder.adapter.indexOf(lowerValue);
            } else {
                lowerIndex = -1;
            }
            if (lastHolder.isOnUpperBoundary()) {
                upperIndex = holder.adapter.indexOf(upperValue);
            } else {
                upperIndex = -1;
            }
        }
        if (upperIndex < 0) {
            upperIndex = OnBoundaryChangedListener.DEF_UPPER_INDEX;
        }
        if (lowerIndex < 0) {
            lowerIndex = OnBoundaryChangedListener.DEF_LOWER_INDEX;
        }
        holder.updateColumnBoundary(lowerIndex, upperIndex, !overstepRollback);
        ILinkageViewManager manager = getLinkageViewManager(column);
        if (manager == null) {
            return;
        }
        if (overstepRollback) {
            manager.changeItemBoundary(column, lowerIndex, upperIndex);
        } else {
            manager.changeItemBoundary(column, OnBoundaryChangedListener.DEF_LOWER_INDEX, OnBoundaryChangedListener.DEF_UPPER_INDEX);
        }
    }

    @Nullable
    protected T getColumnBoundaryValue(int column, R boundary) {
        if (boundary instanceof List) {
            List<?> list = (List<?>) boundary;
            if (list.size() > column) {
                try {
                    return (T) list.get(column);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Nullable
    protected T getColumnInitValue(int column) {
        if (initSelectInfo instanceof List) {
            List<?> list = (List<?>) initSelectInfo;
            if (list.size() > column) {
                try {
                    return (T) list.get(column);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected List<T> getSelectList() {
        ArrayList<T> result = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            result.add(holderList.get(j).selectData);
        }
        return result;
    }

    protected ILinkageViewManager getLinkageViewManager(int column) {
        return linkageManager;
    }

    @Override
    public WheelAdapter<T> getAdapter(int column) {
        column = getRealPosition(column);
        if (column < 0 || column > size - 1) {
            return null;
        }
        return holderList.get(column).adapter;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public OnItemSelectedListener getSelectedListener(int column) {
        column = getRealPosition(column);
        if (column > size - 1 || column < 0) {
            return null;
        }
        return holderList.get(column).itemSelectedListener;
    }

    @Override
    public OnBoundaryChangedListener getBoundaryListener(int column) {
        return holderList.get(column).boundaryChangedListener;
    }

    public void setOverstepRollback(boolean overstepRollback) {
        this.overstepRollback = overstepRollback;
    }

    @Override
    public boolean overstepRollback() {
        return overstepRollback;
    }

    @Override
    public int getSelectIndex(int column) {
        column = getRealPosition(column);
        if (column > size - 1 || column < 0) {
            return 0;
        }
        return holderList.get(column).selectPosition;
    }

    @Override
    public void setBoundary(R start, R end) {
        boundaryLower = start;
        boundaryUpper = end;
    }

    @Override
    public void setInitData(R data) {
        initSelectInfo = data;
    }

    @Override
    public void setLinkageManager(ILinkageViewManager manager) {
        this.linkageManager = manager;
    }

    protected int getRealPosition(int position) {
        return position;
    }

    public boolean isRestoreItem() {
        return isRestoreItem;
    }

    public void setRestoreItem(boolean restoreItem) {
        isRestoreItem = restoreItem;
    }

    public void setOnChangedListener(OnColumnChangedListener<T> changedListener) {
        this.changedListener = changedListener;
    }

    public boolean isLinkage() {
        return linkage;
    }

    public void setLinkage(boolean linkage) {
        this.linkage = linkage;
    }

    public void setOverstepStyle(@Nullable WheelItemStyle style) {
        for (WheelColumnHolder<T> holder : holderList) {
            holder.errorStyle = style;
        }
    }

}