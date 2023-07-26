package com.zpf.apptest.tst;


class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();

        double res1 = solution.minDifficulty(new int[]{6, 5, 4, 3, 2, 1}, 2);//3
        System.out.println("res1=" + res1);

        double res2 = solution.minDifficulty(new int[]{9, 9, 9}, 4);//-1
        System.out.println("res2=" + res2);

        int res3 = solution.minDifficulty(new int[]{1, 1, 1}, 3);//3
        System.out.println("res3=" + res3);

        int res4 = solution.minDifficulty(new int[]{7, 1, 7, 1, 7, 1}, 3);//15
        System.out.println("res4=" + res4);

        int res5 = solution.minDifficulty(new int[]{11, 111, 22, 222, 33, 333, 44, 444}, 6);//843
        System.out.println("res5=" + res5);
    }

    public int minDifficulty(int[] jobDifficulty, int d) {
        if (d > jobDifficulty.length) {
            return -1;
        }

        return -1;
    }

}