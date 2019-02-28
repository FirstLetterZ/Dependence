package com.zpf.tool.dhl;

import android.text.TextUtils;

import com.zpf.tool.dhl.interfaces.DeliveryLockerInterface;
import com.zpf.tool.dhl.interfaces.ExpressageInterface;
import com.zpf.tool.dhl.interfaces.ParcelReceiverInterface;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据存储柜
 * Created by ZPF on 2018/11/8.
 */
public class DeliveryLocker implements DeliveryLockerInterface {
    private HashMap<String, List<ExpressageInterface>> parcels = new HashMap<>();
    private final String NO_SIGNED = "NO_SIGNED_EXPRESSAGE";

    @Override
    public void put(ExpressageInterface expressageInterface) {
        if (expressageInterface != null) {
            String receiver = expressageInterface.getReceiver();
            if (TextUtils.isEmpty(receiver)) {
                receiver = NO_SIGNED;
            }
            List<ExpressageInterface> list = parcels.get(NO_SIGNED);
            if (list == null) {
                list = new LinkedList<>();
                parcels.put(receiver, list);
            }
            list.add(expressageInterface);
        }
    }

    @Override
    public void pick(ParcelReceiverInterface receiverInterface) {
        String receiver = receiverInterface.getReceiver();
        List<ExpressageInterface> list;
        List<ExpressageInterface> untreatedList = new LinkedList<>();
        if (!TextUtils.isEmpty(receiver)) {
            list = parcels.get(receiver);
            if (list != null && list.size() > 0) {
                for (ExpressageInterface expressage : list) {
                    if (receiverInterface.checkSender(expressage.getSender())
                            && !receiverInterface.unpackNow(expressage)) {
                        untreatedList.add(expressage);
                    }
                }
            }
            parcels.remove(receiver);
        }
        if (receiverInterface.receiveOwnerless()) {
            list = parcels.get(NO_SIGNED);
            if (list != null && list.size() > 0) {
                for (ExpressageInterface expressage : list) {
                    if (!receiverInterface.unpackNow(expressage)) {
                        untreatedList.add(expressage);
                    }
                }
            }
            parcels.remove(NO_SIGNED);
        }
        receiverInterface.unpackRemnants(untreatedList);
    }

    @Override
    public void clear() {
        parcels.clear();
    }
}
