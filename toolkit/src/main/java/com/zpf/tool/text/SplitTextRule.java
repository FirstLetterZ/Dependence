package com.zpf.tool.text;

/**
 * @author Created by ZPF on 2021/12/21.
 */
public class SplitTextRule implements TextRuleChecker {
    private final int[] splitStep;//分割步长
    private final char[] splitChars;//分隔符
    private char start = '\0';//将找到第一个相同的字符作为起点
    private int direction = 1;//小于0：向左遍历；大于0：向右遍历；等于0：双向遍历；

    public SplitTextRule(int[] splitStep) {
        this(splitStep, null);
    }

    public SplitTextRule(int[] splitStep, char[] splitChars) {
        if (splitStep != null) {
            this.splitStep = splitStep;
        } else {
            this.splitStep = new int[]{};
        }
        if (splitChars != null) {
            this.splitChars = splitChars;
        } else {
            this.splitChars = new char[]{' '};
        }
    }

    public void setStartPoint(char start, int direction) {
        this.start = start;
        this.direction = direction;
    }

    @Override
    public void extractValidData(StringBuilder display, StringBuilder value) {
        if (splitStep.length == 0 || splitChars.length == 0) {
            return;
        }
        display.delete(0, display.length());
        int vl = value.length();
        if (vl == 0) {
            return;
        }
        int startIndex;
        if (direction <= 0) {
            startIndex = vl;
            if (start != '\0') {
                for (int i = vl - 1; i >= 0; i--) {
                    if (value.charAt(i) == start) {
                        startIndex = i;
                        display.append(start);
                        break;
                    }
                }
            }
        } else {
            startIndex = -1;
            if (start != '\0') {
                for (int i = 0; i < vl; i++) {
                    if (value.charAt(i) == start) {
                        startIndex = i;
                        break;
                    }
                }
            }
        }
        int splitTimes = 0;
        boolean isSplitChar;
        int index;
        int nextSpiltIndex;
        char c;

        index = startIndex - 1;
        if (direction <= 0) {
            nextSpiltIndex = index - splitStep[0];
        } else {
            nextSpiltIndex = -1;
        }
        while (index >= 0 && index < value.length()) {
            c = value.charAt(index);
            isSplitChar = false;
            for (char splitChar : splitChars) {
                if (c == splitChar) {
                    isSplitChar = true;
                    value.delete(index, index + 1);
                    break;
                }
            }
            if (!isSplitChar) {
                if (index == nextSpiltIndex) {
                    display.append(splitChars[splitTimes % splitChars.length]);
                    nextSpiltIndex = nextSpiltIndex - splitStep[splitTimes % splitStep.length];
                    splitTimes++;
                }
                display.append(c);
            }
            index--;
        }
        if (display.length() > 1) {
            display.reverse();
        }

        index = startIndex + 1;
        splitTimes = 0;
        if (direction >= 0) {
            nextSpiltIndex = index + splitStep[0];
        } else {
            nextSpiltIndex = -1;
        }
        while (index >= 0 && index < value.length()) {
            c = value.charAt(index);
            isSplitChar = false;
            for (char splitChar : splitChars) {
                if (c == splitChar) {
                    isSplitChar = true;
                    value.delete(index, index + 1);
                    break;
                }
            }
            if (!isSplitChar) {
                if (index == nextSpiltIndex) {
                    display.append(splitChars[splitTimes % splitChars.length]);
                    nextSpiltIndex = nextSpiltIndex + splitStep[splitTimes % splitStep.length];
                    splitTimes++;
                }
                display.append(c);
                index++;
            }
        }
    }

}