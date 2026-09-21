package com.app.chillout_delivery;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.app.chillout_delivery.model.UserResponseModel;
import com.app.chillout_delivery.utils.PrefsHelper;
import com.app.chillout_delivery.utils.SocketManager;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class ChillOutApplication extends Application {

    private static ChillOutApplication instance;
    public MediaPlayer mediaPlayer;
    public long userId = 0;
    PrefsHelper prefsHelper;
    public static String FCM_TOKEN = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // ✅ Initialize Firebase
        FirebaseApp.initializeApp(this);
        // Initialize PrefsHelper
        PrefsHelper.getInstance(this);
        MapsInitializer.initialize(getApplicationContext());
        // Initialize PrefsHelper
        prefsHelper = PrefsHelper.getInstance(this);
        getFCMToken();
        getUserDetails();
    }

    public static ChillOutApplication getInstance() {
        return instance;
    }

    public UserResponseModel.Data getUserDetails() {
        return prefsHelper.getUser();
    }

    public long getUserId() {
        if (prefsHelper.getUser() != null)
            return prefsHelper.getUser().getDeliveryBoyId();
        else return 0;
    }

    public String getUserName() {
        if (prefsHelper.getUser() != null)
            return prefsHelper.getUser().getUsername();
        else return "";
    }

    public String getMobile() {
        if (prefsHelper.getUser() != null)
            return prefsHelper.getUser().getMobile();
        else return "";
    }

    public String getEmail() {
        if (prefsHelper.getUser() != null)
            return prefsHelper.getUser().getEmail();
        else return "";
    }

    public String getAuthToken() {
        if (prefsHelper.getUser() != null)
            return prefsHelper.getUser().getAuthToken();
        else return "";
    }

    public int getStatus() {
        if (prefsHelper.getUser() != null)
            return prefsHelper.getUser().getStatus();
        else return 0;
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM_TOKEN", "Fetching FCM token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    FCM_TOKEN = task.getResult();
                    System.out.println("Check_JK Token : "+FCM_TOKEN);
                    Log.e("Check_JK FCM_TOKEN", FCM_TOKEN);
                    // TODO: Send this token to your server
                });
    }
}
