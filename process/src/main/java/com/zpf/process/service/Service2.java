package com.zpf.process.service;

import com.zpf.process.model.ServiceId;

/**
 * @author Created by ZPF on 2021/4/8.
 */
@ServiceId(ServiceId.SECOND_SERVICE)
public class Service2 extends ProcessService{

    @Override
    public int getServiceId() {
        return ServiceId.SECOND_SERVICE;
    }
}
