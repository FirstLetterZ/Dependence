package com.zpf.views.tagtext;

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
}
