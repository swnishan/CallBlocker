package com.egreen.app.callblocker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Model class for database table blacklist
public class MissCallHistory {

    // Two mapping fields for the database table blacklist
    public long id;
    public String phoneNumber;
    public String dateTime;

    // Default constructor
    public MissCallHistory() {

    }

    public MissCallHistory(long id, String phoneNumber, String dateTime) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.dateTime = dateTime;
    }
}
