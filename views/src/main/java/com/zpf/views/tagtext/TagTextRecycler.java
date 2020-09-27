package com.zpf.views.tagtext;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TagTextRecycler {
    private int index = 0;
    private int itemPosition = 0;
    private int paddingStart;
    private int paddingTop;
    private float textHeight;
    private int lineHeight;
    private List<TagTextPieceInfo> partList;
    private List<TagTextItem> itemList;

    public TagTextRecycler() {
        itemList = new ArrayList<>(16);
        partList = new ArrayList<>(48);
        for (int i = 0; i < 48; i++) {
            partList.add(new TagTextPieceInfo());
            if (i < 16) {
                itemList.add(new TagTextItem());
            }
        }
    }

    public TagTextItem obtainTextItem(int id, String text, TagTextStyle style) {
        return obtainTextItem(id, text, style,true);
    }

    public TagTextItem obtainTextItem(int id, String text, TagTextStyle style, boolean next) {
        if (next) {
            itemPosition++;
        } else {
            itemPosition = 0;
        }
        while (itemList.size() <= itemPosition) {
            itemList.add(new TagTextItem());
        }
        TagTextItem tagTextItem = itemList.get(itemPosition);
        tagTextItem.textId = id;
        tagTextItem.textStr = text;
        tagTextItem.style.copyStyle(style);
        return tagTextItem;
    }

    public void reset(int dX, int dY, float tHeight, int lHeight) {
        index = 0;
        textHeight = tHeight;
        lineHeight = lHeight;
        paddingTop = dY;
        paddingStart = dX;
    }

    public TagTextPieceInfo obtainOnePiece(int startIndex, int endIndex, float left, float width, int line, float addParagraphSpace) {
        while (partList.size() <= index) {
            partList.add(new TagTextPieceInfo());
        }
        TagTextPieceInfo textPart = partList.get(index);
        recombination(textPart, startIndex, endIndex, left, width, line, addParagraphSpace);
        index++;
        return textPart;
    }

    public void recombination(@NonNull TagTextPieceInfo pieceInfo, int startIndex, int endIndex, float left, float width, int line, float addParagraphSpace) {
        pieceInfo.startIndex = startIndex;
        pieceInfo.endIndex = endIndex;
        pieceInfo.left = paddingStart + left;
        pieceInfo.top = paddingTop + lineHeight * (line - 1) + addParagraphSpace;
        pieceInfo.right = left + width;
        pieceInfo.bottom = pieceInfo.top + lineHeight;
        pieceInfo.drawX = left;
        pieceInfo.drawY = pieceInfo.top + (lineHeight + textHeight) / 2;
    }
}
