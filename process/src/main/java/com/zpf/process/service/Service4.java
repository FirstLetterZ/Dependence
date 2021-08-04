package com.zpf.process.service;

import com.zpf.process.model.ServiceId;

/**
 * @author Created by ZPF on 2021/4/8.
 */
@ServiceId(ServiceId.FOURTH_SERVICE)
public class Service4 extends ProcessService{

    @Override
    public int getServiceId() {
        return ServiceId.FOURTH_SERVICE;
    }
}
