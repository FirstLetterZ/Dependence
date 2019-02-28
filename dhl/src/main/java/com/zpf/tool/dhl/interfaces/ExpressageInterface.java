package com.zpf.tool.dhl.interfaces;

import java.util.List;

/**
 * 传递数据包裹
 * Created by ZPF on 2018/11/8.
 */
public interface ExpressageInterface<T> {
    void put(T part);

    boolean putOnlyOne(T part);

    String getReceiver();

    String getSender();

    T getFirstPart();

    T getLastPart();

    List<T> getAllParts();

}
