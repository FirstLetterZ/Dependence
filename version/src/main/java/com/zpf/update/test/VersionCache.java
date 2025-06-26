package com.zpf.update.test;

import androidx.annotation.Nullable;

public class VersionCache {

    public static final int FORCE_DOWNGRADE = -100;
    public static final int SUGGEST_DOWNGRADE = -10;
    public static final int NO_NEED_UPGRADE = 0;
    public static final int SUGGEST_UPGRADE = 10;
    public static final int FORCE_UPGRADE = 100;

    public int checkUpdate(@Nullable IVersionData serverData, @Nullable IVersionData localData) {
        if (serverData == null) {
            return NO_NEED_UPGRADE;
        }
        if (localData == null) {
            return FORCE_UPGRADE;
        }
        int diffVersion = serverData.getVersionCode() - localData.getVersionCode();
        if (diffVersion == 0) {
            if (equals(serverData.getResourceMd5(), localData.getResourceMd5())) {
                return NO_NEED_UPGRADE;
            } else {
                return SUGGEST_UPGRADE;
            }
        }
        if (equals(serverData.getVersionName(), localData.getVersionName())) {
            if (diffVersion > 0) {
                return SUGGEST_UPGRADE;
            } else {
                return SUGGEST_DOWNGRADE;
            }
        } else {
            if (diffVersion > 0) {
                return FORCE_UPGRADE;
            } else {
                return FORCE_DOWNGRADE;
            }
        }
    }

    private boolean equals(String s1, String s2) {
        if (s1 == null || s1.isEmpty()) {
            return s2 == null || s2.isEmpty();
        }
        return s1.equals(s2);
    }

    public void load(@Nullable IVersionData serverData, @Nullable IVersionData localData) {
        int code = checkUpdate(serverData, localData);

    }

    private IVersionData loadLocalRecourseInfo(){
        return null;
    }

    private IVersionData loadServerRecourseInfo(){
        return null;
    }
}
