package com.zpf.tool;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemeParseResult {
    public final static int ERROR_INIT = 1;
    public final static int ERROR_PARSE_SCHEME = 11;
    public final static int ERROR_PARSE_HOST = 12;
    public final static int ERROR_PARSE_PORT = 13;
    public final static int ERROR_PARSE_PATH = 14;
    public final static int ERROR_PARSE_QUERY = 15;
    public final static int ERROR_UNKNOWN = 99;

    public final String originalUrl;
    public final int errCode;
    public String scheme;
    public String host;
    public String port;
    public String fragment;
    public List<String> pathSegments = new ArrayList<>();
    public Map<String, String> params = new HashMap<>();

    public SchemeParseResult(String originalUrl) {
        this.originalUrl = originalUrl;
        if (originalUrl == null || originalUrl.length() == 0) {
            errCode = ERROR_INIT;
            return;
        }
        char[] chars = originalUrl.toCharArray();
        int parseCode = 0;
        int rule = ERROR_PARSE_SCHEME;
        int index = 0;
        int start = 0;
        String key = null;
        char c;
        while (parseCode == 0 && index < chars.length) {
            c = chars[index];
            if (rule == ERROR_PARSE_SCHEME) {
                if (index > chars.length - 3) {
                    parseCode = ERROR_PARSE_SCHEME;
                } else if (c == ':') {
                    if (index > start && index + 2 < chars.length && chars[index + 1] == '/' && chars[index + 2] == '/') {
                        scheme = new String(chars, start, index - start);
                        rule = ERROR_PARSE_HOST;
                        index = index + 3;
                        start = index;
                    } else {
                        parseCode = ERROR_PARSE_SCHEME;
                    }
                } else {
                    index++;
                }
            } else if (rule == ERROR_PARSE_HOST) {
                if (c == '/') {
                    if (index > start) {
                        host = new String(chars, start, index - start);
                        rule = ERROR_PARSE_PATH;
                        index = index + 1;
                        start = index;
                    } else {
                        parseCode = ERROR_PARSE_HOST;
                    }
                } else if (c == ':') {
                    if (index > start) {
                        host = new String(chars, start, index - start);
                        rule = ERROR_PARSE_PORT;
                        index = index + 1;
                        start = index;
                    } else {
                        parseCode = ERROR_PARSE_HOST;
                    }
                } else if (index == chars.length - 1) {
                    host = new String(chars, start, index - start);
                    break;
                } else {
                    index++;
                }
            } else if (rule == ERROR_PARSE_PORT) {
                if (index > start + 4) {
                    parseCode = ERROR_PARSE_PORT;
                } else if (c == '/') {
                    if (index > start) {
                        port = new String(chars, start, index - start);
                        rule = ERROR_PARSE_PATH;
                        index = index + 1;
                        start = index;
                    } else {
                        parseCode = ERROR_PARSE_PORT;
                    }
                } else if (index == chars.length - 1) {
                    parseCode = ERROR_PARSE_PORT;
                } else {
                    index++;
                }
            } else if (rule == ERROR_PARSE_PATH) {
                if (c == '/') {
                    if (index > start) {
                        pathSegments.add(new String(chars, start, index - start));
                    }
                    index = index + 1;
                    start = index;
                } else if (c == '?') {
                    if (index > start) {
                        pathSegments.add(new String(chars, start, index - start));
                    }
                    rule = ERROR_PARSE_QUERY;
                    index = index + 1;
                    start = index;
                } else if (c == '#') {
                    if (index > start) {
                        pathSegments.add(new String(chars, start, index - start));
                    }
                    fragment = new String(chars, index + 1, chars.length - index - 1);
                    break;
                } else if (index == chars.length - 1) {
                    if (chars.length > start) {
                        pathSegments.add(new String(chars, start, chars.length - start));
                    }
                    break;
                } else {
                    index++;
                }
            } else {
                if (c == '=') {
                    if (index > start) {
                        key = new String(chars, start, index - start);
                    } else {
                        key = null;
                    }
                    index = index + 1;
                    start = index;
                } else if (c == '&' || index == chars.length - 1) {
                    if (key != null) {
                        if (index > start) {
                            params.put(key, new String(chars, start, index - start));
                        } else {
                            params.put(key, "");
                        }
                    }
                    index = index + 1;
                    start = index;
                } else if (c == '?') {//url嵌套：http://host/path?url=http://host2/path2?key=value
                    if (key != null) {
                        params.put(key, new String(chars, start, chars.length - start));
                    }
                    break;
                } else if (c == '#') {
                    if (key != null) {
                        if (index > start) {
                            params.put(key, new String(chars, start, index - start));
                        } else {
                            params.put(key, "");
                        }
                    }
                    fragment = new String(chars, index + 1, chars.length - index - 1);
                    break;
                } else {
                    index++;
                }
            }
        }
        errCode = parseCode;
    }

    public boolean success() {
        return errCode == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "{errCode=" + errCode +
                ", scheme='" + scheme + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", fragment='" + fragment + '\'' +
                ", pathSegments=" + pathSegments +
                ", params=" + params +
                '}';
    }
}