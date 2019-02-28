package com.zpf.api.dataparser;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ZPF on 2018/10/25.
 */
public interface JsonParserInterface {
    <T> T fromJson(Object object, Type classType);

    <T> List<T> fromJsonList(Object object, Type classType);

    String toString(Object object);

    String getStringByName(Object content, String name);

    StringParseResult parseString(String content);
}
