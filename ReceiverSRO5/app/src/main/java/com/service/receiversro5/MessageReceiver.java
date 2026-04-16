package com.service.receiversro5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MessageReceiver extends BroadcastReceiver {

    private MessageListener listener;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    public MessageReceiver() {}

    public MessageReceiver(MessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("com.akzhol.action.broadcast.Message");
        if (listener != null) {
            listener.onMessageReceived(message);
        }
    }
}