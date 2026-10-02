package com.app.chillout_delivery.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.chillout_delivery.R;
import com.app.chillout_delivery.activity.HomeActivity;
import com.app.chillout_delivery.activity.OrderDetailsActivity;
import com.app.chillout_delivery.model.EventModel;
import com.app.chillout_delivery.model.OrderModel;
import com.app.chillout_delivery.utils.EventManager;
import com.app.chillout_delivery.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Objects;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "order_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("FCM_NEW_TOKEN", token);
        // Send updated token to server
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String orderId = "";
        if (remoteMessage.getData().isEmpty()) {
            return;
        }
        String payload = remoteMessage.getData().get("payload");
        String type = remoteMessage.getData().get("type");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        if (remoteMessage.getData().containsKey("orderId"))
            orderId = (remoteMessage.getData().get("orderId") == null || Objects.equals(remoteMessage.getData().get("orderId"), "")) ? "" : remoteMessage.getData().get("orderId");
        if (payload != null) {
            if (Utils.NEW_ORDER_EVENT.equals(type)) {
                setOrderData(Utils.NEW_ORDER_EVENT, payload);
                showOrderNotification(title, body, orderId);
            }
            if (Utils.ORDER_UPDATE_EVENT.equals(type)) {
                setOrderData(Utils.ORDER_UPDATE_EVENT, payload);
                showOrderUpdateNotification(title, body, orderId);
            }
        }
    }

    private void setOrderData(String type, String data) {
        OrderModel orderModel = new Gson().fromJson(data, OrderModel.class);
        EventModel eventModel = new EventModel();
        eventModel.setOrderId(orderModel.getOrderId());
        eventModel.setStatus(orderModel.getOrderStatus());
        eventModel.setType(type);
        eventModel.setData(data);
        EventManager.getInstance().sendEvent(eventModel);
    }

    // ✅ NEW ORDER NOTIFICATION (with sound)
    private void showOrderNotification(String title, String body, String orderId) {
        int notificationId = orderId != null ? orderId.hashCode() : (int) System.currentTimeMillis();
        createChannel();
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_order);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("orderId", orderId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(soundUri);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }

    // ✅ NEW ORDER NOTIFICATION (without sound)
    private void showOrderUpdateNotification(String title, String body, String orderId) {
        int notificationId = orderId != null ? orderId.hashCode() : (int) System.currentTimeMillis();
        createChannel();
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("orderId", orderId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }

    // ✅ CREATE CHANNEL (VERY IMPORTANT for Android 8+)
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_order);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Notifications for new orders");
            channel.setSound(soundUri, audioAttributes);
            channel.enableLights(true);
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

}
