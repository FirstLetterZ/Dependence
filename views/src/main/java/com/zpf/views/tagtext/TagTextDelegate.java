package com.zpf.views.tagtext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TagTextDelegate {
    private int lineHeight;
    private int maxLines;
    private int maxHeight;
    private float paragraphSpace;
    private float fontSize;
    private String ellipsisText;

    public final TagTextStyle defStyle = new TagTextStyle();
    public final TagTextStyle ellipsisStyle = new TagTextStyle();
    private final TagTextMeasureman measureman = new TagTextMeasureman();
    private int calculateHeight = -1;
    private int showHeight = 0;
    private boolean waitCalculate = true;
    private final ArrayList<TagTextItem> contentTextList = new ArrayList<>();
    private TagTextPieceInfo ellipsisPart = new TagTextPieceInfo();
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TagTextRecycler recycler = new TagTextRecycler();
    private float upX = -1f;
    private float upY = -1f;
    private float lastY = -1f;
    private View.OnClickListener defClickListener;
    private TagItemClickListener itemlickListener;
    private View.OnClickListener clickDispatcher = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (itemlickListener == null) {
                if (defClickListener != null) {
                    defClickListener.onClick(v);
                }
                return;
            }
            if (ellipsisPart.startIndex < ellipsisPart.endIndex) {
                if (ellipsisPart.left <= upX && ellipsisPart.right >= upX && ellipsisPart.top <= upY && ellipsisPart.bottom >= upY) {
                    itemlickListener.onClickEllipsis();
                    return;
                }
            }
            boolean handled = false;
            for (TagTextItem item : contentTextList) {
                for (TagTextPieceInfo info : item.parts) {
                    if (info.left <= upX && info.right >= upX && info.top <= upY && info.bottom >= upY) {
                        handled = itemlickListener.onClickItem(item.textId);
                        if (!handled && defClickListener != null) {
                            defClickListener.onClick(v);
                        }
                        handled = true;
                        break;
                    }
                }
                if (handled) {
                    break;
                }
            }
            if (!handled && defClickListener != null) {
                defClickListener.onClick(v);
            }
        }
    };

    public void initDefConfig(float density) {
        textPaint.density = density;
        if (fontSize == 0f) {
            fontSize = 14 * density;
        }
        if (lineHeight == 0) {
            lineHeight = (int) (16 * density + 0.5f);
        }
        if (defStyle.color == 0) {
            defStyle.color = Color.DKGRAY;
        }
        if (ellipsisStyle.color == 0) {
            ellipsisStyle.color = Color.BLUE;
        }
        ellipsisStyle.bold = defStyle.bold;
        ellipsisStyle.italic = defStyle.italic;
        ellipsisStyle.underline = defStyle.underline;
        ellipsisStyle.strikeThru = defStyle.strikeThru;
        if (ellipsisText == null) {
            ellipsisText = "…全文";
        }
    }

    public View.OnClickListener getRealClickListener(View.OnClickListener l) {
        defClickListener = l;
        boolean isClickable = itemlickListener != null || defClickListener != null;
        if (isClickable) {
            return clickDispatcher;
        } else {
            return null;
        }
    }

    public View.OnClickListener getRealClickListener(TagItemClickListener l) {
        itemlickListener = l;
        boolean isClickable = itemlickListener != null || defClickListener != null;
        if (isClickable) {
            return clickDispatcher;
        } else {
            return null;
        }
    }

    public boolean handleTouchEvent(@NonNull View drawOn, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastY = event.getRawY();
            return showHeight < calculateHeight;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int dY = (int) (event.getRawY() - lastY);
            if (dY != 0) {
                drawOn.scrollBy(0, dY);
            }
            lastY = event.getRawY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            upX = event.getX();
            upY = event.getY();
        }
        return false;
    }

    public void onDraw(@NonNull View drawOn, Canvas canvas) {
        if (canvas != null) {
            int painColor;
            canvas.translate(0, drawOn.getScrollY());
            for (TagTextItem item : contentTextList) {
                painColor = item.style.color;
                if (painColor == 0) {
                    painColor = defStyle.color;
                }
                textPaint.setColor(painColor);
                if (item.parts.size() == 0) {
                    continue;
                }
                for (TagTextPieceInfo info : item.parts) {
                    if (info.shouldDraw()) {
                        canvas.drawText(item.textStr, info.startIndex, info.endIndex, info.drawX, info.drawY, textPaint);
                    } else {
                        break;
                    }
                }
            }
            if (ellipsisPart.shouldDraw()) {
                textPaint.setColor(ellipsisStyle.color);
                canvas.drawText(ellipsisText, ellipsisPart.startIndex, ellipsisPart.endIndex, ellipsisPart.drawX, ellipsisPart.drawY, textPaint);
            }
        }
    }

    public int checkBorder(int y) {
        if (y > 0) {
            y = 0;
        } else if (y < showHeight - calculateHeight) {
            y = showHeight - calculateHeight;
        }
        return y;
    }

    public int measureHeight(View drawOn, int widthMeasureSpec, int heightMeasureSpec) {
        if (waitCalculate) {
            waitCalculate = false;
            calculateHeight = calculateDrawHeight(drawOn, View.MeasureSpec.getSize(widthMeasureSpec));
        }
        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
            showHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        } else {
            showHeight = calculateHeight;
        }
        if (maxHeight > 0 && showHeight > maxHeight) {
            showHeight = maxHeight;
        }
        return View.MeasureSpec.makeMeasureSpec(showHeight, View.MeasureSpec.EXACTLY);
    }

    public TagTextStyle setContentText(String text, int id) {
        contentTextList.clear();
        TagTextItem contentItem = recycler.obtainTextItem(id, text, defStyle);
        contentTextList.add(contentItem);
        return contentItem.style;
    }

    public TagTextStyle getDefStyle() {
        return defStyle;
    }

    public TagTextStyle addTextItem(String text, int id) {
        return addTextItem(text, id, -1);
    }

    public TagTextStyle addTextItem(String text, int id, int index) {
        TagTextItem item = recycler.obtainTextItem(id, text, defStyle);
        if (index < 0 || index > contentTextList.size()) {
            contentTextList.add(item);
        } else {
            contentTextList.add(index, item);
        }
        return item.style;
    }

    public void clearContent() {
        contentTextList.clear();
    }

    public void setEllipsisText(String text) {
        if (checkUnequals(text, ellipsisText)) {
            ellipsisText = text;
        }
    }

    public TagTextStyle getEllipsisStyle() {
        return ellipsisStyle;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getLastCalculateHeight() {
        return calculateHeight;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setParagraphSpace(float paragraphSpace) {
        this.paragraphSpace = paragraphSpace;
    }

    public float getParagraphSpace() {
        return paragraphSpace;
    }

    public void checkRefresh(View drawOn) {
        if (!waitCalculate && drawOn != null) {
            int newCalculateHeight = calculateDrawHeight(drawOn, drawOn.getWidth());
            if (newCalculateHeight != calculateHeight) {
                calculateHeight = newCalculateHeight;
                drawOn.requestLayout();
            } else {
                drawOn.invalidate();
            }
        }
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

    private int calculateDrawHeight(@NonNull View drawOn, int width) {
        float realWidth = width - drawOn.getPaddingStart() - drawOn.getPaddingEnd();
        if (realWidth <= 0 || contentTextList.size() == 0) {
            return drawOn.getPaddingTop() + drawOn.getPaddingBottom();
        }

        textPaint.setTextSize(fontSize);
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        final float fontHeight = -metrics.ascent - metrics.descent;
        final int lHeight = Math.max((int) (fontHeight + 0.5f), lineHeight);

        float ellipsisWidth;
        ellipsisPart.reset();
        if (ellipsisText == null || ellipsisText.length() == 0) {
            ellipsisWidth = 0;
        } else {
            ellipsisWidth = textPaint.measureText(ellipsisText);
        }
        recycler.reset(drawOn.getPaddingStart(), drawOn.getPaddingTop(), fontHeight, lHeight);
        int currentLine = 0;
        int startIndex;
        float usedWidth = 0f;
        boolean newline = true;
        boolean newParagraph = false;
        float addParagraphSpace = 0f;
        for (TagTextItem textInfo : contentTextList) {
            startIndex = 0;
            textInfo.parts.clear();
            TagTextMeasureman.TagTextMeasureResult measureResult;
            if (textInfo.textStr != null && textInfo.textStr.length() > 0) {
                if (usedWidth < 0) {
                    continue;
                }
                while (startIndex < textInfo.textStr.length()) {
                    if (newline) {
                        currentLine++;
                        usedWidth = 0f;
                        if (newParagraph) {
                            addParagraphSpace = addParagraphSpace + paragraphSpace;
                        }
                    }
                    if (currentLine == maxLines) {
                        measureResult = measureman.calculateDrawWidth(
                                realWidth - usedWidth - ellipsisWidth, textInfo.textStr, textPaint, startIndex);
                    } else {
                        measureResult = measureman.calculateDrawWidth(
                                realWidth - usedWidth, textInfo.textStr, textPaint, startIndex);
                    }
                    if (measureResult.drawWidth > 0) {
                        textInfo.parts.add(recycler.obtainOnePiece(
                                startIndex, measureResult.endIndex, usedWidth, measureResult.drawWidth, currentLine, addParagraphSpace));
                    }
                    startIndex = measureResult.endIndex;
                    if (currentLine == maxLines) {
                        newline = false;
                        if (measureResult.newline) {
                            if (ellipsisWidth > 0) {
                                recycler.recombination(ellipsisPart, 0, ellipsisText.length(),
                                        usedWidth + measureResult.drawWidth, ellipsisWidth, currentLine, addParagraphSpace);
                            }
                            usedWidth = -1;
                            break;
                        } else {
                            usedWidth = usedWidth + measureResult.drawWidth;
                        }
                    } else {
                        newline = measureResult.newline;
                        usedWidth = usedWidth + measureResult.drawWidth;
                        newParagraph = measureResult.newParagraph;
                    }
                }
            }
        }
        return (int) (drawOn.getPaddingTop() + drawOn.getPaddingBottom() + addParagraphSpace + 0.499f + currentLine * lHeight);
    }

}
