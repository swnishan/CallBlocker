package com.egreen.app.callblocker;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Model class for database table blacklist
public class Blacklist implements Serializable {

    // Two mapping fields for the database table blacklist
    public long id;
    public String countryCode;
    public String phoneNumber;
    public String contactName;
    public String startTime;
    public String endTime;
    public int repeat;
    public int d1;
    public int d2;
    public int d3;
    public int d4;
    public int d5;
    public int d6;
    public int d7;
    public int missCallCount;
    public String date;

    // Default constructor
    public Blacklist() {

    }

    // To easily create Blacklist object, an alternative constructor
    public Blacklist(final String phoneMumber) {
        this.phoneNumber = phoneMumber;
    }

    public Blacklist(String countryCode, String phoneNumber) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
    }

    public Blacklist(String countryCode, String phoneNumber, String startTime, String endTime) {
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Blacklist(long id, String countryCode, String phoneNumber, String startTime, String endTime, int repeat, int d1, int d2, int d3, int d4, int d5, int d6, int d7) {
        this.id = id;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
        this.d5 = d5;
        this.d6 = d6;
        this.d7 = d7;
    }

    public Blacklist(long id, String countryCode, String phoneNumber, String contactName, String startTime, String endTime, int repeat, int d1, int d2, int d3, int d4, int d5, int d6, int d7, int missCallCount, String date) {
        this.id = id;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
        this.d5 = d5;
        this.d6 = d6;
        this.d7 = d7;
        this.missCallCount = missCallCount;
        this.date = date;
    }

    public Blacklist(long id, String countryCode, String phoneNumber, String startTime, String endTime, int repeat, int d1, int d2, int d3, int d4, int d5, int d6, int d7, int missCallCount) {
        this.id = id;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
        this.d5 = d5;
        this.d6 = d6;
        this.d7 = d7;
        this.missCallCount = missCallCount;
    }

    public Blacklist(long id, String countryCode, String phoneNumber, String startTime, String endTime, int repeat, int d1, int d2, int d3, int d4, int d5, int d6, int d7, int missCallCount, String date) {
        this.id = id;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.d4 = d4;
        this.d5 = d5;
        this.d6 = d6;
        this.d7 = d7;
        this.missCallCount = missCallCount;
        this.date = date;
    }

    // Overriding the default method to compare between the two objects bu phone number
    @Override
    public boolean equals(final Object obj) {

        // If passed object is an instance of Blacklist, then compare the phone numbers, else return false as they are not equal
        if (obj.getClass().isInstance(new Blacklist())) {
            // Cast the object to Blacklist
            final Blacklist bl = (Blacklist) obj;
            System.out.println("...............................................................");
            System.out.println("id :" + bl.id);
            System.out.println("cc :" + bl.countryCode);
            System.out.println("no :" + bl.phoneNumber);
            System.out.println("name :" + bl.contactName);
            System.out.println("stime :" + bl.startTime);
            System.out.println("etime :" + bl.endTime);
            System.out.println("repeat :" + bl.repeat);
            System.out.println("sun :" + bl.d1);
            System.out.println("mon :" + bl.d2);
            System.out.println("tue :" + bl.d3);
            System.out.println("wen :" + bl.d4);
            System.out.println("thu :" + bl.d5);
            System.out.println("fri :" + bl.d6);
            System.out.println("sat :" + bl.d7);
            System.out.println("count :" + bl.missCallCount);
            System.out.println("date :" + bl.date);

            try {
                SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd");
                String newtime = sdfDateTime.format(new Date(System.currentTimeMillis()));
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(newtime);

                if (bl.repeat == 0) {
                    return checkBlockNo(bl);
                } else {
                    System.out.println(date.getDay());
                    System.out.println("Repeat");

                    switch (date.getDay()) {
                        case 1:
                            if (bl.d1 == 1) {
                                System.out.println("date 1");
                                return checkBlockNo(bl);
                            }
                            return false;
                        case 2:
                            if (bl.d2 == 1) {
                                System.out.println("date 2");
                                return checkBlockNo(bl);
                            }
                            return false;

                        case 3:
                            if (bl.d3 == 1) {
                                System.out.println("date 3");
                                return checkBlockNo(bl);
                            }
                            return false;

                        case 4:
                            if (bl.d4 == 1) {
                                System.out.println("date 4");
                                return checkBlockNo(bl);
                            }
                            return false;

                        case 5:
                            if (bl.d5 == 1) {
                                System.out.println("date 5");
                                return checkBlockNo(bl);
                            }
                            return false;

                        case 6:
                            if (bl.d6 == 1) {
                                System.out.println("date 6");
                                return checkBlockNo(bl);
                            }
                            return false;

                        case 7:
                            if (bl.d7 == 1) {
                                System.out.println("date 7");
                                return checkBlockNo(bl);
                            }
                            return false;

                    }
                }
                System.out.println("..............................................");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    boolean checkBlockNo(Blacklist bl) {


        if (this.phoneNumber.contains(" "))
            this.phoneNumber = this.phoneNumber.replace(" ", "");


        if (bl.phoneNumber.equals(this.phoneNumber)) {
            System.out.println("number ........................................");
            if (bl.endTime == null) {
                System.out.println("end time null");
                return checkStartTime(bl.startTime);
            } else {
                System.out.println("end time not null");
                return checkStartTime(bl.startTime) && checkEndTime(bl.endTime);
            }


        } else if ((bl.countryCode + bl.phoneNumber).equals(this.phoneNumber)) {
            System.out.println("countrycode number ........................................");
            if (bl.endTime == null) {
                return checkStartTime(bl.startTime);
            } else {
                return checkStartTime(bl.startTime) && checkEndTime(bl.endTime);
            }

        } else if (("0" + bl.phoneNumber).equals(this.phoneNumber)) {
            System.out.println("0 number ........................................");
            if (bl.endTime == null) {
                return checkStartTime(bl.startTime);
            } else {
                return checkStartTime(bl.startTime) && checkEndTime(bl.endTime);
            }

        } else {
            return false;
        }
    }

    boolean checkStartTime(String stTime) {
        try {

            String string1 = stTime;
            System.out.println("Start Time :" + stTime);
            Date time1 = new SimpleDateFormat("hh:mm aa").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            System.out.println("Start Time :" + time1.toString());

            SimpleDateFormat sdfDateTime = new SimpleDateFormat("hh:mm aa");
            String newtime = sdfDateTime.format(new Date(System.currentTimeMillis()));
            Date date = new SimpleDateFormat("hh:mm aa").parse(newtime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(date);
            System.out.println("current Time :" + date.toString());
            if (calendar3.getTime().after(calendar1.getTime())) {
                System.out.println(true);
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(false);
        return false;
    }

    boolean checkEndTime(String endTime) {
        try {


            String string2 = endTime;
            System.out.println("Start Time :" + endTime);
            Date time2 = new SimpleDateFormat("hh:mm aa").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            System.out.println("End Time :" + time2.toString());

            SimpleDateFormat sdfDateTime = new SimpleDateFormat("hh:mm aa");
            String newtime = sdfDateTime.format(new Date(System.currentTimeMillis()));
            Date date = new SimpleDateFormat("hh:mm aa").parse(newtime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(date);
            System.out.println("current Time :" + date.toString());
            if (calendar3.getTime().before(calendar2.getTime())) {
                System.out.println(true);
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(false);
        return false;
    }
}
