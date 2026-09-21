package com.app.chillout_delivery.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.app.chillout_delivery.R;

import io.socket.client.Socket;

public class SocketService extends Service {

    private Socket mSocket;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
//        connectSocket();
    }

    private void startForegroundService() {
        String channelId = "socket_service_channel";

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Socket Service",
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle("App Running")
                .setContentText("Listening for updates...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(1, notification);
    }
}