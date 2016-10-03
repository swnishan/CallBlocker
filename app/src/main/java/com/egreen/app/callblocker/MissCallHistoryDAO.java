package com.egreen.app.callblocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;


public class MissCallHistoryDAO {

    // SQLiteDatabase and DatabaseHelper objects  to access SQLite database
    private SQLiteDatabase database;
    private com.egreen.app.callblocker.DatabaseHelper dbHelper;

    // Constructor initiates the DatabaseHelper to make sure, database creation is done
    public MissCallHistoryDAO(Context context) {
        dbHelper = new com.egreen.app.callblocker.DatabaseHelper(context);
        open();
    }

    private void open() throws SQLException {
        // Opens the database connection to provide the access
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        // Close it, once done
        dbHelper.close();
    }

    public com.egreen.app.callblocker.MissCallHistory create(final com.egreen.app.callblocker.MissCallHistory missCallHistory) {
        // Steps to insert data into db (instead of using raw SQL query)
        // first, Create an object of ContentValues
        final ContentValues values = new ContentValues();

        // second, put the key-value pair into it
        values.put("phone_no", missCallHistory.phoneNumber);
        values.put("date_time", missCallHistory.dateTime);


        // thirst. insert the object into the database
        final long id = database.insert(com.egreen.app.callblocker.DatabaseHelper.MISSED_CALL_HISTORY, null, values);

        // set the primary key to object and return back
        missCallHistory.id = id;
        return missCallHistory;
    }


    public void delete(final com.egreen.app.callblocker.MissCallHistory missCallHistory) {
        // Way to delete a record from database
        database.delete(com.egreen.app.callblocker.DatabaseHelper.MISSED_CALL_HISTORY, "phone_no = '" + missCallHistory.phoneNumber + "'", null);
    }


    public List<com.egreen.app.callblocker.MissCallHistory> getAllMissCallList() {
        // Steps to fetch all records from a database table
        // first, create the desired object
        final List<com.egreen.app.callblocker.MissCallHistory> missCallHistoryArrayList = new ArrayList<>();

        // second, Query the database and set the result into Cursor
        final Cursor cursor = database.query(com.egreen.app.callblocker.DatabaseHelper.MISSED_CALL_HISTORY, new String[]{"id", "phone_no", "date_time"}, null, null, null, null, null);

        // Move the Cursor pointer to the first
        cursor.moveToFirst();

        //Iterate over the cursor
        while (!cursor.isAfterLast()) {
            final com.egreen.app.callblocker.MissCallHistory missCallHistory = new com.egreen.app.callblocker.MissCallHistory();

            // Fetch the desired value from the Cursor by column index
            missCallHistory.id = cursor.getLong(0);
            missCallHistory.phoneNumber = cursor.getString(1);
            missCallHistory.dateTime = cursor.getString(2);


            // Add the object filled with appropriate data into the list
            missCallHistoryArrayList.add(missCallHistory);

            // Move the Cursor pointer to next for the next record to fetch
            cursor.moveToNext();
        }
        return missCallHistoryArrayList;
    }
}
