package com.zpf.tool.event.impl;

import com.zpf.tool.event.api.IEvent;
import com.zpf.tool.event.api.IEventRecord;

import java.util.HashSet;

/**
 * @author Created by ZPF on 2021/6/7.
 */
public class EventRecord implements IEventRecord {
    public final IEvent event;
    private volatile boolean interrupted;
    private final HashSet<String> reader = new HashSet<>();

    public EventRecord(IEvent event) {
        this.event = event;
    }

    @Override
    public int readTime() {
        return reader.size();
    }

    @Override
    public void interrupt() {
        interrupted = true;
    }

    @Override
    public boolean checkReceived(String receiver) {
        if (reader.size() == 0) {
            return false;
        }
        return reader.contains(receiver);
    }

    void addReader(String name) {
        reader.add(name);
    }

    boolean isInterrupt() {
        return interrupted;
    }

    IEvent getEvent() {
        return event;
    }
}
