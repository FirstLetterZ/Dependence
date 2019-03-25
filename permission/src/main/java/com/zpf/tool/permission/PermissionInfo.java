package com.zpf.tool.permission;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZPF on 2018/8/23.
 */
public class PermissionInfo implements Parcelable {
    private String permissionName;
    private String permissionDescription;
    private String permissionGroup;

    public PermissionInfo() {
    }

    public PermissionInfo(String permissionName, String permissionDescription, String permissionGroup) {
        this.permissionName = permissionName;
        this.permissionDescription = permissionDescription;
        this.permissionGroup = permissionGroup;
    }

    protected PermissionInfo(Parcel in) {
        permissionName = in.readString();
        permissionDescription = in.readString();
        permissionGroup = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(permissionName);
        dest.writeString(permissionDescription);
        dest.writeString(permissionGroup);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PermissionInfo> CREATOR = new Creator<PermissionInfo>() {
        @Override
        public PermissionInfo createFromParcel(Parcel in) {
            return new PermissionInfo(in);
        }

        @Override
        public PermissionInfo[] newArray(int size) {
            return new PermissionInfo[size];
        }
    };

    public String getPermissionName() {
        return permissionName;
    }

    public String getPermissionDescription() {
        return permissionDescription;
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }
}
