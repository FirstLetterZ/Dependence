package com.zpf.tool;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by zpf on 2017/8/9.
 */

public class SplitTextWatcher implements TextWatcher {
    private boolean autoSplit = false;
    private int splitCount;//分割次数
    private int[] splitStep = {4};//分割步长
    private String[] splitChars = {" "};//分隔符
    private int selection;//光标位置

    public SplitTextWatcher(int[] splitStep) {
        if (splitStep != null) {
            this.splitStep = splitStep;
        }
    }

    public SplitTextWatcher(int[] splitStep, String[] splitChars) {
        if (splitStep != null) {
            this.splitStep = splitStep;
        }
        if (splitChars != null) {
            this.splitChars = splitChars;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!autoSplit) {
            selection = i + (i2 - i1);
        } else {
            selection = 0;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable != null && editable.length() > 0) {
            int len = editable.length();
            if (autoSplit) {
                autoSplit = false;
            } else {
                String str = String.valueOf(editable);
                if (selection != len || !checkContent(str)) {
                    autoSplit = true;
                    String result = splitContent(str);
                    editable.replace(0, len, result);
                }
            }
        } else {
            autoSplit = false;
        }
    }

    private boolean checkContent(String content) {
        int len = 0;
        if (splitCount > 0) {
            for (int i = 0; i < splitCount + 1; i++) {
                len = len + splitStep[i % splitStep.length] + splitChars[i % splitChars.length].length();
            }
        }
        int a = splitCount % splitStep.length;
        int n = content.length() - len;
        return n > 0 && n <= splitStep[a];
    }

    private String splitContent(String content) {
        String str = content;
        for (String s : splitChars) {
            str = content.replace(s, "");
        }
        final int len = str.length();
        boolean split = true;
        StringBuilder sb = new StringBuilder();
        splitCount = 0;
        int start = 0;
        int end = splitStep[0];
        while (split) {
            if (end >= len) {
                end = len;
                split = false;
            }
            sb.append(str.substring(start, end));
            if (split) {
                int n = splitCount % splitChars.length;
                sb.append(splitChars[n]);
                splitCount++;
                start = end;
                end = end + splitStep[splitCount % splitStep.length];
            }
        }
        return sb.toString();
    }
}