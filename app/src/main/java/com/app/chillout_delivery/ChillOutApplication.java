package com.app.chillout_delivery;

import android.app.Application;

import com.app.chillout_delivery.utils.PrefsHelper;
import com.google.android.gms.maps.MapsInitializer;

public class ChillOutApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // ✅ Initialize Firebase
//        FirebaseApp.initializeApp(this);
        // Initialize PrefsHelper
        PrefsHelper.getInstance(this);
        MapsInitializer.initialize(getApplicationContext());
    }
}
