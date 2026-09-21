package com.app.chillout_delivery.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.chillout_delivery.model.UserResponseModel;
import com.google.gson.Gson;

public class PrefsHelper {

    private static final String PREF_NAME = "ChillOutDelivery";
    private static PrefsHelper instance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public PrefsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PrefsHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PrefsHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static void clearAll(Context context) {
        editor.clear();
        editor.apply();
    }

    public void saveUser(UserResponseModel.Data model) {
        String json = new Gson().toJson(model); // 🔥 convert object to JSON
        editor.putString("USER_DATA", json);
        editor.apply();
    }

    public UserResponseModel.Data getUser() {
        String json = sharedPreferences.getString("USER_DATA", null);
        if (json != null) {
            return new Gson().fromJson(json, UserResponseModel.Data.class);
        }
        return null;
    }

    public void updateUserStatus(int status) {
        UserResponseModel.Data user = getUser();
        if (user != null) {
            user.setStatus(status);
            saveUser(user); // 🔥 reuse existing method
        }
    }
}