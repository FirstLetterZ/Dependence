package com.zpf.tool.dhl;

import com.zpf.tool.dhl.interfaces.ExpressageInterface;
import com.zpf.tool.dhl.interfaces.ParcelDisposeInterface;
import com.zpf.tool.dhl.interfaces.ParcelInspectorInterface;
import com.zpf.tool.dhl.interfaces.ParcelReceiverInterface;

import java.util.List;

/**
 * 数据收件人
 * Created by ZPF on 2018/11/8.
 */
public class ParcelReceiver implements ParcelReceiverInterface {
    private String receiver;
    private ParcelDisposeInterface disposer;
    private ParcelInspectorInterface inspector;

    public ParcelReceiver(String receiver) {
        this.receiver = receiver;
    }

    public ParcelReceiver(String receiver, ParcelDisposeInterface disposer) {
        this.receiver = receiver;
        this.disposer = disposer;
    }

    public ParcelReceiver(String receiver, ParcelInspectorInterface inspector) {
        this.receiver = receiver;
        this.inspector = inspector;
    }

    public ParcelReceiver(String receiver, ParcelDisposeInterface disposer, ParcelInspectorInterface inspector) {
        this.receiver = receiver;
        this.disposer = disposer;
        this.inspector = inspector;
    }

    public void setDisposer(ParcelDisposeInterface disposer) {
        this.disposer = disposer;
    }

    public void setInspector(ParcelInspectorInterface inspector) {
        this.inspector = inspector;
    }

    @Override
    public boolean checkSender(String sender) {
        return inspector == null || inspector.checkSender(sender);
    }

    @Override
    public boolean receiveOwnerless() {
        return inspector == null || inspector.receiveOwnerless();
    }

    @Override
    public String getReceiver() {
        return receiver;
    }

    @Override
    public void unpackRemnants(List<ExpressageInterface> parcels) {
        if (disposer != null) {
            disposer.unpackRemnants(parcels);
        }
    }

    @Override
    public boolean unpackNow(ExpressageInterface parcel) {
        return disposer != null && disposer.unpackNow(parcel);
    }

}
