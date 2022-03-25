package com.zpf.tool.text;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.LinkedList;

/**
 * @author Created by ZPF on 2021/12/21.
 */
public class RuleTextWatcher implements TextWatcher {
    private boolean changed = false;
    private final StringBuilder displayBuilder = new StringBuilder();
    private final StringBuilder dataBuilder = new StringBuilder();
    private final LinkedList<TextRuleChecker> checkers = new LinkedList<>();
    private String validData;
    private String display;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (changed) {
            changed = false;
            return;
        }
        if (s == null) {
            validData = null;
            display = null;
            return;
        }
        final String original = s.toString();
        if (checkers.size() == 0) {
            validData = original;
            display = original;
            return;
        }
        displayBuilder.replace(0, displayBuilder.length(), original);
        dataBuilder.replace(0, dataBuilder.length(), original);
        for (TextRuleChecker checker : checkers) {
            checker.extractValidData(displayBuilder, dataBuilder);
        }
        final String modified = displayBuilder.toString();
        display = modified;
        validData = dataBuilder.toString();
        changed = !original.equals(modified);
        if (changed) {
            s.replace(0, s.length(), modified);
        }
    }

    public void addRuleChecker(TextRuleChecker checker) {
        if (checker != null) {
            checkers.add(checker);
        }
    }

    public String getValue() {
        return validData;
    }

    public String getDisplay() {
        return display;
    }
}
