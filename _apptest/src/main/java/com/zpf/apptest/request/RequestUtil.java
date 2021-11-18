package com.zpf.apptest.request;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public class RequestUtil {
    public static String appendQuery(String url, String paramData) {
        if (url == null || url.length() == 0 || paramData == null || paramData.length() == 0) {
            return url;
        }
        JSONObject query = null;
        try {
            query = new JSONObject(paramData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (query == null || query.length() == 0) {
            return url;
        }
        int mark = url.indexOf("?");
        StringBuilder queryBuilder;
        if (mark < 0) {
            queryBuilder = new StringBuilder(url);
            queryBuilder.append("?");
        } else {
            String[] parts = url.split("\\?");
            queryBuilder = new StringBuilder(parts[0]);
            queryBuilder.append("?");
            if (parts.length > 1 && parts[1] != null && parts[1].length() > 0) {
                queryBuilder.append(parts[1]).append("&");
            }
        }
        Iterator<String> queryKeys = query.keys();
        String qk;
        int i = 0;
        while (queryKeys.hasNext()) {
            qk = queryKeys.next();
            if (i > 0) {
                queryBuilder.append("&");
            }
            queryBuilder.append(qk)
                    .append("=")
                    .append(query.optString(qk));
            i++;
        }
        return queryBuilder.toString();
    }
}
