package com.zpf.apptest.tst;

import java.util.HashMap;
import java.util.LinkedList;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        String str = "小蝌蚪游哇游，过了几天，长出了两条后腿。他们看见鲤鱼妈妈在教小鲤鱼捕食，就迎上去，问：“鲤鱼阿姨，我们的妈妈在哪里？”鲤鱼妈妈说：“你们的妈妈有四条腿，宽嘴巴。你们到那边去找吧！”";
        solution.prepare(str);
        solution.setMathText("小蝌蚪游啊游");
        solution.setMathText("过了两天");
        solution.setMathText("长出了两腿");
        solution.setMathText("他们看见鲤鱼阿姨");
        solution.setMathText("在教小鲤鱼游泳");
    }

    public int minDifficulty(int[] jobDifficulty, int d) {
        if (d > jobDifficulty.length) {
            return -1;
        }

        return -1;
    }

    private String str;
    private final HashMap<Character, LinkedList<Integer>> indexMap = new HashMap<>();
    private final LinkedList<Character> tempStrList = new LinkedList<>();
    private int k = 8;
    private float p = 0.8f;
    private int matched = 0;

    public void prepare(String source) {
        str = source;
        indexMap.clear();
        matched = 0;
        char[] chars = source.toCharArray();
        int i = 0;
        for (char c : chars) {
            LinkedList<Integer> list = indexMap.get(c);
            if (list == null) {
                list = new LinkedList<>();
                indexMap.put(c, list);
            }
            list.add(i);
            i++;
        }
    }

    public void setMathText(String input) {
        int maxCount = (int) (k * (1 + 1 / p));
        match(input, matched, maxCount);
    }

    public void match(String input, int start, int maxMatchCount) {
        if (str == null || str.isEmpty() || input == null || input.isEmpty()) {
            return;
        }
        int count = 0;
        char c;
        for (int i = start; i < str.length(); i++) {
            c = str.charAt(i);
            if (isValid(c)) {
                count++;
                if (count == maxMatchCount) {
                    break;
                }
            }
        }
        int end = start + count;
        int[] dp = new int[maxMatchCount];
        int len = input.length();
        int res = -1;
        for (int i = len - 1; i >= Math.max(len - k, 0); i--) {
            c = input.charAt(i);
            LinkedList<Integer> list = indexMap.get(c);
            if (list == null) {
                continue;
            }
            int nextIndex = peekNext(list, matched, end) - matched;
            for (int j = 0; j < count; j++) {
                if (j == nextIndex) {
                    dp[j] = dp[j] + 1;
                    if (j > res) {
                        float tp = dp[j] * 1f / Math.max(i + 1, j + 1);
                        if (tp >= p) {
                            res = j;
                        }
                    }
                    nextIndex = peekNext(list, matched, end) - matched;
                } else if (j > 0) {
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                }
            }
            if (list.isEmpty()) {
                indexMap.remove(c);
            }
        }
        if (res + 1 > 0) {
            matched = matched + res + 1;
        }
        System.out.println("matched==>" + matched);
    }

    private int peekNext(LinkedList<Integer> list, int start, int end) {
        if (list == null || list.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        Integer temp;
        while (!list.isEmpty()) {
            temp = list.poll();
            if (temp == null) {
                continue;
            }
            if (temp < start) {
                continue;
            }
            if (temp >= end) {
                list.addFirst(temp);
                break;
            }
            return temp;
        }
        return Integer.MAX_VALUE;
    }

    public boolean isValid(char c) {
        return c >= 0x4e00 && c <= 0x9fa5;
    }

}