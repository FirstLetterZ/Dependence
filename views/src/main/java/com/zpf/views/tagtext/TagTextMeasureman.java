package com.zpf.views.tagtext;

import android.graphics.Paint;

public class TagTextMeasureman {

    private TagTextMeasureResult measureResult = new TagTextMeasureResult();

    TagTextMeasureResult calculateDrawWidth(float width, String textStr, Paint paint, int startIndex) {
        measureResult.newline = false;
        measureResult.newParagraph = false;
        measureResult.startIndex = startIndex;
        if (width <= 0 || textStr == null || textStr.length() == 0 || startIndex >= textStr.length()) {
            measureResult.endIndex = startIndex;
            measureResult.drawWidth = 0f;
            return measureResult;
        }
        int endIndex = startIndex + 1;
        float lastWidth;
        float measureWidth = 0f;
        char ci;
        while (endIndex > startIndex) {
            lastWidth = measureWidth;
            ci = textStr.charAt(endIndex - 1);
            if ('\n' == ci) {
                measureResult.drawWidth = lastWidth;
                measureResult.newline = true;
                measureResult.newParagraph = true;
                break;
            }
            measureWidth = paint.measureText(textStr, startIndex, endIndex);
            if (measureWidth > width) {
                measureResult.newline = true;
                endIndex--;
                if (lastWidth < measureWidth) {
                    measureResult.drawWidth = lastWidth;
                    break;
                }
            } else if (measureWidth == width || endIndex == textStr.length()) {
                measureResult.drawWidth = measureWidth;
                if (!measureResult.newline) {
                    measureResult.newline = measureWidth == width;
                }
                break;
            } else {
                endIndex++;
                if (lastWidth > measureWidth) {
                    measureResult.drawWidth = lastWidth;
                    break;
                }
            }
        }
        measureResult.endIndex = endIndex;
        return measureResult;
    }

    static class TagTextMeasureResult {
        int startIndex;
        int endIndex;
        float drawWidth;
        boolean newline;
        boolean newParagraph;
    }
}
