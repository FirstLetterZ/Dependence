package com.zpf.tool.config;

import android.content.Context;
import android.content.SharedPreferences;

public class SpInstance {
    public static final String SP_FILE_NAME = "app_shared_preferences_data_file";

    private static class Instance {
        static SharedPreferences spInstance = AppContext.get().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences get() {
        return Instance.spInstance;
    }
}
