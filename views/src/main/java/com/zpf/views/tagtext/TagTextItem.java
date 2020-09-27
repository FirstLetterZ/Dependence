package com.zpf.views.tagtext;

import java.util.ArrayList;
import java.util.List;

public class TagTextItem {
    public final TagTextStyle style = new TagTextStyle();
    public final List<TagTextPieceInfo> parts = new ArrayList<>();
    int textId;
    String textStr;
}
