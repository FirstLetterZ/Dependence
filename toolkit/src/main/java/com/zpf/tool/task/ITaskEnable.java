package com.zpf.tool.task;

public interface ITaskEnable {
    default boolean enable() {
        return true;
    }
}
