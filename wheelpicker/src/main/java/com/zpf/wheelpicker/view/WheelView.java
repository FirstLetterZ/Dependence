package com.zpf.wheelpicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.zpf.wheelpicker.R;
import com.zpf.wheelpicker.adapter.WheelAdapter;
import com.zpf.wheelpicker.interfaces.IPickerViewData;
import com.zpf.wheelpicker.interfaces.IStyledViewData;
import com.zpf.wheelpicker.listener.LoopViewGestureListener;
import com.zpf.wheelpicker.listener.OnBoundaryChangedListener;
import com.zpf.wheelpicker.listener.OnItemSelectedListener;
import com.zpf.wheelpicker.model.WheelItemStyle;
import com.zpf.wheelpicker.model.WheelViewOptions;
import com.zpf.wheelpicker.timer.InertiaTimerTask;
import com.zpf.wheelpicker.timer.SmoothScrollTimerTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 3d滚轮控件
 */
public class WheelView extends View {
    public enum ACTION { // 点击，滑翔(滑到尽头)，拖拽事件
        CLICK, FLING, DAGGLE
    }

    public enum DividerType { // 分隔线类型
        FILL, WRAP, CIRCLE
    }

    private static final String[] TIME_NUM = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09"};
    private final GestureDetector gestureDetector;
    private final WheelViewOptions viewOptions;
    private OnItemSelectedListener onItemSelectedListener;
    private OnBoundaryChangedListener onBoundaryChangedListener;
    private final ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintIndicator;

    private WheelAdapter<?> adapter;

    private int measuredHeight;// WheelView 控件高度
    private int measuredWidth;// WheelView 控件宽度
    private int maxTextWidth;
    private int maxTextHeight;
    private float itemHeight;//每行高度

    private int upperBoundary = OnBoundaryChangedListener.DEF_UPPER_INDEX;//上边界
    private int lowerBoundary = OnBoundaryChangedListener.DEF_LOWER_INDEX;//下边界
    private int lastIndex = -1;
    // 第一条线Y坐标值
    private float firstLineY;
    //第二条线Y坐标
    private float secondLineY;
    //中间label绘制的Y坐标
    private float centerY;
    //当前滚动总高度y值
    private float totalScrollY;
    //初始化默认选中项
    private int initPosition;
    //选中的Item是第几个
    private int selectedItem;
    private int preCurrentIndex;
    // 半径
    private int radius;

    private float mOffset = 0;
    private float previousY = 0;
    private long startTime = 0;

