package com.service.receiversro5;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private MessageReceiver messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.MyTextView); // ID твоего TextView

        // Создаем ресивер и передаем действие для обновления текста
        messageReceiver = new MessageReceiver(message -> {
            textView.setText("Получено: " + message);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Регистрируем ресивер динамически
        IntentFilter filter = new IntentFilter("com.akzhol.action.CAT");
        registerReceiver(messageReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Обязательно отключаем, чтобы не было утечки памяти
        unregisterReceiver(messageReceiver);
    }
}