package com.service.sro5;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    final String TAG = "MyService";
    private boolean isRunning = false;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        MyService getService() { return MyService.this; }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service: onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service: onStartCommand");

        if (!isRunning) {
            isRunning = true;
            new Thread(() -> {
                while (isRunning) {
                    try {
                        Log.d(TAG, "Service: выполняет фоновую работу...");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service: onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service: onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service: onDestroy");
        isRunning = false;
        super.onDestroy();
    }
}