    // 修改这个值可以改变滑行速度
    private static final int VELOCITY_FLING = 5;
    private int labelReactWidth = 0;
    private int drawCenterContentStart = 0;//中间选中文字开始绘制位置
    private int drawOutContentStart = 0;//非中间文字开始绘制位置
    private static final float SCALE_CONTENT = 0.8F;//非中间文字则用此控制高度，压扁形成3d错觉
    private float CENTER_CONTENT_OFFSET;//偏移量

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewOptions = new WheelViewOptions();
        gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        gestureDetector.setIsLongpressEnabled(false);
        totalScrollY = 0;
        initPosition = -1;
        TypedArray a = null;
        if (attrs != null) {
            a = context.obtainStyledAttributes(attrs, R.styleable.WheelView, 0, 0);
        }
        float density = getResources().getDisplayMetrics().density;
        if (density < 1) {//根据密度不同进行适配
            CENTER_CONTENT_OFFSET = 2.4F;
        } else if (1 <= density && density < 2) {
            CENTER_CONTENT_OFFSET = 4.0F;
        } else if (2 <= density && density < 3) {
            CENTER_CONTENT_OFFSET = 6.0F;
        } else if (density >= 3) {
            CENTER_CONTENT_OFFSET = density * 2.5F;
        }
        viewOptions.itemStyle.textSize = 18 * density;//默认字体大小
        viewOptions.initWithTypedArray(a);
        if (a != null) {
            a.recycle();
        }
        initPaints();
    }

    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(viewOptions.itemStyle.textColorOut);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(viewOptions.itemStyle.typeface);
        paintOuterText.setTextSize(viewOptions.itemStyle.textSize);
        paintCenterText = new Paint();
        paintCenterText.setColor(viewOptions.itemStyle.textColorCenter);
        paintCenterText.setAntiAlias(true);
        paintCenterText.setTextScaleX(1.1F);
        paintCenterText.setTypeface(viewOptions.itemStyle.typeface);
        paintCenterText.setTextSize(viewOptions.itemStyle.textSize);
        paintIndicator = new Paint();
        paintIndicator.setColor(viewOptions.dividerColor);
        paintIndicator.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void reMeasure() {//重新测量
        if (adapter == null) {
            return;
        }
        measureTextWidthHeight();
        //半圆的周长 = item高度乘以item数目-1
        int halfCircumference = (int) (itemHeight * (viewOptions.getItemsVisibleCount() - 1));
        //整个圆的周长除以PI得到直径，这个直径用作控件的总高度
        measuredHeight = (int) ((halfCircumference * 2) / Math.PI);
        //求出半径
        radius = (int) (halfCircumference / Math.PI);
        //计算两条横线 和 选中项画笔的基线Y位置
        firstLineY = (measuredHeight - itemHeight) / 2.0F;
        secondLineY = (measuredHeight + itemHeight) / 2.0F;
        centerY = secondLineY - (itemHeight - maxTextHeight) / 2.0f - CENTER_CONTENT_OFFSET;
        //初始化显示的item的position
        if (initPosition == -1) {
            if (viewOptions.isLoop) {
                initPosition = (adapter.getItemsCount() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }
        preCurrentIndex = initPosition;
    }

    /**
     * 计算最大length的Text的宽高度
     */
    private void measureTextWidthHeight() {
        Rect rect = new Rect();
        WheelItemStyle itemStyle;
        for (int i = 0; i < adapter.getItemsCount(); i++) {
            Object item = adapter.getItem(i);
            String s1 = getContentText(item);
            itemStyle = getItemStyle(item, i);
            if (itemStyle.textSize > 0) {
                paintCenterText.setTextSize(itemStyle.textSize);
            } else {
                paintCenterText.setTextSize(viewOptions.itemStyle.textSize);
            }
            paintCenterText.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
        }
        paintCenterText.getTextBounds("\u661F\u671F", 0, 2, rect); // 星期的字符编码（以它为标准高度）
        maxTextHeight = rect.height() + 2;
        itemHeight = viewOptions.getLineSpacingMultiplier() * maxTextHeight;
    }

    public void smoothScroll(ACTION action) {//平滑滚动的实现
        cancelFuture();
        boolean overBorder = false;
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            overBorder = true;
            float allowance = totalScrollY % itemHeight;
            if (allowance > itemHeight / 2.0F) {
                allowance = itemHeight - allowance;
            } else {
                allowance = -allowance;
            }
            mOffset = allowance;
        }
        if (mOffset != 0) {
            //停止的时候，位置有偏移，不是全部都能正确停止到中间位置的，这里把文字位置挪回中间去
            mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset, overBorder), 0, 10, TimeUnit.MILLISECONDS);
        }
    }

    public final void scrollBy(float velocityY) {//滚动惯性的实现
        cancelFuture();
        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, VELOCITY_FLING, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    public final void setSelectedIndex(int index) {
        setSelectedIndex(index, false);
    }

    public final void setSelectedIndex(int index, boolean dataChanged) {
        if (dataChanged) {
            lastIndex = -1;
            selectedItem = -1;
        }
        int realPosition = setItemPosition(index);
        if (lastIndex != realPosition && onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(this, realPosition);
        }
        lastIndex = realPosition;
    }

    public final int getSelectedIndex() {
        if (adapter == null) {
            return 0;
        }
        if (viewOptions.isLoop && (selectedItem < 0 || selectedItem >= adapter.getItemsCount())) {
            return Math.max(0, Math.min(Math.abs(Math.abs(selectedItem) - adapter.getItemsCount()), adapter.getItemsCount() - 1));
        }
        return Math.max(0, Math.min(selectedItem, adapter.getItemsCount() - 1));
    }

    public void onItemSelected() {
        int currentItem = setItemPosition(getSelectedIndex());
        if (lastIndex != currentItem && onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(this, currentItem);
        }
        lastIndex = currentItem;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (adapter == null) {
            return;
        }
        //initPosition越界会造成preCurrentIndex的值不正确
        initPosition = Math.min(Math.max(0, initPosition), adapter.getItemsCount() - 1);

        //滚动的Y值高度除去每行Item的高度，得到滚动了多少个item，即change数
        //滚动偏移值,用于记录滚动了多少个item
        int change = (int) (totalScrollY / itemHeight);

        try {
            //滚动中实际的预选中的item(即经过了中间位置的item) ＝ 滑动前的位置 ＋ 滑动相对位置
            preCurrentIndex = initPosition + change % adapter.getItemsCount();
        } catch (ArithmeticException e) {
            Log.e("WheelView", "出错了！adapter.getItemsCount() == 0，联动数据不匹配");
        }
        if (!viewOptions.isLoop) {//不循环的情况
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > adapter.getItemsCount() - 1) {
                preCurrentIndex = adapter.getItemsCount() - 1;
            }
        } else {//循环
            if (preCurrentIndex < 0) {//举个例子：如果总数是5，preCurrentIndex ＝ －1，那么preCurrentIndex按循环来说，其实是0的上面，也就是4的位置
                preCurrentIndex = adapter.getItemsCount() + preCurrentIndex;
            }
            if (preCurrentIndex > adapter.getItemsCount() - 1) {//同理上面,自己脑补一下
                preCurrentIndex = preCurrentIndex - adapter.getItemsCount();
            }
        }
        //跟滚动流畅度有关，总滑动距离与每个item高度取余，即并不是一格格的滚动，每个item不一定滚到对应Rect里的，这个item对应格子的偏移值
        float itemHeightOffset = (totalScrollY % itemHeight);

        //绘制中间两条横线
        setDividerPaintStyle();
        if (viewOptions.dividerType == DividerType.WRAP) {//横线长度仅包裹内容
            float startX;
            float endX;

            if (TextUtils.isEmpty(viewOptions.label)) {//隐藏Label的情况
                startX = (measuredWidth - maxTextWidth) / 2f - 12;
            } else {
                startX = (measuredWidth - maxTextWidth) / 4f - 12;
            }

            if (startX <= 0) {//如果超过了WheelView的边缘
                startX = 10;
            }
            endX = measuredWidth - startX;
            canvas.drawLine(startX, firstLineY, endX, firstLineY, paintIndicator);
            canvas.drawLine(startX, secondLineY, endX, secondLineY, paintIndicator);
        } else if (viewOptions.dividerType == DividerType.CIRCLE) {
            //分割线为圆圈形状
            paintIndicator.setStyle(Paint.Style.STROKE);
            paintIndicator.setStrokeWidth(viewOptions.dividerWidth);
            float startX;
            float endX;
            if (TextUtils.isEmpty(viewOptions.label)) {//隐藏Label的情况
                startX = (measuredWidth - maxTextWidth) / 2f - 12;
            } else {
                startX = (measuredWidth - maxTextWidth) / 4f - 12;
            }
            if (startX <= 0) {//如果超过了WheelView的边缘
                startX = 10;
            }
            endX = measuredWidth - startX;
            //半径始终以宽高中最大的来算
            float radius = Math.max((endX - startX), itemHeight) / 1.8f;
            canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, radius, paintIndicator);
        } else {
            canvas.drawLine(0.0F, firstLineY, measuredWidth, firstLineY, paintIndicator);
            canvas.drawLine(0.0F, secondLineY, measuredWidth, secondLineY, paintIndicator);
        }

        // 设置数组中每个元素的值
        int counter = 0;
        while (counter < viewOptions.getItemsVisibleCount()) {
            Object showText;
            int index = preCurrentIndex - (viewOptions.getItemsVisibleCount() / 2 - counter);//索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值

            //判断是否循环，如果是循环数据源也使用相对循环的position获取对应的item值，如果不是循环则超出数据源范围使用""空白字符串填充，在界面上形成空白无数据的item项
            if (viewOptions.isLoop) {
                index = getLoopMappingIndex(index);
                showText = adapter.getItem(index);
            } else if (index < 0) {
                showText = "";
            } else if (index > adapter.getItemsCount() - 1) {
                showText = "";
            } else {
                showText = adapter.getItem(index);
            }

            canvas.save();
            // 弧长 L = itemHeight * counter - itemHeightOffset
            // 求弧度 α = L / r  (弧长/半径) [0,π]
            double radian = ((itemHeight * counter - itemHeightOffset)) / radius;
            // 弧度转换成角度(把半圆以Y轴为轴心向右转90度，使其处于第一象限及第四象限
            // angle [-90°,90°]
            float angle = (float) (90D - (radian / Math.PI) * 180D);//item第一项,从90度开始，逐渐递减到 -90度

            // 计算取值可能有细微偏差，保证负90°到90°以外的不绘制
            if (angle > 90F || angle < -90F) {
                canvas.restore();
            } else {
                //获取内容文字
                String contentText = getContentText(showText);
                final boolean hasContent = !TextUtils.isEmpty(contentText);
                if (hasContent) {
                    WheelItemStyle itemStyle = getItemStyle(showText, index);
                    //如果是label每项都显示的模式，并且item内容不为空、label 也不为空
                    if (!viewOptions.isCenterLabel && !TextUtils.isEmpty(viewOptions.label)) {
                        contentText = contentText + viewOptions.label;
                    }
                    // 根据当前角度计算出偏差系数，用以在绘制时控制文字的 水平移动 透明度 倾斜程度.
                    float offsetCoefficient = (float) Math.pow(Math.abs(angle) / 90f, 2.2);

                    reMeasureTextSize(contentText, itemStyle);
                    //计算开始绘制的位置
                    measuredCenterContentStart(contentText);
                    measuredOutContentStart(contentText);
                    float translateY = (float) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
                    //根据Math.sin(radian)来更改canvas坐标系原点，然后缩放画布，使得文字高度进行缩放，形成弧形3d视觉差
                    canvas.translate(0.0F, translateY);
                    if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                        // 条目经过第一条线
                        canvas.save();
                        canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
                        canvas.scale(1.0F, (float) Math.sin(radian) * SCALE_CONTENT);
                        setOutPaintStyle(offsetCoefficient, angle, itemStyle);
                        canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
                        canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                        setCenterPaintStyle(itemStyle);
                        canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTER_CONTENT_OFFSET, paintCenterText);
                        canvas.restore();
                    } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                        // 条目经过第二条线
                        canvas.save();
                        canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
                        canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                        setCenterPaintStyle(itemStyle);
                        canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTER_CONTENT_OFFSET, paintCenterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
                        canvas.scale(1.0F, (float) Math.sin(radian) * SCALE_CONTENT);
                        setOutPaintStyle(offsetCoefficient, angle, itemStyle);
                        canvas.drawText(contentText, drawOutContentStart, maxTextHeight, paintOuterText);
                        canvas.restore();
                    } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                        //让文字居中
                        float Y = maxTextHeight - CENTER_CONTENT_OFFSET;//因为圆弧角换算的向下取值，导致角度稍微有点偏差，加上画笔的基线会偏上，因此需要偏移量修正一下
                        setCenterPaintStyle(itemStyle);
                        canvas.drawText(contentText, drawCenterContentStart, Y, paintCenterText);
                        //设置选中项
                        selectedItem = preCurrentIndex - (viewOptions.getItemsVisibleCount() / 2 - counter);
                    } else {
                        // 其他条目
                        canvas.save();
                        canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                        canvas.scale(1.0F, (float) Math.sin(radian) * SCALE_CONTENT);
                        setOutPaintStyle(offsetCoefficient, angle, itemStyle);
                        // 控制文字水平偏移距离
                        canvas.drawText(contentText, drawOutContentStart + viewOptions.textXOffset * offsetCoefficient, maxTextHeight, paintOuterText);
                        canvas.restore();
                    }
                }
                canvas.restore();
            }
            counter++;
        }
        //只显示选中项Label文字的模式，并且Label文字不为空,可绘制区域宽度大于0，则进行绘制
        if (!TextUtils.isEmpty(viewOptions.label) && viewOptions.isCenterLabel && labelReactWidth > 0) {
            //绘制文字，靠右并留出空隙，使用上次绘制中间label的样式
            float drawLabelStart;
            float labelWidth = paintCenterText.measureText(viewOptions.label);
            if (viewOptions.labelGravity == Gravity.CENTER) {
                drawLabelStart = measuredWidth - (labelReactWidth + labelWidth) * 0.5f;
            } else if (viewOptions.labelGravity == Gravity.LEFT) {
                drawLabelStart = measuredWidth - labelReactWidth + CENTER_CONTENT_OFFSET;
            } else {
                drawLabelStart = measuredWidth - labelWidth - CENTER_CONTENT_OFFSET;
            }
            canvas.drawText(viewOptions.label, drawLabelStart, centerY, paintCenterText);
        }
    }

    private void setDividerPaintStyle() {
        paintIndicator.setStrokeWidth(viewOptions.dividerWidth);
        paintIndicator.setColor(viewOptions.dividerColor);
    }

    //设置文字倾斜角度，透明度
    private void setOutPaintStyle(float offsetCoefficient, float angle, WheelItemStyle itemStyle) {
        // 控制文字倾斜角度
        float DEFAULT_TEXT_TARGET_SKEW_X = 0.5f;
        int multiplier = 0;
        if (viewOptions.textXOffset > 0) {
            multiplier = 1;
        } else if (viewOptions.textXOffset < 0) {
            multiplier = -1;
        }
        paintOuterText.setTextSkewX(multiplier * (angle > 0 ? -1 : 1) * DEFAULT_TEXT_TARGET_SKEW_X * offsetCoefficient);
        // 控制透明度
        int alpha = viewOptions.isAlphaGradient ? (int) ((90F - Math.abs(angle)) / 90f * 255) : 255;
        paintOuterText.setAlpha(alpha);
        if (paintOuterText.getTypeface() != itemStyle.typeface) {
            paintOuterText.setTypeface(itemStyle.typeface);
        }
        paintOuterText.setColor(itemStyle.textColorOut);
    }

    private void setCenterPaintStyle(WheelItemStyle itemStyle) {
        if (paintCenterText.getTypeface() != itemStyle.typeface) {
            paintCenterText.setTypeface(itemStyle.typeface);
        }
        paintCenterText.setColor(itemStyle.textColorCenter);
    }

    private void reMeasureTextSize(String contentText, WheelItemStyle itemStyle) {
        float size = itemStyle.textSize;
        if (size <= 0) {
            size = viewOptions.itemStyle.textSize;
        }
        paintCenterText.setTextSize(size);
        Rect rect = new Rect();
        paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
        int width = rect.width();
        while (width > measuredWidth) {
            size--;
            //设置2条横线中间的文字大小
            paintCenterText.setTextSize(size);
            paintCenterText.getTextBounds(contentText, 0, contentText.length(), rect);
            width = rect.width();
        }
        //设置2条横线外面的文字大小
        paintOuterText.setTextSize(size);
    }

    //递归计算出对应的index
    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            index = index + adapter.getItemsCount();
            index = getLoopMappingIndex(index);
        } else if (index > adapter.getItemsCount() - 1) {
            index = index - adapter.getItemsCount();
            index = getLoopMappingIndex(index);
        }
        return index;
    }

    /**
     * 获取所显示的数据源
     *
     * @param item data resource
     * @return 对应显示的字符串
     */
    private String getContentText(Object item) {
        if (item == null) {
            return "";
        } else if (item instanceof IPickerViewData) {
            return ((IPickerViewData) item).getPickerViewText();
        } else if (item instanceof Integer) {
            //如果为整形则最少保留两位数.
            return getFixNum((int) item);
        }
        return item.toString();
    }

    private int setItemPosition(int currentItem) {
        if (currentItem > upperBoundary) {
            currentItem = upperBoundary;
        } else if (currentItem < lowerBoundary) {
            currentItem = lowerBoundary;
        }
        boolean currentChanged = this.selectedItem != currentItem;
        this.selectedItem = currentItem;
        this.initPosition = currentItem;
        totalScrollY = 0;//回归顶部，不然重设setCurrentItem的话位置会偏移的，就会显示出不对位置的数据
        if (currentChanged) {
            invalidate();
        }
        return getSelectedIndex();
    }

    private WheelItemStyle getItemStyle(Object item, int index) {
        if (item instanceof IStyledViewData) {
            return WheelItemStyle.mergeStyle(((IStyledViewData) item).getItemStyle(), viewOptions.itemStyle);
        } else if (index < lowerBoundary || index > upperBoundary) {
            return WheelItemStyle.mergeStyle(WheelItemStyle.defErrorStyle, viewOptions.itemStyle);
        } else {
            return viewOptions.itemStyle;
        }
    }

    private String getFixNum(int timeNum) {
        return timeNum >= 0 && timeNum < 10 ? TIME_NUM[timeNum] : String.valueOf(timeNum);
    }

    private void measuredCenterContentStart(String content) {
        Rect rect = new Rect();
        paintCenterText.getTextBounds(content, 0, content.length(), rect);
        switch (viewOptions.itemGravity) {
            case Gravity.CENTER://显示内容居中
                if (viewOptions.isOptions || viewOptions.label == null || viewOptions.label.equals("")
                        || !viewOptions.isCenterLabel) {
                    drawCenterContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                    labelReactWidth = (measuredWidth - maxTextWidth) / 2;
                } else {//只显示中间label时，时间选择器内容偏左一点，留出空间绘制单位标签
                    drawCenterContentStart = (int) ((measuredWidth - rect.width()) * 0.25);
                    labelReactWidth = (int) ((measuredWidth - maxTextWidth) * 0.75);
                }
                break;
            case Gravity.LEFT:
                drawCenterContentStart = 0;
                labelReactWidth = measuredWidth - maxTextWidth;
                break;
            case Gravity.RIGHT://添加偏移量
                drawCenterContentStart = measuredWidth - rect.width() - (int) CENTER_CONTENT_OFFSET;
                labelReactWidth = 0;
                break;
        }
    }

    private void measuredOutContentStart(String content) {
        Rect rect = new Rect();
        paintOuterText.getTextBounds(content, 0, content.length(), rect);
        switch (viewOptions.itemGravity) {
            case Gravity.CENTER:
                if (viewOptions.isOptions || viewOptions.label == null || viewOptions.label.equals("")
                        || !viewOptions.isCenterLabel) {
                    drawOutContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                } else {//只显示中间label时，时间选择器内容偏左一点，留出空间绘制单位标签
                    drawOutContentStart = (int) ((measuredWidth - rect.width()) * 0.25);
                }
                break;
            case Gravity.LEFT:
                drawOutContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawOutContentStart = measuredWidth - rect.width() - (int) CENTER_CONTENT_OFFSET;
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);  //控件宽度
        reMeasure();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = gestureDetector.onTouchEvent(event);
        boolean isIgnore = false;//超过边界滑动时，不再绘制UI。

        float top = -initPosition * itemHeight;
        float bottom = (adapter.getItemsCount() - 1 - initPosition) * itemHeight;
        float overScroll = itemHeight * 0.48f;//允许超过边界
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                if (dy == 0) {
                    break;
                }
                previousY = event.getRawY();
                totalScrollY = totalScrollY + dy;
                // normal mode。
                if (!viewOptions.isLoop) {
                    //快滑动到边界了，设置已滑动到边界的标志
                    if (dy < 0) {
                        if (totalScrollY < top - overScroll) {
                            isIgnore = !(totalScrollY - dy > top - overScroll);
                            totalScrollY = top - overScroll;
                        }
                    } else if (dy > 0) {
                        if (totalScrollY > bottom + overScroll) {
                            isIgnore = !(totalScrollY - dy < bottom + overScroll);
                            totalScrollY = bottom + overScroll;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                if (!eventConsumed) {//未消费掉事件
                    /**
                     *@describe <关于弧长的计算>
                     *
                     * 弧长公式： L = α*R
                     * 反余弦公式：arccos(cosα) = α
                     * 由于之前是有顺时针偏移90度，
                     * 所以实际弧度范围α2的值 ：α2 = π/2-α    （α=[0,π] α2 = [-π/2,π/2]）
                     * 根据正弦余弦转换公式 cosα = sin(π/2-α)
                     * 代入，得： cosα = sin(π/2-α) = sinα2 = (R - y) / R
                     * 所以弧长 L = arccos(cosα)*R = arccos((R - y) / R)*R
                     */
                    float y = event.getY();
                    double L = Math.acos((radius - y) / radius) * radius;
                    //item0 有一半是在不可见区域，所以需要加上 itemHeight / 2
                    int circlePosition = (int) ((L + itemHeight / 2) / itemHeight);
                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
                    //已滑动的弧长值
                    mOffset = (int) ((circlePosition - viewOptions.getItemsVisibleCount() / 2) * itemHeight - extraOffset);
                    if ((System.currentTimeMillis() - startTime) > 120) {
                        // 处理拖拽事件
                        smoothScroll(ACTION.DAGGLE);
                    } else {
                        // 处理条目点击事件
                        smoothScroll(ACTION.CLICK);
                    }
                }
                break;
        }
        if (!isIgnore && event.getAction() != MotionEvent.ACTION_DOWN) {
            invalidate();
        }
        return true;
    }

    public void setOnBoundaryChangedListener(OnBoundaryChangedListener onBoundaryChangedListener) {
        this.onBoundaryChangedListener = onBoundaryChangedListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.onItemSelectedListener = itemSelectedListener;
    }

    public final void setAdapter(WheelAdapter<?> adapter) {
        this.adapter = adapter;
        reMeasure();
        invalidate();
    }

    public final WheelAdapter<?> getAdapter() {
        return adapter;
    }

    public int getItemsCount() {
        return adapter != null ? adapter.getItemsCount() : 0;
    }

    public float getTotalScrollY() {
        return totalScrollY;
    }

    public void setTotalScrollY(float totalScrollY) {
        this.totalScrollY = totalScrollY;
    }

    //设置上下限，如果选中条目发生变化则返回true
    public boolean setBoundary(int lower, int upper) {
        int newUpperBoundary;
        if (upper < 0) {
            newUpperBoundary = OnBoundaryChangedListener.DEF_UPPER_INDEX;
        } else {
            newUpperBoundary = upper;
        }
        int newLowerBoundary = Math.max(lower, OnBoundaryChangedListener.DEF_LOWER_INDEX);
        if (newUpperBoundary != this.upperBoundary || newLowerBoundary != this.lowerBoundary) {
            this.upperBoundary = newUpperBoundary;
            this.lowerBoundary = newLowerBoundary;
            int cIndex = getSelectedIndex();
            if (cIndex > upperBoundary) {
                cIndex = upperBoundary;
                setSelectedIndex(cIndex);
            } else if (cIndex < lowerBoundary) {
                cIndex = lowerBoundary;
                setSelectedIndex(cIndex);
            }
            lastIndex = cIndex;
            if (onBoundaryChangedListener != null) {
                onBoundaryChangedListener.onChanged(this, cIndex, newLowerBoundary, newUpperBoundary);
            }
            return true;
        }
        return false;
    }

    public int getUpperBoundary() {
        return upperBoundary;
    }

    public int getLowerBoundary() {
        return lowerBoundary;
    }

    public float getItemHeight() {
        return itemHeight;
    }

    public int getInitPosition() {
        return initPosition;
    }

    public WheelViewOptions getViewOptions() {
        return viewOptions;
    }

}