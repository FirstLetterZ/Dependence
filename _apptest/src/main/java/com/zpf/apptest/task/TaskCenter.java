package com.zpf.apptest.task;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Created by ZPF on 2021/10/16.
 */
public class TaskCenter {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ConcurrentHashMap<String, Record> recordMap = new ConcurrentHashMap<>();
    private final SparseArray<ITaskGroup> groups = new SparseArray<>();
    private final LinkedList<ITaskAuditor> auditors = new LinkedList<>();
    private final ExecutorService workThreadPool;

    public TaskCenter() {
        this.workThreadPool = Executors.newCachedThreadPool();
    }

    public TaskCenter(ExecutorService executor) {
        if (executor == null) {
            this.workThreadPool = Executors.newCachedThreadPool();
        } else {
            this.workThreadPool = executor;
        }
    }

    public String submit(final int type, final Object params, final ITaskCallback callback, final boolean callbackOnMain) {
        if (groups.get(type) != null) {
            ITaskAuditor.Result auditResult = new ITaskAuditor.Result();
            ITaskAuditor.Result tempResult = auditResult;
            int i = 0;
            for (ITaskAuditor auditor : auditors) {
                try {
                    auditResult = auditor.onRequest(type, params, tempResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    auditResult.message = "Error in audit process.(index = " + i + ")\n" + e.getMessage();
                    auditResult.success = false;
                }
                if (auditResult == null) {
                    auditResult = tempResult;
                    Logger.getGlobal().log(Level.WARNING, "No results returned during the review of the request, take the last result.(index = " + i + ")");
                } else {
                    tempResult = auditResult;
                }
                if (!auditResult.success) {
                    if (auditResult.message == null) {
                        auditResult.message = "The request was turned down.(index = " + i + ")";
                    }
                    break;
                }
                i++;
            }
            if (auditResult.success) {
                String id = type + "-" + UUID.randomUUID().toString();
                Record record = new Record(type, callbackOnMain);
                record.callback = new WeakReference<>(callback);
                recordMap.put(id, record);
                dispatch(id, type, params, record);
                return id;
            } else {
                callback(callback, type, params, auditResult.code, null, auditResult.message, isInMainThread());
            }
        } else {
            callback(callback, type, params, -1, null,
                    "No service of the corresponding type was found.", isInMainThread());
        }
        return null;
    }

    public boolean cancel(String id) {
        if (id == null) {
            return false;
        }
        Record record = recordMap.remove(id);
        if (record == null) {
            return true;
        }
        Future<?> future = record.future;
        if (future == null) {
            return true;
        }
        return future.cancel(true);
    }

    public void setTaskGroup(int type, ITaskGroup group) {
        if (group == null) {
            return;
        }
        synchronized (groups) {
            groups.append(type, group);
        }
    }

    public void removeTaskGroup(int type) {
        synchronized (groups) {
            groups.remove(type);
        }
    }

    public void addAuditor(ITaskAuditor auditor) {
        if (auditor != null) {
            this.auditors.add(auditor);
        }
    }

    public void removeAuditor(ITaskAuditor auditor) {
        if (auditor != null) {
            this.auditors.remove(auditor);
        }
    }

    private void dispatch(final String id, final int type, final Object params, Record record) {
        record.future = workThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                ITaskGroup group = null;
                try {
                    group = groups.get(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (group == null) {
                    endJob(id, type, params, -3, null, "work group is null");
                    return;
                }
                TaskResult response = null;
                try {
                    response = group.doJob(params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response == null) {
                    endJob(id, type, params, -3, null, "work result is null");
                    return;
                }
                ITaskAuditor.Result auditResult = new ITaskAuditor.Result();
                ITaskAuditor.Result tempResult = auditResult;
                int i = 0;
                for (ITaskAuditor auditor : auditors) {
                    try {
                        auditResult = auditor.onResponse(type, response, tempResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        auditResult.message = "Error in audit process.(index = " + i + ")\n" + e.getMessage();
                        auditResult.success = false;
                    }
                    if (auditResult == null) {
                        auditResult = tempResult;
                        Logger.getGlobal().log(Level.WARNING, "No results returned during the review of the response, take the last result.(index = " + i + ")");
                    } else {
                        tempResult = auditResult;
                    }
                    if (!auditResult.success) {
                        if (auditResult.message == null) {
                            auditResult.message = "The response was turned down.(index = " + i + ")";
                        }
                        break;
                    }
                    i++;
                }
                if (auditResult.success) {
                    endJob(id, type, params, auditResult.code, null, auditResult.message);
                } else {
                    endJob(id, type, params, response.code, response.data, response.message);
                }
            }
        });
    }

    private void endJob(String id, final int type, final Object params, final int code,
                        final Object data, final String message) {
        Record record = recordMap.remove(id);
        if (record == null) {
            return;
        }
        WeakReference<ITaskCallback> reference = record.callback;
        if (reference == null) {
            return;
        }
        callback(reference.get(), type, params, code, data, message, record.callbackOnMain);
    }

    private void callback(ITaskCallback callback, final int type, final Object params, final int code,
                          final Object data, final String message, boolean callbackOnMain) {
        if (callback == null) {
            return;
        }
        if (callbackOnMain && !isInMainThread()) {
            final WeakReference<ITaskCallback> reference = new WeakReference<>(callback);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ITaskCallback c = reference.get();
                    if (c != null) {
                        c.onResult(type, params, code, data, message);
                    }
                }
            });
        } else {
            callback.onResult(type, params, code, data, message);
        }
    }

    private boolean isInMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private static class Record {
        final int type;
        final boolean callbackOnMain;
        WeakReference<ITaskCallback> callback;
        Future<?> future;

        public Record(int type, boolean callbackOnMain) {
            this.type = type;
            this.callbackOnMain = callbackOnMain;
        }
    }

}
