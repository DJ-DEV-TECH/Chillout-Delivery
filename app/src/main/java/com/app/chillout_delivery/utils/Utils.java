package com.app.chillout_delivery.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class Utils {

    public static final String NEW_ORDER_EVENT = "NEW_ORDER";
    public static final String ORDER_UPDATE_EVENT = "UPDATE_ORDER";

    public static String getAuthToken(String token) {
        return "Bearer " + token;
    }

    public static int getOrderId(String orderId) {
        String numberPart = orderId.substring(3);  // remove "ORD"
        return Integer.parseInt(numberPart);
    }

    // ⚠️ Show settings dialog
    public static void showPermissionDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Permission Required")
                .setMessage("Location permission is required to continue. Please enable it in settings.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
