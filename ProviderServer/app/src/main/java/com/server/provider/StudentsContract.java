package com.server.provider;

import android.content.ContentUris;
import android.net.Uri;

public class StudentsContract {
    static final String TABLE_NAME = "Students";
    static final String CONTENT_AUTHORITY = "akzhol.com.server.studentsprovider";
    static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE= "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static class Columns{
        public static final String _ID = "_id";
        public static final String NAME = "Name";
        public static final String STUDENT_GROUP = "Student_group";
        public static final String PHONE = "Phone";

        private Columns(){

        }
    }
    static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    // создает uri с помощью id
    static Uri buildStudentUri(long taskId){
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }
    // получает id из uri
    static long getStudentId(Uri uri){
        return ContentUris.parseId(uri);
    }
}
