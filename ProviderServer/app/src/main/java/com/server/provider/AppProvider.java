package com.server.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppProvider extends ContentProvider {

    private AppDatabase mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int STUDENTS = 100;
    public static final int STUDENT_ID = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // content://akzhol.com.server.studentsprovider";/STUDENTS
        matcher.addURI(StudentsContract.CONTENT_AUTHORITY, StudentsContract.TABLE_NAME, STUDENTS);
        // content://akzhol.com.server.studentsprovider";/STUDENTS/8
        matcher.addURI(StudentsContract.CONTENT_AUTHORITY, StudentsContract.TABLE_NAME + "/#", STUDENT_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int match = sUriMatcher.match(uri);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch(match){
            case STUDENTS:
                queryBuilder.setTables(StudentsContract.TABLE_NAME);
                break;
            case STUDENT_ID:
                queryBuilder.setTables(StudentsContract.TABLE_NAME);
                long studentId = StudentsContract.getStudentId(uri);
                queryBuilder.appendWhere(StudentsContract.Columns._ID + " = " + studentId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+ uri);
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch(match){
            case STUDENTS:
                return StudentsContract.CONTENT_TYPE;
            case STUDENT_ID:
                return StudentsContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: "+ uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;

        if (match == STUDENTS) {
            db = mOpenHelper.getWritableDatabase();
            recordId = db.insert(StudentsContract.TABLE_NAME, null, values);
            if (recordId > 0) {
                returnUri = StudentsContract.buildStudentUri(recordId);
            } else {
                throw new SQLException("Failed to insert: " + uri.toString());
            }
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        String selectionCriteria = selection;

        if(match != STUDENTS && match != STUDENT_ID)
            throw new IllegalArgumentException("Unknown URI: "+ uri);

        if(match==STUDENT_ID) {
            long studentId = StudentsContract.getStudentId(uri);
            selectionCriteria = StudentsContract.Columns._ID + " = " + studentId;
            if ((selection != null) && (selection.length() > 0)) {
                selectionCriteria += " AND (" + selection + ")";
            }
        }
        return db.delete(StudentsContract.TABLE_NAME, selectionCriteria, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String selectionCriteria = selection;

        if(match != STUDENTS && match != STUDENT_ID)
            throw new IllegalArgumentException("Unknown URI: "+ uri);

        if(match==STUDENT_ID) {
            long studentId = StudentsContract.getStudentId(uri);
            selectionCriteria = StudentsContract.Columns._ID + " = " + studentId;
            if ((selection != null) && (selection.length() > 0)) {
                selectionCriteria += " AND (" + selection + ")";
            }
        }
        return db.update(StudentsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
    }
}