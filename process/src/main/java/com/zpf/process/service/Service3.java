package com.zpf.process.service;

import com.zpf.process.model.ServiceId;

/**
 * @author Created by ZPF on 2021/4/8.
 */
@ServiceId(ServiceId.THIRD_SERVICE)
public class Service3 extends ProcessService{

    @Override
    public int getServiceId() {
        return ServiceId.THIRD_SERVICE;
    }
}
