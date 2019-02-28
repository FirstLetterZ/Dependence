package com.zpf.tool.dhl;

import com.zpf.tool.dhl.interfaces.ExpressageInterface;

import java.util.LinkedList;
import java.util.List;

/**
 * 数据包裹
 * Created by ZPF on 2018/11/8.
 */
public class Expressage implements ExpressageInterface {
    private String receiver;//收件人
    private String sender;//发件人
    private LinkedList<Object> parts;

    public Expressage(String receiver, String sender) {
        this.receiver = receiver;
        this.sender = sender;
        parts = new LinkedList<>();
    }

    @Override
    public void put(Object object) {
        if (object != null) {
            parts.add(object);
        }
    }

    @Override
    public boolean putOnlyOne(Object object) {
        if (object != null && !parts.contains(object)) {
            parts.add(object);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getReceiver() {
        return receiver;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public Object getFirstPart() {
        return parts.peekFirst();
    }

    @Override
    public Object getLastPart() {
        return parts.peekLast();
    }

    @Override
    public List getAllParts() {
        return parts;
    }

}
