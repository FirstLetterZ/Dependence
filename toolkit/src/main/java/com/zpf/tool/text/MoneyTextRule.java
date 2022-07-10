package com.zpf.tool.text;

/**
 * @author Created by ZPF on 2021/12/21.
 */
public class MoneyTextRule implements TextRuleChecker {
    private final int decimalPlaces;

    public MoneyTextRule(int decimalPlaces) {
        this.decimalPlaces = Math.max(0, decimalPlaces);
    }

    @Override
    public void extractValidData(StringBuilder display, StringBuilder value) {
        display.delete(0, display.length());
        if (value.length() == 0) {
            return;
        }
        int decimalIndex = -1;
        char c;
        for (int i = 0; i < value.length(); i++) {
            c = value.charAt(i);
            if (c == '.' && decimalPlaces > 0 && decimalIndex < 0) {
                if (display.length() == 0) {
                    display.append('0');
                }
                display.append('.');
                decimalIndex = display.length();
            } else if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6'
                    || c == '7' || c == '8' || c == '9') {
                display.append(c);
            } else if (c == '0' && display.length() > 0) {
                display.append(c);
            }
            if (decimalIndex > 0 && display.length() == decimalIndex + decimalPlaces) {
                break;
            }
        }
        if (display.length() == 0) {
            display.append('0');
        }
        value.delete(0, value.length());
        value.append(display);
    }


}
