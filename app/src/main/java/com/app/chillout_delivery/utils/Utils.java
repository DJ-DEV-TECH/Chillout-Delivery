package com.app.chillout_delivery.utils;

public class Utils {

    public static String getAuthToken(String token) {
        return "Bearer " + token;
    }
}
