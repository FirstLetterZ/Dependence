package com.zpf.update.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicLong;

public class TaskQueue<P extends Comparable<P>> {
    private final Entry first = new Entry(0L, null, () -> {
    });
    private final AtomicLong counter = new AtomicLong();

    public long add(@Nullable P param, @NonNull Runnable task) {
        Entry entry = new Entry(counter.incrementAndGet(), param, task);
        synchronized (first) {
            Entry last = first;
            Entry current = first.next;
            while (current != null) {
                if (param != null && (current.param == null || param.compareTo(current.param) < 0)) {
                    break;
                }
                last = current;
                current = last.next;
            }
            last.next = entry;
        }
        return entry.id;
    }

    public boolean take(@NonNull IDispatcher<P> predicate) {
        synchronized (first) {
            Entry last = first;
            Entry current = first.next;
            int i = 0;
            while (current != null) {
                if (predicate.apply(current.param, i)) {
                    boolean enable = true;
                    if (current.task instanceof ITaskEnable) {
                        enable = ((ITaskEnable) current.task).enable();
                    }
                    if (enable) {
                        last.next = current.next;
                        current.next = null;
                        predicate.dispatch(current.param, current.task);
                        return true;
                    }
                }
                i++;
                last = current;
                current = last.next;
            }
        }
        return false;
    }

    public boolean remove(long id) {
        synchronized (first) {
            Entry last = first;
            Entry current = first.next;
            while (current != null) {
                if (current.id == id) {
                    last.next = current.next;
                    current.next = null;
                    return true;
                }
                last = current;
                current = last.next;
            }
        }
        return false;
    }

    public boolean remove(@NonNull Runnable task) {
        synchronized (first) {
            Entry last = first;
            Entry current = first.next;
            while (current != null) {
                if (current.task == task) {
                    last.next = current.next;
                    current.next = null;
                    return true;
                }
                last = current;
                current = last.next;
            }
        }
        return false;
    }

    public void clear() {
        synchronized (first) {
            first.next = null;
            counter.set(0L);
        }
    }

    public boolean isEmpty() {
        synchronized (first) {
            return first.next == null;
        }
    }

    protected class Entry {
        public final long id;
        public final P param;
        @NonNull
        public final Runnable task;
        @Nullable
        Entry next;

        Entry(long id, @Nullable P param, @NonNull Runnable task) {
            this.id = id;
            this.param = param;
            this.task = task;
        }
    }

}
