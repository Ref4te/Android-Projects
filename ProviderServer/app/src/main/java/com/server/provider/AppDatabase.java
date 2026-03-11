package com.server.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Students.db";
    public static final int DATABASE_VERSION = 1;

    private static AppDatabase instance = null;

    private AppDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static AppDatabase getInstance(Context context){
        if(instance == null){
            instance = new AppDatabase(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + StudentsContract.TABLE_NAME + "(" +
                StudentsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                StudentsContract.Columns.NAME + " TEXT NOT NULL, " +
                StudentsContract.Columns.STUDENT_GROUP + " TEXT, " +
                StudentsContract.Columns.PHONE + " TEXT NOT NULL)";
        db.execSQL(sql);

        // добавление начальных данных
        db.execSQL("INSERT INTO "+ StudentsContract.TABLE_NAME +" (" + StudentsContract.Columns.NAME
                + ", " + StudentsContract.Columns.PHONE  + ") VALUES ('Will Smith', '+12345678990');");
        db.execSQL("INSERT INTO "+ StudentsContract.TABLE_NAME +" (" + StudentsContract.Columns.NAME
                + ", " + StudentsContract.Columns.STUDENT_GROUP  + ", " + StudentsContract.Columns.PHONE +
                " ) VALUES ('Манат Акжол', 'ВТиПО-35', '+8777557755');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}