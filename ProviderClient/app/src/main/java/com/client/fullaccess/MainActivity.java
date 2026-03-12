package com.client.fullaccess;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView textStudents;

    static final String TABLE_NAME = "Students";
    static final String CONTENT_AUTHORITY = "akzhol.com.server.studentsprovider";
    static final Uri CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY + "/" + TABLE_NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStudents = findViewById(R.id.textStudents);

        loadStudents();
    }

    private void loadStudents() {
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(
                    CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor == null) {
                textStudents.setText("Cursor = null. Провайдер не найден или доступ запрещен.");
                return;
            }

            if (cursor.getCount() == 0) {
                textStudents.setText("Данные есть, но таблица пустая.");
                return;
            }

            StringBuilder builder = new StringBuilder();

            // Показываем реальные названия колонок
            String[] columnNames = cursor.getColumnNames();
            builder.append("Колонки в Cursor:\n");
            builder.append(Arrays.toString(columnNames));
            builder.append("\n\n");

            while (cursor.moveToNext()) {
                for (String columnName : columnNames) {
                    int index = cursor.getColumnIndex(columnName);
                    String value = cursor.getString(index);

                    builder.append(columnName)
                            .append(" = ")
                            .append(value)
                            .append("\n");
                }
                builder.append("\n----------------------\n\n");
            }

            textStudents.setText(builder.toString());

        } catch (Exception e) {
            textStudents.setText("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}