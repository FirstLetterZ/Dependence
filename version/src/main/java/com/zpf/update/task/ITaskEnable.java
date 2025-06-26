package com.zpf.update.task;

public interface ITaskEnable {
    default boolean enable() {
        return true;
    }
}
