package com.zpf.views.tagtext;

import android.graphics.Paint;

public class TagTextStyle {
    public boolean bold;
    public boolean italic;
    public boolean underline;
    public boolean strikeThru;
    public int color;
    public int touchColor;

    public void copyStyle(TagTextStyle style) {
        bold = style.bold;
        italic = style.italic;
        underline = style.underline;
        strikeThru = style.strikeThru;
        color = style.color;
        touchColor = style.touchColor;
    }

    public void setPaintStyle(Paint paint, int defColor) {
        paint.setFakeBoldText(bold);
        paint.setUnderlineText(underline);
        paint.setStrikeThruText(strikeThru);
        if (italic) {
            paint.setTextSkewX(-0.5f);
        } else {
            paint.setTextSkewX(0f);
        }
        if (color == 0) {
            paint.setColor(defColor);
        } else {
            paint.setColor(color);
        }

    }
}
