package com.egreen.app.callblocker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Define the SQLite database name
    private static final String DATABASE_NAME = "call_blocker.db";

    // Define the SQLite database version
    private static final int DATABASE_VERSION = 1;

    // Define the SQLite Table name to create
    public static final String TABLE_BLACKLIST = "blacklist";
    public static final String MISSED_CALL_HISTORY = "missedcallhistory";

    // Table creation SQL statement
    private static final String TABLE_BLACKLIST_CREATE = "create table " + TABLE_BLACKLIST + "( id "
            + " integer primary key autoincrement,country_code  text not null, phone_number  text not null,  contact_name  text ,start_time  text, end_time  text, repeat  integer, d1  integer, d2  integer, d3  integer, d4  integer, d5  integer, d6  integer, d7  integer,miss_call_count  integer,date  text" +
            ");";

    private static final String MISSED_CALL_HISTORY_CREATE = "create table " + MISSED_CALL_HISTORY + "( id "
            + " integer primary key autoincrement, phone_no text not null,date_time text" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This method will execute once in the application entire life cycle
    // All table creation code should put here
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_BLACKLIST_CREATE);
        db.execSQL(MISSED_CALL_HISTORY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

}
