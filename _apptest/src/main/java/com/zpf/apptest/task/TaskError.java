package com.zpf.apptest.task;

/**
 * @author Created by ZPF on 2021/10/18.
 */
public class TaskError {
    public final int code;
    public final String desc;

    public TaskError(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TaskError taskError = (TaskError) obj;
        return code == taskError.code;
    }


    public static final TaskError rr1 = new TaskError(-1, "");
    public static final TaskError rr2 = new TaskError(-1, "");
    public static final TaskError rr3 = new TaskError(-1, "");
    public static final TaskError rr4 = new TaskError(-1, "");
    public static final TaskError rr5 = new TaskError(-1, "");
}
