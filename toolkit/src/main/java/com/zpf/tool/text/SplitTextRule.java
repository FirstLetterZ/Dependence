package com.zpf.tool.text;

/**
 * @author Created by ZPF on 2021/12/21.
 */
public class SplitTextRule implements TextRuleChecker {
    private final int[] splitStep;//分割步长
    private final char[] splitChars;//分隔符

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
        int splitTimes = 0;
        boolean isSplitChar;
        int index = 0;
        int nextSpiltIndex = splitStep[0];
        char c;
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
                if (display.length() == nextSpiltIndex) {
                    display.append(splitChars[splitTimes % splitChars.length]);
                    splitTimes++;
                    nextSpiltIndex = display.length() + splitStep[splitTimes % splitStep.length];
                }
                display.append(c);
                index++;
            }
        }
    }

}