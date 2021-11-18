package com.zpf.apptest.task;

/**
 * @author Created by ZPF on 2021/10/16.
 */
public interface ITaskAuditor {
    Result onRequest(int type, Object params, Result prevResult);

    Result onResponse(int type, TaskResult response, Result prevResult);

    class Result {
        public boolean success = true;
        public int code = 0;
        public String message = null;
    }
}
