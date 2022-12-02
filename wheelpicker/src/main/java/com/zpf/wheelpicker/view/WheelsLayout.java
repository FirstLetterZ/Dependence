package com.zpf.wheelpicker.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.Nullable;

import com.zpf.wheelpicker.R;
import com.zpf.wheelpicker.interfaces.ILinkageViewManager;
import com.zpf.wheelpicker.interfaces.IWheelDataModel;
import com.zpf.wheelpicker.model.WheelViewOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class WheelsLayout extends LinearLayout implements ILinkageViewManager {

    private final ArrayList<WheelView> wheelViews = new ArrayList<>();
    private final ArrayList<Space> spaceViews = new ArrayList<>();
    //修改以下参数后需要调用notifyOptionsChanged修改WheelView子视图中的配置
    private final WheelViewOptions options;
    protected final float density;
    //以下3个参数修改后需要调用rebuildLayout重新构建子视图
    private int[] itemWeights;
    private int itemSize = 0;//必须设置
    private int itemSpaceWidth;
    private boolean interceptParent;
    private boolean finishBuild = false;//是否已完成子视图构建
    private IWheelDataModel<?> dataManager;

    public WheelsLayout(Context context) {
        this(context, null, 0);
    }

    public WheelsLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public WheelsLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        options = new WheelViewOptions();
        density = getResources().getDisplayMetrics().density;
        options.itemStyle.textSize = 16 * density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView, 0, 0);
        options.initWithTypedArray(a);
        if (a != null) {
            a.recycle();
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WheelsLayout, 0, 0);
        if (array != null) {
            itemSpaceWidth = array.getInteger(R.styleable.WheelsLayout_itemSpaceWidth, 0);
            interceptParent = array.getBoolean(R.styleable.WheelsLayout_interceptParent, false);
            String weights = array.getString(R.styleable.WheelsLayout_itemWeights);
            if (weights != null && weights.length() > 0) {
                String[] ws = weights.split("\\|");
                if (ws.length > 0) {
                    int[] weightArr = new int[ws.length];
                    try {
                        for (int i = 0; i < ws.length; i++) {
                            weightArr[i] = Integer.parseInt(ws[i]);
                        }
                        itemWeights = weightArr;
                    } catch (Exception e) {
                        //
                    }
                }
            }
            array.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        finishBuild = true;
        rebuildLayout();
    }

    public int[] getItemWeights() {
        if (itemWeights == null || itemWeights.length == 0) {
            return new int[]{1};
        }
        return Arrays.copyOf(itemWeights, itemWeights.length);
    }

    //设置子视图宽度比例
    public void setItemWeights(int[] itemWeights) {
        this.itemWeights = itemWeights;
    }

    public int getItemSize() {
        return wheelViews.size();
    }

    //设置子视图数量
    public void setItemSize(int itemSize) {
        this.itemSize = Math.max(itemSize, 0);
    }

    public int getItemSpaceWidth() {
        return itemSpaceWidth;
    }

    public void setItemSpaceWidth(int itemSpaceWidth) {
        this.itemSpaceWidth = itemSpaceWidth;
    }

    public WheelViewOptions getOptions() {
        return options;
    }

    public WheelView getItemView(int position) {
        if (position >= 0 && position < wheelViews.size()) {
            return wheelViews.get(position);
        }
        return null;
    }

    public void notifyOptionsChanged() {
        for (WheelView itemView : wheelViews) {
            itemView.getViewOptions().initWithOptions(options);
            itemView.invalidate();
        }
    }

    public void setDataManager(IWheelDataModel<?> dataManager) {
        this.dataManager = dataManager;
        if (dataManager != null) {
            dataManager.setLinkageManager(this);
            int listSize = dataManager.getSize();
            if (itemSize != listSize) {
                setItemSize(listSize);
                rebuildLayout();
            } else {
                refreshViewItem();
            }
        }
    }

    //重构所有子视图
    public void rebuildLayout() {
        if (!finishBuild) {
            return;
        }
        finishBuild = false;
        final int count = itemSize;
        removeAllViews();
        if (count <= 0) {
            wheelViews.clear();
            spaceViews.clear();
            finishBuild = true;
            refreshViewItem();
            return;
        }
        Space itemSpace;
        WheelView itemView;
        final int spaceWidth = itemSpaceWidth;
        final int[] weights = itemWeights;
        for (int i = 0; i < count; i++) {
            if (spaceWidth > 0) {
                LayoutParams spaceLp = new LayoutParams(
                        (int) (spaceWidth * density), LayoutParams.MATCH_PARENT);
                if (i >= spaceViews.size()) {
                    itemSpace = new Space(getContext());
                    spaceViews.add(itemSpace);
                } else {
                    itemSpace = spaceViews.get(i);
                }
                addView(itemSpace, spaceLp);
            }
            LayoutParams itemLp = new LayoutParams(
                    0, LayoutParams.MATCH_PARENT);
            if (weights != null && weights.length > 0) {
                itemLp.weight = weights[i % weights.length];
            } else {
                itemLp.weight = 1;
            }
            if (i >= wheelViews.size()) {
                itemView = new WheelView(getContext());
                wheelViews.add(itemView);
            } else {
                itemView = wheelViews.get(i);
            }
            itemView.getViewOptions().initWithOptions(options);
            addView(itemView, itemLp);
        }
        if (spaceWidth > 0) {
            LayoutParams spaceLp = new LayoutParams(
                    (int) (spaceWidth * density), LayoutParams.MATCH_PARENT);
            if (count >= spaceViews.size()) {
                itemSpace = new Space(getContext());
                spaceViews.add(itemSpace);
            } else {
                itemSpace = spaceViews.get(count);
            }
            addView(itemSpace, spaceLp);
        }
        while (wheelViews.size() > count) {
            wheelViews.remove(wheelViews.size() - 1);
        }
        while (spaceViews.size() > count + 1) {
            spaceViews.remove(spaceViews.size() - 1);
        }
        finishBuild = true;
        refreshViewItem();
    }

    public void refreshViewItem() {
        if (!finishBuild || dataManager == null) {
            return;
        }
        dataManager.refreshDataList();
        for (int i = 0; i < getItemSize(); i++) {
            WheelView wheelView = getItemView(i);
            if (wheelView == null) {
                continue;
            }
            wheelView.setAdapter(dataManager.getAdapter(i));
            wheelView.setOnItemSelectedListener(dataManager.getSelectedListener(i));
            if (dataManager.overstepRollback()) {
                wheelView.setOnBoundaryChangedListener(dataManager.getBoundaryListener(i));
            } else {
                wheelView.setOnBoundaryChangedListener(null);
                wheelView.setBoundary(-1, -1);
            }
            if (dataManager.getSelectIndex(i) >= 0) {
                wheelView.setSelectedIndex(dataManager.getSelectIndex(i));
            }
        }
        if (dataManager.overstepRollback()) {
            try {
                getItemView(0).setBoundary(0, dataManager.getAdapter(0).getItemsCount() - 1);
            } catch (Exception e) {
                //
            }
        }
    }

    public Object getSelectResult() {
        if (!finishBuild || dataManager == null) {
            return null;
        }
        return dataManager.getSelectData();
    }

    public void setInterceptParent(boolean interceptParent) {
        this.interceptParent = interceptParent;
        if (!interceptParent) {
            ViewParent parent = getParent();
            if (parent != null) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
    }

    public boolean isInterceptParent() {
        return interceptParent;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (interceptParent) {
            if (getMeasuredHeight() > 0) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_UP:
                        getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void changeItemBoundary(int itemPosition, int lowerBoundary, int upperBoundary) {
        WheelView wheelView = getItemView(itemPosition);
        if (wheelView != null) {
            wheelView.setBoundary(lowerBoundary, upperBoundary);
        }
    }

    @Override
    public void notifyItemDataChanged(int itemPosition, int selectItemIndex) {
        if (itemPosition < 0) {
            //检查视图重构或者数据刷新
            setDataManager(dataManager);
            return;
        }
        WheelView wheelView = getItemView(itemPosition);
        if (wheelView != null) {
            if (selectItemIndex >= 0) {
                wheelView.setSelectedIndex(selectItemIndex, true);
            } else {
                wheelView.invalidate();
            }
        }
    }
}