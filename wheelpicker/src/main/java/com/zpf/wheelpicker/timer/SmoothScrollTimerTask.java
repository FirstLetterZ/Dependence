package com.zpf.wheelpicker.timer;

import com.zpf.wheelpicker.view.WheelView;

import java.util.TimerTask;

/**
 * 平滑滚动的实现
 *
 * @author 小嵩
 */
public final class SmoothScrollTimerTask extends TimerTask {

    private float realTotalOffset;
    private float realOffset;
    private float offset;
    private final WheelView wheelView;
    private final boolean overBorder;

    public SmoothScrollTimerTask(WheelView wheelView, float offset, boolean overBorder) {
        this.wheelView = wheelView;
        this.overBorder = overBorder;
        this.offset = offset;
        realTotalOffset = Integer.MAX_VALUE;
        realOffset = 0;
    }

    @Override
    public final void run() {
        if (realTotalOffset == Integer.MAX_VALUE) {
            realTotalOffset = offset;
        }
        //把要滚动的范围细分成10小份，按10小份单位来重绘
        realOffset = realTotalOffset * 0.1F;

        if (realOffset == 0) {
            if (realTotalOffset < 0) {
                realOffset = -1;
            } else {
                realOffset = 1;
            }
        }

        if (Math.abs(realTotalOffset) <= 1) {
            wheelView.cancelFuture();
            wheelView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wheelView.onItemSelected();
                }
            },50);
        } else {
            wheelView.setTotalScrollY(wheelView.getTotalScrollY() + realOffset);

            //这里如果不是循环模式，则点击空白位置需要回滚，不然就会出现选到－1 item的 情况
            //拖拽允许少量超出
            if (!wheelView.getViewOptions().isLoop && !overBorder) {
                float itemHeight = wheelView.getItemHeight();
                float top = -wheelView.getInitPosition() * itemHeight;
                float bottom = (wheelView.getItemsCount() - 1 - wheelView.getInitPosition()) * itemHeight;
                if (wheelView.getTotalScrollY() <= top || wheelView.getTotalScrollY() >= bottom) {
                    wheelView.setTotalScrollY(wheelView.getTotalScrollY() - realOffset);
                    wheelView.cancelFuture();
                    wheelView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wheelView.onItemSelected();
                        }
                    },50);
                    return;
                }
            }
            wheelView.postInvalidateDelayed(50L);
            realTotalOffset = realTotalOffset - realOffset;
        }
    }
}
