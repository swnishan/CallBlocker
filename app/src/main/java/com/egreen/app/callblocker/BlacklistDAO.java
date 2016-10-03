package com.egreen.app.callblocker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class BlacklistDAO {

    // SQLiteDatabase and DatabaseHelper objects  to access SQLite database
    private SQLiteDatabase database;
    private com.egreen.app.callblocker.DatabaseHelper dbHelper;

    // Constructor initiates the DatabaseHelper to make sure, database creation is done
    public BlacklistDAO(Context context) {
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

    public com.egreen.app.callblocker.Blacklist create(final com.egreen.app.callblocker.Blacklist blackList) {
        // Steps to insert data into db (instead of using raw SQL query)
        // first, Create an object of ContentValues
        final ContentValues values = new ContentValues();

        // second, put the key-value pair into it
        values.put("country_code", blackList.countryCode);
        values.put("phone_number", blackList.phoneNumber);
        values.put("contact_name", blackList.contactName);
        values.put("start_time", blackList.startTime);
        values.put("end_time", blackList.endTime);
        values.put("repeat", blackList.repeat);
        values.put("d1", blackList.d1);
        values.put("d2", blackList.d2);
        values.put("d3", blackList.d3);
        values.put("d4", blackList.d4);
        values.put("d5", blackList.d5);
        values.put("d6", blackList.d6);
        values.put("d7", blackList.d7);
        values.put("miss_call_count", blackList.missCallCount);
        values.put("date", blackList.date);

        // thirst. insert the object into the database
        final long id = database.insert(com.egreen.app.callblocker.DatabaseHelper.TABLE_BLACKLIST, null, values);

        // set the primary key to object and return back
        blackList.id = id;
        return blackList;
    }


    public void delete(final Blacklist blackList) {
        // Way to delete a record from database
        database.delete(DatabaseHelper.TABLE_BLACKLIST, "phone_number = '" + blackList.phoneNumber + "'", null);
    }

    public void plusCallCount(String phoneNo, int count) {
        ContentValues values = new ContentValues();
        values.put("miss_call_count", count);
        System.out.println("increase miss call count");
        try {
            database.update(DatabaseHelper.TABLE_BLACKLIST, values, "phone_number = '" + phoneNo + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int searchPhoneNo(String phoneNo) {
        final Cursor cursor = database.query(DatabaseHelper.TABLE_BLACKLIST, new String[]{"phone_number"}, "phone_number = '" + phoneNo + "'", null, null, null, null);
        return cursor.getCount();
    }

    public void updateBlock(Blacklist blackList){
        final ContentValues values = new ContentValues();

        // second, put the key-value pair into it
        values.put("country_code", blackList.countryCode);
        values.put("contact_name", blackList.contactName);
        values.put("start_time", blackList.startTime);
        values.put("end_time", blackList.endTime);
        values.put("repeat", blackList.repeat);
        values.put("d1", blackList.d1);
        values.put("d2", blackList.d2);
        values.put("d3", blackList.d3);
        values.put("d4", blackList.d4);
        values.put("d5", blackList.d5);
        values.put("d6", blackList.d6);
        values.put("d7", blackList.d7);

        database.update(DatabaseHelper.TABLE_BLACKLIST, values, "phone_number = '" + blackList.phoneNumber + "'", null);

    }


    public List<Blacklist> getAllBlacklist() {
        // Steps to fetch all records from a database table
        // first, create the desired object
        final List<Blacklist> blacklistNumbers = new ArrayList<Blacklist>();

        // second, Query the database and set the result into Cursor
        final Cursor cursor = database.query(DatabaseHelper.TABLE_BLACKLIST, new String[]{"id", "country_code", "phone_number", "contact_name", "start_time", "end_time", "repeat", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "miss_call_count", "date"}, null, null, null, null, null);

        // Move the Cursor pointer to the first
        cursor.moveToFirst();

        //Iterate over the cursor
        while (!cursor.isAfterLast()) {
            final Blacklist number = new Blacklist();

            // Fetch the desired value from the Cursor by column index
            number.id = cursor.getLong(0);
            number.countryCode = cursor.getString(1);
            number.phoneNumber = cursor.getString(2);
            number.contactName = cursor.getString(3);
            number.startTime = cursor.getString(4);
            number.endTime = cursor.getString(5);
            number.repeat = cursor.getInt(6);
            number.d1 = cursor.getInt(7);
            number.d2 = cursor.getInt(8);
            number.d3 = cursor.getInt(9);
            number.d4 = cursor.getInt(10);
            number.d5 = cursor.getInt(11);
            number.d6 = cursor.getInt(12);
            number.d7 = cursor.getInt(13);
            number.missCallCount = cursor.getInt(14);
            number.date = cursor.getString(15);

            // Add the object filled with appropriate data into the list
            blacklistNumbers.add(number);

            // Move the Cursor pointer to next for the next record to fetch
            cursor.moveToNext();
        }
        return blacklistNumbers;
    }
}
