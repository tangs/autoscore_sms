package com.tangs.myapplication.ui.main.data;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalSharedPreferences {
    private static final String DATABASE_NAME = "info";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_PLATFORM = "platform";
    private static final String KEY_HOST = "host";

    private SharedPreferences sharedPreferences;

    public LocalSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
    }

    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phoneNumber).commit();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(KEY_PHONE_NUMBER,"");
    }

    public void setPlatform(String platform) {
        sharedPreferences.edit().putString(KEY_PLATFORM, platform).commit();
    }

    public String getPlatform() {
        return sharedPreferences.getString(KEY_PLATFORM,"");
    }

    public void setHost(String host) {
        sharedPreferences.edit().putString(KEY_HOST, host).commit();
    }

    public String getHost() {
        return sharedPreferences.getString(KEY_HOST,"");
    }
}
