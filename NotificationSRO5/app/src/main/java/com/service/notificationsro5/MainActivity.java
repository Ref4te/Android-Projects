package com.service.notificationsro5;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnClicker;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClicker = findViewById(R.id.btnClicker);
        btnClicker.setOnClickListener(v -> {
            Clicker();
        });


        Intent intent = new Intent(this, MyService.class);


        findViewById(R.id.btnShowNotif).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        });


        findViewById(R.id.btnStopNotif).setOnClickListener(v -> {
            stopService(intent);
            counter = 0;
        });
    }
    public void Clicker(){
        counter++;

        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("COUNT", counter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}