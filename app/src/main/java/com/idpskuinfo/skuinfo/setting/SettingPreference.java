package com.idpskuinfo.skuinfo.setting;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingPreference {
    private static final String PREFS_NAME = "setting_pref";

    private static final String HOSTNAME = "hostname";
    private static final String PORT = "port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private final SharedPreferences preferences;

    public SettingPreference(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setSetting(SettingModel value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(HOSTNAME, value.HostName);
        editor.putString(PORT, value.Port);
        editor.putString(USERNAME, value.UserName);
        editor.putString(PASSWORD, value.Password);
        editor.apply();
    }

    public SettingModel getSetting() {
        SettingModel model = new SettingModel();
        model.setHostName(preferences.getString(HOSTNAME, ""));
        model.setPort(preferences.getString(PORT, ""));
        model.setUserName(preferences.getString(USERNAME, ""));
        model.setPassword(preferences.getString(PASSWORD, ""));
        return model;
    }
}
