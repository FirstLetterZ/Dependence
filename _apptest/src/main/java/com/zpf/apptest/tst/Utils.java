package com.zpf.apptest.tst;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static char[][] str2CharArrayArray(String str) {
        List<List<String>> list = str2ListList(str);
        int m = list.size();
        int n = list.get(0).size();
        char[][] res = new char[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = list.get(i).get(j);
                res[i][j] = s.charAt(0);
            }
        }
        return res;
    }

    public static int[][] str2IntArrayArray(String str) {
        List<List<String>> list = str2ListList(str);
        int m = list.size();
        int n = list.get(0).size();
        int[][] res = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = list.get(i).get(j);
                res[i][j] = Integer.parseInt(s);
            }
        }
        return res;
    }

    public static String[][] str2StringArrayArray(String str) {
        List<List<String>> list = str2ListList(str);
        int m = list.size();
        int n = list.get(0).size();
        String[][] res = new String[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = list.get(i).get(j);
                res[i][j] = s;
            }
        }
        return res;
    }

    public static long[][] str2LongArrayArray(String str) {
        List<List<String>> list = str2ListList(str);
        int m = list.size();
        int n = list.get(0).size();
        long[][] res = new long[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                String s = list.get(i).get(j);
                res[i][j] = Long.parseLong(s);
            }
        }
        return res;
    }

    public static List<List<String>> str2ListList(String str) {
        List<List<String>> res = new ArrayList<>();
        List<String> list = null;
        StringBuilder builder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if ('[' == c) {
                if (list == null) {
                    list = new ArrayList<>();
                    res.add(list);
                }
                builder.delete(0, builder.length());
            } else if (']' == c) {
                if (list != null) {
                    list.add(builder.toString());
                    builder.delete(0, builder.length());
                    list = null;
                }
            } else if (',' == c) {
                if (list != null) {
                    list.add(builder.toString());
                    builder.delete(0, builder.length());
                }
            } else if ('\t' != c && '\n' != c && '\r' != c) {
                builder.append(c);
            }
        }
        printListList(res);
        return res;
    }

    public static <T> void printList(List<T> list) {
        StringBuilder builder = new StringBuilder();
        builder.delete(0, builder.length());
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(list.get(i));
        }
        builder.append("]");
        System.out.println(builder);
    }

    public static <T> void printListList(List<List<T>> list) {
        StringBuilder builder = new StringBuilder();
        for (List<?> strList : list) {
            builder.delete(0, builder.length());
            builder.append("[");
            for (int i = 0; i < strList.size(); i++) {
                if (i > 0) {
                    builder.append(",");
                }
                builder.append(strList.get(i));
            }
            builder.append("]");
            System.out.println(builder);
        }
    }

    public static void printArrayArray(Object aa) {
        int m;
        int n;
        StringBuilder builder = new StringBuilder();
        if (aa instanceof char[][]) {
            m = ((char[][]) aa).length;
            n = ((char[][]) aa)[0].length;
            for (int i = 0; i < m; i++) {
                builder.delete(0, builder.length());
                builder.append("[");
                for (int j = 0; j < n; j++) {
                    if (j > 0) {
                        builder.append(",");
                    }
                    builder.append(((char[][]) aa)[i][j]);
                }
                builder.append("]");
                System.out.println(builder);
            }
        } else if (aa instanceof boolean[][]) {
            m = ((boolean[][]) aa).length;
            n = ((boolean[][]) aa)[0].length;
            for (int i = 0; i < m; i++) {
                builder.delete(0, builder.length());
                builder.append("[");
                for (int j = 0; j < n; j++) {
                    if (j > 0) {
                        builder.append(",");
                    }
                    builder.append(((boolean[][]) aa)[i][j]);
                }
                builder.append("]");
                System.out.println(builder);
            }
        } else if (aa instanceof int[][]) {
            m = ((int[][]) aa).length;
            n = ((int[][]) aa)[0].length;
            for (int i = 0; i < m; i++) {
                builder.delete(0, builder.length());
                builder.append("[");
                for (int j = 0; j < n; j++) {
                    if (j > 0) {
                        builder.append(",");
                    }
                    builder.append(((int[][]) aa)[i][j]);
                }
                builder.append("]");
                System.out.println(builder);
            }
        } else if (aa instanceof byte[][]) {
            m = ((byte[][]) aa).length;
            n = ((byte[][]) aa)[0].length;
            for (int i = 0; i < m; i++) {
                builder.delete(0, builder.length());
                builder.append("[");
                for (int j = 0; j < n; j++) {
                    if (j > 0) {
                        builder.append(",");
                    }
                    builder.append(((byte[][]) aa)[i][j]);
                }
                builder.append("]");
                System.out.println(builder);
            }
        } else if (aa instanceof String[][]) {
            m = ((String[][]) aa).length;
            n = ((String[][]) aa)[0].length;
            for (int i = 0; i < m; i++) {
                builder.delete(0, builder.length());
                builder.append("[");
                for (int j = 0; j < n; j++) {
                    if (j > 0) {
                        builder.append(",");
                    }
                    builder.append(((String[][]) aa)[i][j]);
                }
                builder.append("]");
                System.out.println(builder);
            }
        }
    }

}
