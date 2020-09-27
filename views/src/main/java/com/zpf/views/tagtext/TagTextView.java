package com.zpf.views.tagtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zpf.views.R;
import com.zpf.views.TypeClickListener;

public class TagTextView extends View {
    private TagTextDelegate textDelegate;

    public TagTextView(Context context) {
        super(context);
        initConfig(null);
    }

    public TagTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfig(context.obtainStyledAttributes(attrs, R.styleable.TagTextView));
    }

    public TagTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context.obtainStyledAttributes(attrs, R.styleable.TagTextView));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TagTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initConfig(context.obtainStyledAttributes(attrs, R.styleable.TagTextView));
    }

    private void initConfig(@Nullable TypedArray typedArray) {
        textDelegate = new TagTextDelegate();
        float d = getResources().getDisplayMetrics().density;
        if (typedArray != null) {
            String ellipsisText = typedArray.getString(R.styleable.TagTextView_ellipsisText);
            textDelegate.ellipsisStyle.color = typedArray.getColor(R.styleable.TagTextView_ellipsisColor, Color.BLUE);
            int lineHeight = (int) (typedArray.getDimension(R.styleable.TagTextView_lineHeight, 16 * d) + 0.5f);
            int maxLines = typedArray.getInteger(R.styleable.TagTextView_maxLines, -1);
            float fontSize = typedArray.getDimension(R.styleable.TagTextView_fontSize, 14 * d);
            float paragraphSpace = typedArray.getDimension(R.styleable.TagTextView_paragraphSpace, 0f);
            textDelegate.defStyle.color = typedArray.getColor(R.styleable.TagTextView_fontColor, Color.DKGRAY);
            textDelegate.defStyle.bold = typedArray.getBoolean(R.styleable.TagTextView_bold, false);
            textDelegate.defStyle.italic = typedArray.getBoolean(R.styleable.TagTextView_italic, false);
            textDelegate.defStyle.underline = typedArray.getBoolean(R.styleable.TagTextView_underline, false);
            textDelegate.defStyle.strikeThru = typedArray.getBoolean(R.styleable.TagTextView_strikeThru, false);
            typedArray.recycle();
            textDelegate.setParagraphSpace(paragraphSpace);
            textDelegate.setFontSize(fontSize);
            textDelegate.setMaxLines(maxLines);
            textDelegate.setLineHeight(lineHeight);
            textDelegate.setEllipsisText(ellipsisText);
        }
        textDelegate.initDefConfig(d);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, textDelegate.measureHeight(this, widthMeasureSpec, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getScrollX(), getScrollY());
        textDelegate.onDraw(this,canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return textDelegate.handleTouchEvent(this, event) || super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(textDelegate.getRealClickListener(l));
    }

    public void setTypeClickListener(@Nullable TypeClickListener l) {
        super.setOnClickListener(textDelegate.getRealClickListener(l));
    }

    public TagTextStyle setContentText(String text, int id) {
        return textDelegate.setContentText(text, id);
    }

    public void clearContent() {
        textDelegate.clearContent();
    }

    public TagTextStyle getDefStyle() {
        return textDelegate.getDefStyle();
    }

    public TagTextStyle addTextItem(String text, int id) {
        return textDelegate.addTextItem(text, id);
    }

    public TagTextStyle addTextItem(String text, int id, int index) {
        return textDelegate.addTextItem(text, id, index);
    }

    public void setEllipsisText(String text) {
        textDelegate.setEllipsisText(text);
    }

    public TagTextStyle getEllipsisStyle() {
        return textDelegate.getEllipsisStyle();
    }

    public void setMaxLines(int maxLines) {
        textDelegate.setMaxLines(maxLines);
    }

    public int getMaxLines() {
        return textDelegate.getMaxLines();
    }

    public void setFontSize(float fontSize) {
        textDelegate.setFontSize(fontSize);
    }

    public float getFontSize() {
        return textDelegate.getFontSize();
    }

    public void checkRefresh() {
        textDelegate.checkRefresh(this);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, textDelegate.checkBorder(y));
    }
}
