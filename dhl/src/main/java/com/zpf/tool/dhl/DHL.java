package com.zpf.tool.dhl;

import android.util.SparseArray;

import com.zpf.tool.dhl.interfaces.ExpressageInterface;
import com.zpf.tool.dhl.interfaces.ParcelReceiverInterface;

/**
 * 数据传递服务商
 * Created by ZPF on 2018/11/9.
 */
public class DHL {
    private SparseArray<DeliveryLocker> deliveryLockers = new SparseArray<>();
    private DeliveryLocker defDeliveryLocker = new DeliveryLocker();

    private DHL() {
    }

    private static volatile DHL mInstance;

    public static DHL get() {
        if (mInstance == null) {
            synchronized (DHL.class) {
                if (mInstance == null) {
                    mInstance = new DHL();
                }
            }
        }
        return mInstance;
    }

    public boolean put(ExpressageInterface expressageInterface) {
        if (expressageInterface != null) {
            defDeliveryLocker.put(expressageInterface);
            return true;
        }
        return false;
    }

    /**
     * @param lockerId 存储柜id
     * @param expressageInterface 存储包裹
     */
    public boolean put(int lockerId, ExpressageInterface expressageInterface) {
        DeliveryLocker deliveryLocker = deliveryLockers.get(lockerId);
        if (expressageInterface != null && deliveryLocker != null) {
            deliveryLocker.put(expressageInterface);
            return true;
        } else {
            return false;
        }
    }

    public void send(ParcelReceiverInterface receiverInterface) {
        if (receiverInterface != null) {
            defDeliveryLocker.pick(receiverInterface);
        }
    }

    /**
     * @param lockerId 存储柜id
     * @param receiverInterface 收件人信息
     */
    public void send(int lockerId, ParcelReceiverInterface receiverInterface) {
        if (receiverInterface != null) {
            DeliveryLocker deliveryLocker = deliveryLockers.get(lockerId);
            if (deliveryLocker != null) {
                deliveryLocker.pick(receiverInterface);
            } else {
                receiverInterface.unpackRemnants(null);
            }
        }
    }

    /**
     * @param lockerId 分配的id
     * @param deliveryLocker 数据柜
     */
    public void addDeliveryLocker(int lockerId, DeliveryLocker deliveryLocker) {
        if (deliveryLocker != null) {
            deliveryLockers.put(lockerId, deliveryLocker);
        }
    }

    public void clear(int lockerId) {
        deliveryLockers.remove(lockerId);
    }

    public void clear() {
        defDeliveryLocker.clear();
    }

    public void clearAll() {
        defDeliveryLocker.clear();
        deliveryLockers.clear();
    }
}
