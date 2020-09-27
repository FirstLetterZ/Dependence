package com.zpf.views.tagtext;

public class TagTextPieceInfo {
    float left;
    float top;
    float right;
    float bottom;
    float drawX;
    float drawY;
    int startIndex;
    int endIndex;

    void reset() {
        left = 0f;
        top = 0f;
        right = 0f;
        bottom = 0f;
        drawX = 0f;
        drawY = 0f;
        startIndex = 0;
        endIndex = 0;
    }

    boolean shouldDraw() {
        return startIndex < endIndex && right > left && bottom > top;
    }
}
