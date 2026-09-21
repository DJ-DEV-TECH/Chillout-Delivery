package com.app.chillout_delivery.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("FCM_NEW_TOKEN", token);
        // Send updated token to server
    }

}
