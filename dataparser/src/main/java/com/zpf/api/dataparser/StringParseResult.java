package com.zpf.api.dataparser;

/**
 * Created by ZPF on 2018/11/23.
 */
public class StringParseResult {
    @StringParseType
    private int type;
    private Object data;

    public StringParseResult(@StringParseType int type, Object data) {
        this.type = type;
        this.data = data;
    }

    @StringParseType
    public int getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
