package com.zpf.tool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Created by ZPF on 2021/2/18.
 */
public class SingleGroupTask<R> {
    private long retryInterval = 3000;//失败重试间隔
    private long refreshInterval = 600000;//缓存刷新间隔
    private long cacheEffectiveTime = 0;//缓存有效时间
    private final long minIntervalTime = 200;//重调接口的最小时间间隔
    private long lastCacheTime = 0L;//上次缓存时间
    private long maxWaitTime = 630000L;//最大等待时间，避免忘记stop导致线程不释放
    private int maxRefreshCount = 1;//最大刷新次数：等于0--不刷新，小于0--无限刷新，大于0--有限次数刷新
    private int maxRetryCount = 1;//最大重试次数：等于0--不重试，小于0--无限重试，大于0--有限次数重试
    private int failCount = 0;//失败重试计数
    private int successCount = 0;//成功刷新计数
    private final Object lock = new Object();
    private R[] cacheArray;
    private ResultListener<R> resultListener;
    private RequestExecutor<R> requestExecutor;
    private int requestCount = 0;
    private volatile long nextRequestTime = 0L;
    private volatile boolean loading = false;
    private volatile Thread workThread = null;

    public void setRequestExecutor(RequestExecutor<R> requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    public void setResultListener(ResultListener<R> resultListener) {
        this.resultListener = resultListener;
    }

    public void setMaxRefreshCount(int maxRefreshCount) {
        this.maxRefreshCount = maxRefreshCount;
        successCount = 0;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
        failCount = 0;
    }

    public void setCacheEffectiveTime(long cacheEffectiveTime) {
        this.cacheEffectiveTime = cacheEffectiveTime;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = Math.max(refreshInterval, minIntervalTime);
        maxWaitTime = Math.max(this.refreshInterval + 30000, maxWaitTime);
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = Math.max(retryInterval, minIntervalTime);
        maxWaitTime = Math.max(this.retryInterval + 30000, maxWaitTime);
    }

    public void reload(boolean onlyFail) {
        if (loading) {
            return;
        }
        if (!onlyFail) {
            cacheArray = null;
        }
        startLoad(requestCount);
    }

    public void stopLoad() {
        loading = false;
        if (workThread != null) {
            workThread.interrupt();
        }
        workThread = null;
    }

    public void startLoad() {
        startLoad(1);
    }

    public void startLoad(int size) {
        if (loading || requestExecutor == null || size <= 0) {
            return;
        }
        requestCount = size;
        if (workThread == null || workThread.isInterrupted()) {
            workThread = createThread();
            workThread.start();
        }
        successCount = 0;
        failCount = 0;
        nextRequestTime = 0;
        synchronized (lock) {
            lock.notify();
        }
    }

    public boolean isLiving() {
        return workThread != null && !workThread.isInterrupted();
    }

    public long getNextRequestTime() {
        return nextRequestTime;
    }

    public void clearCache() {
        if (loading) {
            return;
        }
        cacheArray = null;
    }

    private long calculateNextRequestTime(boolean success) {
        long nextRequestTime;
        if (success) {
            successCount++;
            if (successCount <= 0) {
                successCount = 1;
            }
            failCount = 0;
            if (maxRefreshCount < 0 || successCount <= maxRefreshCount) {
                nextRequestTime = System.currentTimeMillis() + refreshInterval + 20;
            } else {
                nextRequestTime = -1;
            }
        } else {
            failCount++;
            if (maxRetryCount < 0 || failCount <= maxRetryCount) {
                nextRequestTime = System.currentTimeMillis() + retryInterval;
            } else {
                nextRequestTime = -1;
            }
        }
        return nextRequestTime;
    }

    //发生异常导致回调时，返回size小于0
    private void callListener(final boolean success, final int size, @NonNull final List<R> list) {
        if (resultListener != null) {
            resultListener.onResult(success, size, list);
        }
    }

    private Thread createThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                long startWaitTime = System.currentTimeMillis();
                long waitTimeout;
                final LinkedList<Integer> requestIndexList = new LinkedList<>();
                try {
                    while (System.currentTimeMillis() - startWaitTime < maxWaitTime) {
                        synchronized (lock) {
                            if (nextRequestTime > 0) {
                                waitTimeout = nextRequestTime - System.currentTimeMillis();
                            } else if (nextRequestTime == 0) {
                                waitTimeout = 0;
                            } else {
                                waitTimeout = maxWaitTime;
                            }
                            final RequestExecutor<R> executor = requestExecutor;
                            final int size = requestCount;
                            if (executor == null || size <= 0 || waitTimeout > 0) {
                                loading = false;
                                nextRequestTime = 0;
                                if (waitTimeout <= 0) {
                                    waitTimeout = maxWaitTime;
                                } else {
                                    waitTimeout = Math.max(waitTimeout, minIntervalTime);
                                }
                                //进入等待
                                startWaitTime = System.currentTimeMillis();
                                lock.wait(waitTimeout);
                                continue;
                            }
                            loading = true;
                            //检查缓存有效时间
                            if (cacheEffectiveTime <= 0 || System.currentTimeMillis() - lastCacheTime > cacheEffectiveTime) {
                                cacheArray = null;
                            }
                            if (cacheArray == null || cacheArray.length != size) {
                                cacheArray = (R[]) new Object[size];
                            }
                            final R[] resultCache = cacheArray;
                            requestIndexList.clear();
                            R cache;
                            for (int i = 0; i < size; i++) {
                                cache = resultCache[i];
                                if (cache == null) {
                                    requestIndexList.add(i);
                                }
                            }
                            final long delay;
                            if (requestIndexList.size() == 0) {
                                //全部读取缓存
                                nextRequestTime = SingleGroupTask.this.calculateNextRequestTime(true);
                                if (nextRequestTime < 0) {
                                    delay = maxWaitTime;
                                } else {
                                    delay = Math.max(nextRequestTime - System.currentTimeMillis(), minIntervalTime);
                                }
                                SingleGroupTask.this.callListener(true, size, Arrays.asList(resultCache));
                            } else {
                                final AtomicInteger count = new AtomicInteger(requestIndexList.size());
                                for (int index : requestIndexList) {
                                    final int n = index;
                                    executor.asyncRequest(index, new ResultCallback<R>() {
                                        @Override
                                        public void callback(@Nullable R result) {
                                            resultCache[n] = result;
                                            if (count.decrementAndGet() == 0) {
                                                boolean success = true;
                                                for (R r : resultCache) {
                                                    if (r == null) {
                                                        success = false;
                                                        break;
                                                    }
                                                }
                                                lastCacheTime = System.currentTimeMillis();
                                                nextRequestTime = SingleGroupTask.this.calculateNextRequestTime(success);
                                                SingleGroupTask.this.callListener(success, size, Arrays.asList(resultCache));
                                                cacheArray = resultCache;
                                                if (nextRequestTime >= 0) {
                                                    synchronized (lock) {
                                                        lock.notify();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                                delay = maxWaitTime;
                            }
                            loading = false;
                            if (delay > 0) {
                                //进入等待
                                startWaitTime = System.currentTimeMillis();
                                lock.wait(delay);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (loading) {
                        e.printStackTrace();
                        //发生异常，触发回调
                        SingleGroupTask.this.callListener(false, -1, (List<R>) Collections.emptyList());
                    }
                } finally {
                    loading = false;
                    workThread = null;
                }
            }
        });
    }

    public interface ResultCallback<T> {
        void callback(@Nullable T result);
    }

    public interface ResultListener<T> {
        void onResult(boolean success, int requestSize, @NonNull List<T> resultList);
    }

    public interface RequestExecutor<T> {
        void asyncRequest(int index, ResultCallback<T> callback);
    }
}
