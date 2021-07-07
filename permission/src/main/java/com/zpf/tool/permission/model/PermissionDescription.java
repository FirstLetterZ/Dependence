package com.zpf.tool.permission.model;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("InlinedApi")
public class PermissionDescription {

    private static class Instance {
        static final PermissionDescription pr = new PermissionDescription();
    }

    public static PermissionDescription get() {
        return Instance.pr;
    }

    private final HashMap<String, PermissionInfo> permissions;

    public PermissionDescription() {
        permissions = new HashMap<>(24);

        permissions.put(Manifest.permission.WRITE_CONTACTS,
                new PermissionInfo(Manifest.permission.WRITE_CONTACTS, "写入联系人", Manifest.permission_group.CONTACTS));
        permissions.put(Manifest.permission.READ_CONTACTS,
                new PermissionInfo(Manifest.permission.READ_CONTACTS, "读取联系人", Manifest.permission_group.CONTACTS));
        permissions.put(Manifest.permission.GET_ACCOUNTS,
                new PermissionInfo(Manifest.permission.GET_ACCOUNTS, "访问账户列表", Manifest.permission_group.CONTACTS));

        permissions.put(Manifest.permission.READ_CALL_LOG,
                new PermissionInfo(Manifest.permission.READ_CALL_LOG, "读取通话记录", Manifest.permission_group.PHONE));
        permissions.put(Manifest.permission.READ_PHONE_STATE,
                new PermissionInfo(Manifest.permission.READ_PHONE_STATE, "获取本机识别码", Manifest.permission_group.PHONE));
        permissions.put(Manifest.permission.CALL_PHONE,
                new PermissionInfo(Manifest.permission.CALL_PHONE, "拨打电话", Manifest.permission_group.PHONE));
        permissions.put(Manifest.permission.WRITE_CALL_LOG,
                new PermissionInfo(Manifest.permission.WRITE_CALL_LOG, "写入通话记录", Manifest.permission_group.PHONE));
        permissions.put(Manifest.permission.USE_SIP,
                new PermissionInfo(Manifest.permission.USE_SIP, "使用SIP视频", Manifest.permission_group.PHONE));
        permissions.put(Manifest.permission.PROCESS_OUTGOING_CALLS,
                new PermissionInfo(Manifest.permission.PROCESS_OUTGOING_CALLS, "处理拨出电话", Manifest.permission_group.PHONE));
        permissions.put(Manifest.permission.ADD_VOICEMAIL,
                new PermissionInfo(Manifest.permission.ADD_VOICEMAIL, "添加语音邮件", Manifest.permission_group.PHONE));

        permissions.put(Manifest.permission.READ_CALENDAR,
                new PermissionInfo(Manifest.permission.READ_CALENDAR, "读取日程信息", Manifest.permission_group.CALENDAR));
        permissions.put(Manifest.permission.WRITE_CALENDAR,
                new PermissionInfo(Manifest.permission.WRITE_CALENDAR, "写入日程信息", Manifest.permission_group.CALENDAR));

        permissions.put(Manifest.permission.CAMERA,
                new PermissionInfo(Manifest.permission.CAMERA, "访问摄像头", Manifest.permission_group.CAMERA));

        permissions.put(Manifest.permission.BODY_SENSORS,
                new PermissionInfo(Manifest.permission.BODY_SENSORS, "读取生命体征相关的传感器数据", Manifest.permission_group.SENSORS));

        permissions.put(Manifest.permission.ACCESS_FINE_LOCATION,
                new PermissionInfo(Manifest.permission.ACCESS_FINE_LOCATION, "获取精确位置", Manifest.permission_group.LOCATION));
        permissions.put(Manifest.permission.ACCESS_COARSE_LOCATION,
                new PermissionInfo(Manifest.permission.ACCESS_COARSE_LOCATION, "获取粗略位置", Manifest.permission_group.LOCATION));

        permissions.put(Manifest.permission.READ_EXTERNAL_STORAGE,
                new PermissionInfo(Manifest.permission.READ_EXTERNAL_STORAGE, "读取手机存储", Manifest.permission_group.STORAGE));
        permissions.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                new PermissionInfo(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入手机存储", Manifest.permission_group.STORAGE));

        permissions.put(Manifest.permission.RECORD_AUDIO,
                new PermissionInfo(Manifest.permission.RECORD_AUDIO, "录音", Manifest.permission_group.MICROPHONE));

        permissions.put(Manifest.permission.READ_SMS,
                new PermissionInfo(Manifest.permission.READ_SMS, "读取短信内容", Manifest.permission_group.SMS));
        permissions.put(Manifest.permission.RECEIVE_WAP_PUSH,
                new PermissionInfo(Manifest.permission.RECEIVE_WAP_PUSH, "接收Wap Push", Manifest.permission_group.SMS));
        permissions.put(Manifest.permission.RECEIVE_MMS,
                new PermissionInfo(Manifest.permission.RECEIVE_MMS, "接收彩信", Manifest.permission_group.SMS));
        permissions.put(Manifest.permission.RECEIVE_SMS,
                new PermissionInfo(Manifest.permission.RECEIVE_SMS, "接收短信", Manifest.permission_group.SMS));
        permissions.put(Manifest.permission.SEND_SMS,
                new PermissionInfo(Manifest.permission.SEND_SMS, "发送短信", Manifest.permission_group.SMS));
    }

    public HashMap<String, PermissionInfo> getDescriptions() {
        return permissions;
    }

    @Nullable
    public PermissionInfo queryMissInfo(String name) {
        return permissions.get(name);
    }

    //获取所有缺失权限的详细描述
    public List<PermissionInfo> queryMissInfo(List<String> list) {
        List<PermissionInfo> permissionInfoList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            PermissionInfo info;
            for (String name : list) {
                info = permissions.get(name);
                if (info == null) {
                    permissionInfoList.add(new PermissionInfo(name, name, null));
                } else {
                    permissionInfoList.add(info);
                }
            }
        }
        return permissionInfoList;
    }
}
