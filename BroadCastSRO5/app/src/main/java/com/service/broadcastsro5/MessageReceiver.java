package com.service.broadcastsro5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("Testing", "Action received: " + action);

        String message = intent.getStringExtra("com.akzhol.action.broadcast.Message");
        Toast.makeText(context, "Обнаружено сообщение: " + message, Toast.LENGTH_LONG).show();
    }
}
