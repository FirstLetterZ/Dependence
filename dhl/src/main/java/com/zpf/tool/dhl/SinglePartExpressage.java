package com.zpf.tool.dhl;

import com.zpf.tool.dhl.interfaces.ExpressageInterface;

import java.util.Collections;
import java.util.List;

/**
 * 单件数据包裹
 * Created by ZPF on 2018/11/9.
 */
public class SinglePartExpressage<T> implements ExpressageInterface<T> {
    private String receiver;//收件人
    private String sender;//发件人
    private T part;

    public SinglePartExpressage(String receiver, String sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    public SinglePartExpressage(String receiver, String sender, T part) {
        this.receiver = receiver;
        this.sender = sender;
        this.part = part;
    }

    @Override
    public void put(T part) {
        this.part = part;
    }

    @Override
    public boolean putOnlyOne(T part) {
        this.part = part;
        return true;
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
    public T getFirstPart() {
        return part;
    }

    @Override
    public T getLastPart() {
        return part;
    }

    @Override
    public List<T> getAllParts() {
        return Collections.singletonList(part);
    }

}
