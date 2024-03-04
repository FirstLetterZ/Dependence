package com.zpf.tool;

import java.util.List;

public class EmojiUtil {

    public static boolean isEmojiNationalFlag(int codePoint) {
        return codePoint >= 127462 && codePoint <= 127487;
    }

    // String str = new String(new int[]{0x1F44B, 0x1F3FD}, 0, 2);
    public static boolean isEmojiSkinColor(int codePoint) {
        return codePoint >= 127995 && codePoint <= 127999;
    }

    // String str = new String(new int[]{0x1F3F4, 0xE0067, 0xE0062, 0xE0065, 0xE006E, 0xE0067, 0xE007F}, 0, 7);
    public static boolean isEmojiTagEnd(int codePoint) {
        return codePoint == 917631;
    }

    public static boolean isEmojiTagSpec(int codePoint) {
        return codePoint >= 917536 && codePoint <= 917630;
    }

    public static boolean isEmojiDecorateBlock(Character.UnicodeBlock block) {
        if (block == null) {
            return false;
        }
        return block.equals(Character.UnicodeBlock.LOW_SURROGATES)
                || block.equals(Character.UnicodeBlock.VARIATION_SELECTORS)
                || block.equals(Character.UnicodeBlock.VARIATION_SELECTORS_SUPPLEMENT)
                || block.equals(Character.UnicodeBlock.COMBINING_HALF_MARKS)
                || block.equals(Character.UnicodeBlock.COMBINING_MARKS_FOR_SYMBOLS)
                || block.equals(Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS)
                || block.equals(Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS_SUPPLEMENT);
    }

    public static void pickAllEmoji(CharSequence data, StringBuilder removeResult, List<String> emojiList) {
        if (removeResult == null && emojiList == null) {
            return;
        }
        if (removeResult != null) {
            removeResult.delete(0, removeResult.length());
        }
        if (emojiList != null) {
            emojiList.clear();
        }
        if (data == null || data.length() == 0) {
            return;
        }
        StringBuilder emojiBuilder = new StringBuilder();
        int i = 0;
        int codePoint;
        Character.UnicodeBlock block;
        while (i < data.length()) {
            if (i + 1 < data.length()) {
                block = Character.UnicodeBlock.of(data.charAt(i + 1));
                if (isEmojiDecorateBlock(block)) {
                    codePoint = Character.codePointAt(data, i);
                    if (isEmojiNationalFlag(codePoint)) {
                        recordEmoji(emojiBuilder, emojiList);
                        if (i + 3 < data.length()) {
                            codePoint = Character.codePointAt(data, i + 2);
                            if (isEmojiNationalFlag(codePoint)) {
                                emojiBuilder.append(data, i, i + 4);
                                recordEmoji(emojiBuilder, emojiList);
                            }
                            i = i + 4;
                        } else {
                            i = i + 2;
                        }
                    } else {
                        if (i + 2 < data.length() && data.charAt(i + 2) == '\u200D') {
                            emojiBuilder.append(data, i, i + 3);
                            i = i + 3;
                        } else if (i + 3 < data.length()) {
                            codePoint = Character.codePointAt(data, i + 2);
                            if (isEmojiSkinColor(codePoint)) {
                                emojiBuilder.append(data, i, i + 4);
                                recordEmoji(emojiBuilder, emojiList);
                                i = i + 4;
                            } else if (isEmojiTagSpec(codePoint)) {
                                emojiBuilder.append(data, i, i + 4);
                                i = i + 4;
                                while (i < data.length()) {
                                    codePoint = Character.codePointAt(data, i);
                                    if (isEmojiTagSpec(codePoint)) {
                                        emojiBuilder.append(data, i, i + 2);
                                        i = i + 2;
                                    } else if (isEmojiTagEnd(codePoint)) {
                                        emojiBuilder.append(data, i, i + 2);
                                        recordEmoji(emojiBuilder, emojiList);
                                        i = i + 2;
                                        break;
                                    } else { //error
                                        break;
                                    }
                                }
                                emojiBuilder.delete(0, emojiBuilder.length());
                            } else if (isEmojiTagEnd(codePoint)) {
                                emojiBuilder.append(data, i, i + 4);
                                recordEmoji(emojiBuilder, emojiList);
                                i = i + 4;
                            } else {
                                emojiBuilder.append(data, i, i + 2);
                                recordEmoji(emojiBuilder, emojiList);
                                i = i + 2;
                            }
                        } else {
                            emojiBuilder.append(data, i, i + 2);
                            recordEmoji(emojiBuilder, emojiList);
                            i = i + 2;
                        }
                    }
                    continue;
                }
            }
            int type = Character.getType(data.charAt(i));
            if (type == (int) Character.OTHER_SYMBOL) {
                if (emojiList != null) {
                    emojiList.add(String.valueOf(data.charAt(i)));
                }
            } else if (removeResult != null) {
                removeResult.append(data.charAt(i));
            }
            i++;
        }
        recordEmoji(emojiBuilder, emojiList);
    }

    private static void recordEmoji(StringBuilder builder, List<String> emojiList) {
        if (builder != null && builder.length() > 0) {
            if (emojiList != null) {
                emojiList.add(builder.toString());
            }
            builder.delete(0, builder.length());
        }
    }

}