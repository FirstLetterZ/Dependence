package com.zpf.views;import android.graphics.Rect;import android.view.View;public class ViewRatioSurveyor {    public float aspectRatio = 0f;    public int contentWidth = 0;    public int contentHeight = 0;    public Rect measure(int widthMeasureSpec, int heightMeasureSpec) {        if (aspectRatio == 0) {            return null;        }        float aspectRatio = this.aspectRatio;        float targetRatio = -1;        float measureRatio = -1;        int sourceWidth = contentWidth;        int sourceHeight = contentHeight;        int measureHeight = View.MeasureSpec.getSize(heightMeasureSpec);        int measureWidth = View.MeasureSpec.getSize(widthMeasureSpec);        if (aspectRatio > 0) {            targetRatio = aspectRatio;            if (measureHeight > 0) {                measureRatio = measureWidth * 1f / measureHeight;            }        } else if (aspectRatio < 0 && (sourceWidth > 0 && sourceHeight > 0)) {            targetRatio = sourceWidth * 1f / sourceHeight;            if (measureHeight > 0) {                measureRatio = measureWidth * 1f / measureHeight;            }        }        if (targetRatio > 0 && Math.abs(measureRatio - targetRatio) > 0.01f) {            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);            if (widthMode == View.MeasureSpec.EXACTLY) {                int targetHeight = (int) (measureWidth / targetRatio);                return new Rect(0, 0, measureWidth, targetHeight);            } else if (heightMode == View.MeasureSpec.EXACTLY) {                int targetWidth = (int) (measureHeight * targetRatio);                return new Rect(0, 0, targetWidth, measureHeight);            } else {                if (measureWidth >= sourceWidth && measureHeight >= sourceHeight) {                    return new Rect(0, 0, sourceWidth, sourceHeight);                } else if (measureWidth < sourceWidth) {                    int targetHeight = (int) (measureWidth / targetRatio);                    return new Rect(0, 0, measureWidth, targetHeight);                } else {                    int targetWidth = (int) (measureHeight * targetRatio);                    return new Rect(0, 0, targetWidth, measureHeight);                }            }        } else {            return null;        }    }}