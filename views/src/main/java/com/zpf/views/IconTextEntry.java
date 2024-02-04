package com.zpf.views;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;

public class IconTextEntry implements Parcelable {
    private int iconResId;
    private int imageResId;
    private int textResId;
    private String clickAction;
    private String textString;
    @ColorInt
    private int textColor;
    private int textSize;

    public IconTextEntry() {

    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getTextResId() {
        return textResId;
    }

    public void setTextResId(int textResId) {
        this.textResId = textResId;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public String getTextString() {
        return textString;
    }

    public void setTextString(String textString) {
        this.textString = textString;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    protected IconTextEntry(Parcel in) {
        iconResId = in.readInt();
        imageResId = in.readInt();
        textResId = in.readInt();
        clickAction = in.readString();
        textString = in.readString();
        textColor = in.readInt();
        textSize = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(iconResId);
        dest.writeInt(imageResId);
        dest.writeInt(textResId);
        dest.writeString(clickAction);
        dest.writeString(textString);
        dest.writeInt(textColor);
        dest.writeInt(textSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IconTextEntry> CREATOR = new Creator<IconTextEntry>() {
        @Override
        public IconTextEntry createFromParcel(Parcel in) {
            return new IconTextEntry(in);
        }

        @Override
        public IconTextEntry[] newArray(int size) {
            return new IconTextEntry[size];
        }
    };
}