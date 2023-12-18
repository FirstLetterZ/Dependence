package com.zpf.apptest.task;

import java.util.Map;

public interface IRequest {
    int type();

    String token();

    String path();

    String cacheKey();


    Map<String, Object> params();


}
