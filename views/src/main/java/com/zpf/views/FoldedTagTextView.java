package com.zpf.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoldedTagTextView extends TextView {
    private List<TypeTextInfo> contentTextList;
    private String ellipsisText;
    private int ellipsisColor;
    private DrawInfoHelper drawInfoHelper = new DrawInfoHelper();
    private List<DrawInfo> drawInfoList = new ArrayList<>();
    private TypeTextInfo defTypeTextInfo;
    private boolean hasMeasured = false;
    private int lastCalculateHeight = -1;
    private float upX = -1f;
    private float upY = -1f;
    private OnClickListener defClickListener;
    private TypeClickListener typeClickListener;
    private OnClickListener clickDispatcher = new OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean handled = false;
            for (DrawInfo info : drawInfoList) {
                if (info.left <= upX && info.right >= upX && info.top <= upY && info.bottom >= upY) {
                    if (typeClickListener != null) {
                        if (info.isEllipsis) {
                            typeClickListener.onClickEllipsis();
                            handled = true;
                        } else {
                            handled = typeClickListener.onClickTypeText(info.type, info.id);
                        }
                    }
                    break;
                }
            }
            if (!handled && defClickListener != null) {
                defClickListener.onClick(v);
            }
        }
    };

    public FoldedTagTextView(Context context) {
        super(context);
        initConfig(null);
    }

    public FoldedTagTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfig(context.obtainStyledAttributes(attrs, R.styleable.FoldedTagTextView));
    }

    public FoldedTagTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context.obtainStyledAttributes(attrs, R.styleable.FoldedTagTextView));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FoldedTagTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initConfig(context.obtainStyledAttributes(attrs, R.styleable.FoldedTagTextView));
    }

    private void initConfig(@Nullable TypedArray typedArray) {
        super.setText("", BufferType.NORMAL);
        if (typedArray != null) {
            ellipsisColor = typedArray.getColor(R.styleable.FoldedTagTextView_ellipsis_color, Color.BLACK);
            ellipsisText = typedArray.getString(R.styleable.FoldedTagTextView_ellipsis_text);
            typedArray.recycle();
        } else {
            ellipsisColor = getTextColors().getDefaultColor();
        }
        if (ellipsisText == null) {
            ellipsisText = "…全文";
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!hasMeasured || lastCalculateHeight < 0) {
            lastCalculateHeight = calculateHeight(MeasureSpec.getSize(widthMeasureSpec));
        }
        hasMeasured = true;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(lastCalculateHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {
            for (DrawInfo info : drawInfoList) {
                if (info.drawText != null && info.drawText.length() > 0) {
                    getPaint().setColor(info.color);
                    canvas.drawText(info.drawText, info.drawX, info.drawY, getPaint());
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            upX = event.getX();
            upY = event.getY();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        defClickListener = l;
        checkClickEnable();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (contentTextList == null) {
            contentTextList = new ArrayList<>();
        }
        if (contentTextList.size() != 1 || checkUnequals(text, contentTextList.get(0).content)) {
            contentTextList.clear();
            if (defTypeTextInfo == null) {
                defTypeTextInfo = new TypeTextInfo();
            }
            defTypeTextInfo.type = 0;
            defTypeTextInfo.id = 0;
            defTypeTextInfo.color = getTextColors().getDefaultColor();
            if (text == null) {
                defTypeTextInfo.content = null;
            } else {
                defTypeTextInfo.content = text.toString();
            }
            if (defTypeTextInfo.content != null && defTypeTextInfo.content.length() > 0) {
                contentTextList.add(defTypeTextInfo);
            }
            checkMeasure();
        }
    }

    public void setEllipsisText(String text) {
        if (checkUnequals(text, ellipsisText)) {
            ellipsisText = text;
            checkMeasure();
        }
    }

    public void setEllipsisColor(int color) {
        ellipsisColor = color;
    }

    public void addTypeText(TypeTextInfo text) {
        addTypeText(text, -1);
    }

    public void addTypeText(TypeTextInfo text, int index) {
        if (contentTextList == null) {
            contentTextList = new ArrayList<>();
            contentTextList.add(text);
        } else if (index >= contentTextList.size() || index < 0) {
            contentTextList.add(text);
        } else {
            contentTextList.add(index, text);
        }
        checkMeasure();
    }

    public void setTypeTextArray(List<TypeTextInfo> textList) {
        contentTextList = textList;
        checkMeasure();
    }

    public void setTypeClickListener(TypeClickListener typeClickListener) {
        this.typeClickListener = typeClickListener;
        checkClickEnable();
    }

    private boolean checkUnequals(CharSequence a, CharSequence b) {
        if (a == null) {
            return b != null && b.length() > 0;
        }
        if (b == null) {
            return a.length() > 0;
        }
        return false;
    }

    private void checkMeasure() {
        if (hasMeasured) {
            int newCalculateHeight = calculateHeight(getWidth());
            if (newCalculateHeight != lastCalculateHeight) {
                lastCalculateHeight = newCalculateHeight;
                requestLayout();
            } else {
                invalidate();
            }
        }
    }

    private void checkClickEnable() {
        boolean isClickable = typeClickListener != null || defClickListener != null;
        if (isClickable) {
            super.setOnClickListener(clickDispatcher);
        } else {
            super.setOnClickListener(null);
        }
    }

    private int calculateHeight(int width) {
        int realWidth = width - getPaddingStart() - getPaddingEnd();
        if (realWidth <= 0 || contentTextList.size() == 0) {
            return getPaddingTop() + getPaddingBottom();
        }
        Paint paint = getPaint();
        int currentLine = 0;
        int startIndex;
        int endIndex;
        float lastWidth;
        float measureWidth;
        float usedWidth = 0f;
        float ellipsisWidth;
        if (ellipsisText == null || ellipsisText.length() == 0) {
            ellipsisWidth = 0;
        } else {
            ellipsisWidth = paint.measureText(ellipsisText);
        }
        drawInfoList.clear();
        drawInfoHelper.reset(this, paint);
        for (TypeTextInfo textInfo : contentTextList) {
            startIndex = 0;
            endIndex = startIndex + 1;
            measureWidth = 0;
            if (textInfo.content != null && textInfo.content.length() > 0) {
                if (currentLine == 0) {
                    currentLine = 1;
                }
                while (endIndex <= textInfo.content.length()) {
                    lastWidth = measureWidth;
                    char ci = textInfo.content.charAt(endIndex - 1);
                    if ('\n' == ci) {
                        if (endIndex > startIndex) {
                            drawInfoList.add(drawInfoHelper.getDrawInfo(
                                    textInfo.content.substring(startIndex, endIndex),
                                    textInfo.type, textInfo.id, textInfo.color,
                                    usedWidth, lastWidth, currentLine, false));
                        }
                        if (currentLine == getMaxLines()) {
                            if (ellipsisWidth > 0) {
                                drawInfoList.add(drawInfoHelper.getDrawInfo(
                                        ellipsisText, -1, -1, ellipsisColor,
                                        usedWidth + lastWidth, ellipsisWidth, currentLine, true));
                            }
                            break;
                        } else {
                            startIndex = endIndex;
                            endIndex = startIndex + 1;
                            currentLine++;
                            usedWidth = 0f;
                            measureWidth = 0f;
                            continue;
                        }
                    }
                    measureWidth = paint.measureText(textInfo.content, startIndex, endIndex);
                    if (currentLine == getMaxLines()) {
                        if (measureWidth + usedWidth + ellipsisWidth > realWidth) {
                            if (endIndex > startIndex) {
                                drawInfoList.add(drawInfoHelper.getDrawInfo(
                                        textInfo.content.substring(startIndex, endIndex - 1),
                                        textInfo.type, textInfo.id, textInfo.color,
                                        usedWidth, lastWidth, currentLine, false));

                            }
                            if (ellipsisWidth > 0) {
                                drawInfoList.add(drawInfoHelper.getDrawInfo(
                                        ellipsisText, -1, -1, ellipsisColor,
                                        usedWidth + lastWidth, ellipsisWidth, currentLine, true));
                            }
                            break;
                        } else if (endIndex == textInfo.content.length()) {
                            drawInfoList.add(drawInfoHelper.getDrawInfo(
                                    textInfo.content.substring(startIndex, endIndex),
                                    textInfo.type, textInfo.id, textInfo.color, usedWidth, lastWidth, currentLine, false));
                            usedWidth = usedWidth + measureWidth;
                            break;
                        } else {
                            endIndex++;
                        }
                    } else {
                        if (measureWidth + usedWidth > realWidth) {
                            if (endIndex > startIndex) {
                                drawInfoList.add(drawInfoHelper.getDrawInfo(
                                        textInfo.content.substring(startIndex, endIndex - 1),
                                        textInfo.type, textInfo.id, textInfo.color,
                                        usedWidth, lastWidth, currentLine, false));
                            }
                            currentLine++;
                            startIndex = endIndex - 1;
                            endIndex = startIndex + 1;
                            usedWidth = 0f;
                            measureWidth = 0f;
                        } else if (endIndex == textInfo.content.length()) {
                            drawInfoList.add(drawInfoHelper.getDrawInfo
                                    (textInfo.content.substring(startIndex, endIndex),
                                            textInfo.type, textInfo.id, textInfo.color,
                                            usedWidth, measureWidth, currentLine, false));
                            usedWidth = usedWidth + measureWidth;
                            break;
                        } else {
                            endIndex++;
                        }
                    }
                }
            }
        }
        Log.e("ZPF", "infoList=" + Arrays.toString(drawInfoList.toArray()) + ";currentLine=" + currentLine);
        return getPaddingTop() + getPaddingBottom() + currentLine * drawInfoHelper.lineHeight;
    }

    private static class DrawInfo {
        int type;
        int id;
        int color;
        boolean isEllipsis;
        String drawText;
        float top;
        float right;
        float left;
        float bottom;
        float drawX;
        float drawY;
    }

    private static class DrawInfoHelper {
        private int index = 0;
        private List<DrawInfo> infoList;
        private int paddingStart;
        private int paddingTop;
        private float textHeight;
        private int lineHeight;

        public DrawInfoHelper() {
            infoList = new ArrayList<>(16);
            for (int i = 0; i < 16; i++) {
                infoList.add(new DrawInfo());
            }
        }

        public void reset(TextView view, Paint paint) {
            index = 0;
            Paint.FontMetrics metrics = paint.getFontMetrics();
            textHeight = -metrics.ascent - metrics.descent;
            lineHeight = view.getLineHeight();
            paddingTop = view.getPaddingTop();
            paddingStart = view.getPaddingStart();
        }

        public DrawInfo getDrawInfo(String text, int type, int id, int color, float start, float width, int line, boolean isEllipsis) {
            while (infoList.size() <= index) {
                infoList.add(new DrawInfo());
            }
            DrawInfo info = infoList.get(index);
            info.type = type;
            info.id = id;
            info.color = color;
            info.drawText = text;
            info.drawX = paddingStart + start;
            info.drawY = paddingTop + (lineHeight + textHeight) / 2 + lineHeight * (line - 1);
            info.left = paddingStart + start;
            info.top = paddingTop + lineHeight * (line - 1);
            info.right = paddingStart + start + width;
            info.bottom = paddingTop + lineHeight * line;
            info.isEllipsis = isEllipsis;
            index++;
            return info;
        }
    }
}
