package com.service.notificationsro5;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        int count = intent.getIntExtra("COUNT", 0);


        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent firstIntent = new Intent(this, FirstActivity.class);
        PendingIntent firstPendingIntent = PendingIntent.getActivity(this, 1, firstIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent secondIntent = new Intent(this, SecondActivity.class);
        PendingIntent secondPendingIntent = PendingIntent.getActivity(this, 2, secondIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Сервис работает в фоне и количество кликов: " + count)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(mainPendingIntent)
                .addAction(R.mipmap.ic_launcher, "Активность 1", firstPendingIntent)
                .addAction(R.mipmap.ic_launcher, "Активность 2", secondPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(1, notification);
        }

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyForegroundService", "onDestroy: Фоновый сервис остановлен");
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}