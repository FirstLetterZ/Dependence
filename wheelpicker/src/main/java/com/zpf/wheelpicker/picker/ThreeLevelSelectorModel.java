package com.zpf.wheelpicker.picker;

import com.zpf.wheelpicker.adapter.ListWheelAdapter;
import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.interfaces.ILinkageManager;
import com.zpf.wheelpicker.interfaces.IWheelDataModel;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;
import com.zpf.wheelpicker.view.WheelView;

import java.util.ArrayList;
import java.util.List;

public class ThreeLevelSelectorModel<T> implements IWheelDataModel<List<T>> {
    private List<T> initSelectInfo;
    private final List<T> firstColumn;
    private final List<List<T>> secondColumn;
    private final List<List<List<T>>> thirdColumn;
    private final int[] selectIndex;
    private ILinkageManager linkageManager;
    private final ArrayList<OnItemSelectedListener> selectedListeners;
    private ThreeLevelSelectListener<T> selectListener;
    public boolean isRestoreItem = false; //切换时，还原第一项
    private final ArrayList<ListWheelAdapter<T>> adapterList = new ArrayList<>();

    public ThreeLevelSelectorModel(final List<T> firstColumn, final List<List<T>> secondColumn,
                                   final List<List<List<T>>> thirdColumn) {
        this.firstColumn = firstColumn;
        this.secondColumn = secondColumn;
        this.thirdColumn = thirdColumn;
        selectIndex = new int[]{0, 0, 0};
        selectedListeners = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final int position = i;
            selectedListeners.add(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelView view, int itemIndex) {
                    selectIndex[position] = itemIndex;
                    if (linkageManager != null) {
                        int index = -1;
                        if (isRestoreItem) {
                            index = 0;
                        }
                        if (position == 0) {
                            adapterList.get(1).changeDataSource(secondColumn.get(selectIndex[0]));
                            adapterList.get(2).changeDataSource(thirdColumn.get(selectIndex[0]).get(selectIndex[1]));
                            linkageManager.notifyItemDataChanged(1, index);
                            linkageManager.notifyItemDataChanged(2, index);
                        } else if (position == 1) {
                            adapterList.get(2).changeDataSource(thirdColumn.get(selectIndex[0]).get(selectIndex[1]));
                            linkageManager.notifyItemDataChanged(2, index);
                        }
                    }
                    if (selectListener != null) {
                        T a = firstColumn.get(selectIndex[0]);
                        T b = secondColumn.get(selectIndex[0]).get(selectIndex[1]);
                        T c = thirdColumn.get(selectIndex[0]).get(selectIndex[1]).get(selectIndex[2]);
                        selectListener.onSelectChanged(a, b, c);
                    }
                }
            });
        }
    }


    @Override
    public WheelAdapter<?> getAdapter(int position) {
        if (position < 0 || position > 2) {
            return null;
        }
        if (adapterList.size() == 0) {
            adapterList.add(new ListWheelAdapter<>(firstColumn));
            adapterList.add(new ListWheelAdapter<>(secondColumn.get(selectIndex[0])));
            adapterList.add(new ListWheelAdapter<>(thirdColumn.get(selectIndex[0]).get(selectIndex[1])));
        }
        return adapterList.get(position);
    }

    @Override
    public int getListSize() {
        return 3;
    }

    @Override
    public OnItemSelectedListener getSelectedListener(int position) {
        if (position > 2 || position < 0) {
            return null;
        }
        return selectedListeners.get(position);
    }

    @Override
    public OnBoundaryChangedListener getBoundaryListener(int position) {
        return null;
    }

    @Override
    public boolean hasBoundary() {
        return false;
    }

    @Override
    public int getSelectIndex(int position) {
        if (position >= 0 && position < 3) {
            return selectIndex[position];
        }
        return 0;
    }

    @Override
    public void setBoundary(List<T> start, List<T> end) {
//
    }

    @Override
    public void setInitData(List<T> data) {
        initSelectInfo = data;
    }

    @Override
    public List<T> getSelectData() {
        List<T> result = new ArrayList<>();
        result.add(firstColumn.get(selectIndex[0]));
        result.add(secondColumn.get(selectIndex[0]).get(selectIndex[1]));
        result.add(thirdColumn.get(selectIndex[0]).get(selectIndex[1]).get(selectIndex[2]));
        return result;
    }

    @Override
    public void refreshDataList() {
        if (initSelectInfo != null && initSelectInfo.size() == 3) {
            int index = 0;
            List<?> target = firstColumn;
            while (index < 3) {
                if (index == 0) {
                    target = firstColumn;
                } else if (index == 1) {
                    target = secondColumn.get(selectIndex[0]);
                } else if (index == 2) {
                    target = thirdColumn.get(selectIndex[0]).get(selectIndex[1]);
                }
                for (int i = 0; i < target.size(); i++) {
                    if (initSelectInfo.get(0).equals(target.get(i))) {
                        selectIndex[index] = i;
                        break;
                    }
                }
                index++;
            }
        }
        if (adapterList.size() == 0) {
            adapterList.add(new ListWheelAdapter<>(firstColumn));
            adapterList.add(new ListWheelAdapter<>(secondColumn.get(selectIndex[0])));
            adapterList.add(new ListWheelAdapter<>(thirdColumn.get(selectIndex[0]).get(selectIndex[1])));
        } else {
            adapterList.get(1).changeDataSource(secondColumn.get(selectIndex[0]));
            adapterList.get(2).changeDataSource(thirdColumn.get(selectIndex[0]).get(selectIndex[1]));
        }
    }

    @Override
    public void setLinkageManager(ILinkageManager manager) {
        this.linkageManager = manager;
    }

    public boolean isRestoreItem() {
        return isRestoreItem;
    }

    public void setRestoreItem(boolean restoreItem) {
        isRestoreItem = restoreItem;
    }

    public void setSelectListener(ThreeLevelSelectListener<T> selectListener) {
        this.selectListener = selectListener;
    }
}
