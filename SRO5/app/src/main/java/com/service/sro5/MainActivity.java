package com.service.sro5;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MyLogs";
    boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "MainActivity: onServiceConnected");
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public void onClickStart(View view) {
        startService(new Intent(this, MyService.class));
    }
    public void onClickStop(View view) {
        stopService(new Intent(this, MyService.class));
    }

    public void onClickBind(View view) {
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    public void onClickUnbind(View view) {
        if (isBound) {
            unbindService(connection);
            isBound = false;
            Log.d(TAG, "MainActivity: unbindService called");
        }
    }


}