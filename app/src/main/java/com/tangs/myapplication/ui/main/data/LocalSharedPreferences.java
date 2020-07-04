package com.tangs.myapplication.ui.main.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LocalSharedPreferences {
    private static final String DATABASE_NAME = "info";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_PLATFORM = "platform";
    private static final String KEY_HOST = "host";
    private static final String KEY_PLATFORM_FILTER = "platform_filter";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_AUTO_REFRESH = "auto_refresh";
    private static final String KEY_SMS_SEQUENCE_ID = "sms_sequence_id";

    private static LocalSharedPreferences localSharedPreferences;
    private SharedPreferences sharedPreferences;

    private LocalSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(DATABASE_NAME, Context.MODE_PRIVATE);
    }

    public static LocalSharedPreferences getInstance(Context context) {
        if (localSharedPreferences == null) {
            localSharedPreferences = new LocalSharedPreferences(context);
        }
        return localSharedPreferences;
    }

    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(KEY_PHONE_NUMBER,"");
    }

    public void setPlatform(String platform) {
        sharedPreferences.edit().putString(KEY_PLATFORM, platform).apply();
    }

    public String getPlatform() {
        return sharedPreferences.getString(KEY_PLATFORM,"");
    }

    public void setHost(String host) {
        sharedPreferences.edit().putString(KEY_HOST, host).apply();
    }

    public String getHost() {
        return sharedPreferences.getString(KEY_HOST,"");
    }

//    public void setDarkModeIfNotContains(boolean isDark) {
//        if (sharedPreferences.contains(KEY_DARK_MODE)) return;
//        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDark).commit();
//    }

    public void setDarkMode(boolean isDark) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDark).apply();
    }

    public boolean isDarkMode(boolean def) {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, def);
    }

    public void setAutoRefresh(boolean autoRefresh) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_REFRESH, autoRefresh).apply();
    }

    public boolean isAutoRefresh(boolean def) {
        return sharedPreferences.getBoolean(KEY_AUTO_REFRESH, def);
    }

    public int getSmsSequenceId() {
        return sharedPreferences.getInt(KEY_SMS_SEQUENCE_ID, 1);
    }

    public void setSmsSequenceId(int smsSequenceId) {
        sharedPreferences.edit().putInt(KEY_SMS_SEQUENCE_ID, smsSequenceId).apply();
    }

    public void smsSequenceIdIncrease() {
        setSmsSequenceId((getSmsSequenceId() + 1) % 2000000000);
    }
}
