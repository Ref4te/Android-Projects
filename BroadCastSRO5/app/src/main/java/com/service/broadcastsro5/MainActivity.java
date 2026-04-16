package com.service.broadcastsro5;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    final String TAG = "Testing";

    public static final String WHERE_MY_CAT_ACTION = "com.akzhol.action.CAT";
    public static final String ALARM_MESSAGE = "Срочно пришлите кота!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void sendMessage(View view) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(this, MessageReceiver.class);
        intent.setAction(WHERE_MY_CAT_ACTION);
        intent.putExtra("com.akzhol.action.broadcast.Message", ALARM_MESSAGE);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(componentName);

        sendBroadcast(intent);
        Log.d(TAG, "Broadcast sent");
    }
}
