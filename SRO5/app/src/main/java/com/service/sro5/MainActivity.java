package com.service.sro5;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MyLifecycle";
    boolean isBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Activity: onServiceConnected");
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Activity: onCreate");
    }

    @Override
    protected void onStart() { super.onStart(); Log.d(TAG, "Activity: onStart"); }

    @Override
    protected void onResume() { super.onResume(); Log.d(TAG, "Activity: onResume"); }

    @Override
    protected void onPause() { super.onPause(); Log.d(TAG, "Activity: onPause"); }

    @Override
    protected void onStop() { super.onStop(); Log.d(TAG, "Activity: onStop"); }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Activity: onDestroy");
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        super.onDestroy();
    }

    public void onClickStart(View view) {
        view.setEnabled(false);
        startService(new Intent(this, MyService.class));
        ((Button) view).setText("Сервис запущен");
    }

    public void onClickStop(View view) {
        stopService(new Intent(this, MyService.class));
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setEnabled(true);
        btnStart.setText("Запустить сервис");
    }

    public void onClickBind(View view) {
        bindService(new Intent(this, MyService.class), connection, Context.BIND_AUTO_CREATE);
    }

    public void onClickUnbind(View view) {
        if (isBound) {
            unbindService(connection);
            isBound = false;
            Log.d(TAG, "Activity: unbindService called");
        }
    }
}