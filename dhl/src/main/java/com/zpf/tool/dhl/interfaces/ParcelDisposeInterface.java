package com.zpf.tool.dhl.interfaces;

import java.util.List;

/**
 * 数据包裹处理
 * Created by ZPF on 2018/11/8.
 */
public interface ParcelDisposeInterface {

    void unpackRemnants(List<ExpressageInterface> parcels);

    boolean unpackNow(ExpressageInterface parcel);
}
