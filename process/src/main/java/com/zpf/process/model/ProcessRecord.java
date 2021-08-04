package com.zpf.process.model;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.zpf.process.aidl.IServiceHandler;
import com.zpf.process.manager.HostManagerImpl;
import com.zpf.process.service.Service1;
import com.zpf.process.service.Service2;
import com.zpf.process.service.Service3;
import com.zpf.process.service.Service4;
import com.zpf.process.service.Service5;
import com.zpf.process.util.ProcessUtil;

import java.util.LinkedList;

/**
 * @author Created by ZPF on 2021/4/23.
 */
public class ProcessRecord {
    private final LinkedList<ServiceRecord> records;

    public ProcessRecord(int size) {
        if (size < 1 || size > 5) {
            throw new IllegalArgumentException("service size must in 1 to 5,current is " + size);
        }
        Class<?>[] services = new Class<?>[]{Service1.class, Service2.class, Service3.class, Service4.class, Service5.class};
        records = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            records.add(new ServiceRecord(services[i]));
        }
    }

    public void initConfig(Context context, Bundle config) {
        ServiceRecord record;
        HostManagerImpl hostManager = HostManagerImpl.get(context);
        for (int i = 0; i < records.size(); i++) {
            record = records.get(i);
            record.config = config;
            record.hostManager = hostManager;
            if (i == 0) {
                //启动一个服务
                record.startService(context);
            }
        }
    }

    public int getServiceSize() {
        return records.size();
    }

    public void callService(Context context, String clientId, Bundle params) throws RemoteException, RuntimeException {
        if (clientId == null || clientId.length() == 0) {
            throw new IllegalArgumentException("clientId不合法");
        }
        if (getServiceSize() == 0 || context == null) {
            throw new RuntimeException("请完成初始化并在主进程内调用");
        }
        HostManagerImpl hostManager = HostManagerImpl.get(context);
        if (hostManager == null) {
            throw new RuntimeException("请在主进程内调用");
        }
        ServiceRecord targetService = null;//正在或曾经为此服务的进程
        ServiceRecord emptyService = null;//已经启动的空闲进程
        ServiceRecord newService = null;//未启动的空闲进程
        ServiceRecord lastService = null;//最早启动的进程
        int i;
        for (ServiceRecord s : records) {
            s.hostManager = hostManager;
            i = s.search(clientId);
            if (i == 0) {
                targetService = s;
                break;
            } else if (i == 1) {
                if (emptyService == null) {
                    emptyService = s;
                }
            } else if (i == 2) {
                if (lastService == null || s.startTime < lastService.startTime) {
                    lastService = s;
                }
            } else if (i == 3) {
                if (newService == null) {
                    newService = s;
                }
            }
        }
        if (targetService != null) {
            targetService.startToWork(context, clientId, params);
        } else if (emptyService != null) {
            emptyService.startToWork(context, clientId, params);
            //预热一个新进程
            if (newService != null) {
                newService.startService(context);
            }
        } else if (newService != null) {
            newService.startToWork(context, clientId, params);
        } else if (lastService != null) {
            lastService.startToWork(context, clientId, params);
        } else {
            throw new RuntimeException("没有合适的服务");
        }
    }

    public IServiceHandler findServiceHandler(Context context, String clientId) {
        if (clientId == null || clientId.length() == 0) {
            throw new IllegalArgumentException("clientId不合法");
        }
        if (getServiceSize() == 0 || context == null) {
            throw new RuntimeException("还没有完成初始化");
        }
        if (!ProcessUtil.isHostProcess(context)) {
            throw new RuntimeException("请在主进程内调用");
        }
        for (ServiceRecord s : records) {
            if (s.search(clientId) == 0) {
                return s.getServiceHandler();
            }
        }
        return null;
    }

    public void updateServiceHandler(int serviceId, IServiceHandler serviceHandler) {
        for (ServiceRecord s : records) {
            if (s.updateServiceInfo(serviceId, serviceHandler)) {
                break;
            }
        }
    }

    public void resumeService(Context context, LinkedList<Integer> list) {
        boolean handled;
        while (list != null && list.size() > 0) {
            handled = false;
            for (ServiceRecord s : records) {
                if (list.remove((Integer) s.serviceId)) {
                    s.startService(context);
                    handled = true;
                }
            }
            if (!handled) {
                break;
            }
        }
    }
}