package com.server.provider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button refreshButton;
    AppDatabase appDatabase;
    CardAdapter adapter;
    ArrayList<Student> fullStudentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshButton = findViewById(R.id.refreshButton);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewStudents);

        appDatabase = AppDatabase.getInstance(this);
        adapter = new CardAdapter(fullStudentList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refreshButton.setOnClickListener(v -> loadStudents());
        loadStudents();
    }

    private void loadStudents() {
        fullStudentList.clear();
        SQLiteDatabase db = appDatabase.getReadableDatabase();

        Cursor cursor = db.query(StudentsContract.TABLE_NAME, null, null, null, null, null, StudentsContract.Columns._ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(StudentsContract.Columns._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(StudentsContract.Columns.NAME));
                String group = cursor.getString(cursor.getColumnIndexOrThrow(StudentsContract.Columns.STUDENT_GROUP));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(StudentsContract.Columns.PHONE));
                fullStudentList.add(new Student(id, name, group, phone));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}