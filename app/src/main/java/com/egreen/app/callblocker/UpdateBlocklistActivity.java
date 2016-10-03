package com.egreen.app.callblocker;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.egreen.app.callblocker.R;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mirko.android.datetimepicker.time.RadialPickerLayout;
import mirko.android.datetimepicker.time.TimePickerDialog;

public class UpdateBlocklistActivity extends ActionBarActivity implements OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = UpdateBlocklistActivity.class.getName();
    private static final int REQUEST_CONTACT_NUMBER = 1234;
    private AutoCompleteTextView country_code_auto, phone_auto;
    private Button btnShowContactList;
    private BootstrapButton cancel_btn, update_btn;
    private TextView tvDisplayStartTime, tvDisplayEndTime, tvChangeStartTime, tvChangeEndTime, tvChangeCountryCode, txtRepeat;
    private CheckBox checkBoxRepeat, checkBoxEndTime;
    private LinearLayout layDatePicker;
    private BootstrapButton d1, d2, d3, d4, d5, d6, d7;
    int wd1 = 0, wd2 = 0, wd3 = 0, wd4 = 0, wd5 = 0, wd6 = 0, wd7 = 0;
    ArrayList<ContactNoWithName> listContact;
    ArrayList<ContryWithCode> contryWithCodes;
    private Blacklist blackListNo;

    // Declaration of BlacklistDAO to interact with SQlite database
    private BlacklistDAO blackListDao;

    private final Calendar mCalendar = Calendar.getInstance();

    private int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);

    private int minute = mCalendar.get(Calendar.MINUTE);

    private int day = mCalendar.get(Calendar.DAY_OF_MONTH);

    private int month = mCalendar.get(Calendar.MONTH);

    private int year = mCalendar.get(Calendar.YEAR);

    private String tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_blocklist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Initialization of the DAO object.
        blackListDao = new BlacklistDAO(this);


        country_code_auto = (AutoCompleteTextView) findViewById(R.id.country_code_auto);
        phone_auto = (AutoCompleteTextView) findViewById(R.id.phone_auto);

        tvDisplayStartTime = (TextView) findViewById(R.id.tvDisplayStartTime);
        tvDisplayEndTime = (TextView) findViewById(R.id.tvDisplayEndTime);
        tvChangeStartTime = (TextView) findViewById(R.id.tvChangeStartTime);
        tvChangeEndTime = (TextView) findViewById(R.id.tvChangeEndTime);
        tvChangeCountryCode = (TextView) findViewById(R.id.tvChangeCountryCode);
        txtRepeat = (TextView) findViewById(R.id.txtRepeat);


        cancel_btn = (BootstrapButton) findViewById(R.id.cancel_btn);
        update_btn = (BootstrapButton) findViewById(R.id.update_btn);
        btnShowContactList = (Button) findViewById(R.id.btnShowContactList);

        cancel_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        tvChangeStartTime.setOnClickListener(this);
        tvChangeEndTime.setOnClickListener(this);
        btnShowContactList.setOnClickListener(this);
        tvChangeCountryCode.setOnClickListener(this);

        d1 = (BootstrapButton) findViewById(R.id.d1);
        d2 = (BootstrapButton) findViewById(R.id.d2);
        d3 = (BootstrapButton) findViewById(R.id.d3);
        d4 = (BootstrapButton) findViewById(R.id.d4);
        d5 = (BootstrapButton) findViewById(R.id.d5);
        d6 = (BootstrapButton) findViewById(R.id.d6);
        d7 = (BootstrapButton) findViewById(R.id.d7);

        checkBoxRepeat = (CheckBox) findViewById(R.id.checkBoxRepeat);
        checkBoxEndTime = (CheckBox) findViewById(R.id.checkBoxEndTime);

        layDatePicker = (LinearLayout) findViewById(R.id.layDatePicker);

        contryWithCodes = new ArrayList<>();
        loadCountries();

        SimpleDateFormat sdfDateTime = new SimpleDateFormat("hh:mm aa");
        String newtime = sdfDateTime.format(new Date(System.currentTimeMillis()));
        tvDisplayStartTime.setText(newtime);
        tvDisplayEndTime.setText(newtime);

        listContact = new ArrayList<>();
        loadContactList();
        ArrayAdapter contactAdapter = new ArrayAdapter(UpdateBlocklistActivity.this, android.R.layout.simple_list_item_1, listContact);
        phone_auto.setAdapter(contactAdapter);
        phone_auto.setOnItemClickListener(this);

        blackListNo = (Blacklist) getIntent().getSerializableExtra("bundle");
        if (blackListNo != null) {
            country_code_auto.setText(blackListNo.countryCode.replace("+", ""));
            if (blackListNo.contactName != null)
                phone_auto.setText(blackListNo.contactName);
            else
                phone_auto.setText(blackListNo.phoneNumber);

            phone_auto.setFocusable(false);
            phone_auto.setCursorVisible(false);

            tvDisplayStartTime.setText(blackListNo.startTime);

            if (blackListNo.endTime != null) {
                tvDisplayEndTime.setText(blackListNo.endTime);
                tvDisplayEndTime.setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
                checkBoxEndTime.setChecked(true);
            }
            if (blackListNo.repeat == 1) {
                checkBoxRepeat.setEnabled(true);
                checkBoxRepeat.setChecked(true);
                markSelectedRepeatDays(blackListNo);
            }

        }

        if (checkBoxRepeat.isChecked()) {
            checkBoxRepeat.setEnabled(true);
            layDatePicker.setVisibility(View.VISIBLE);
        } else {
            checkBoxRepeat.setEnabled(false);
            layDatePicker.setVisibility(View.INVISIBLE);
            unselectDays();
        }

        if (checkBoxEndTime.isChecked()) {
            checkBoxRepeat.setEnabled(true);
            txtRepeat.setTextColor(getResources().getColor(R.color.black));
        } else {
            checkBoxRepeat.setEnabled(false);
            txtRepeat.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        checkBoxRepeat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxRepeat.isChecked()) {
                    layDatePicker.setVisibility(View.VISIBLE);
                    selectAllDays();
                } else {
                    layDatePicker.setVisibility(View.INVISIBLE);
                    unselectDays();
                }
            }
        });

        checkBoxEndTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxEndTime.isChecked()) {
                    checkBoxRepeat.setEnabled(true);
                    txtRepeat.setTextColor(getResources().getColor(R.color.black));
                } else {
                    checkBoxRepeat.setEnabled(false);
                    txtRepeat.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        });
    }

    private void markSelectedRepeatDays(Blacklist blackListNo) {
        if (blackListNo.d1 == 1) {
            d1.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
            d1.setTextColor(getResources().getColor(R.color.white));
            wd1 = 1;
        } else {
            d1.setBackgroundColor(getResources().getColor(R.color.white));
            d1.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
            wd1 = 0;
        }
        if (blackListNo.d2 == 1) {
            d2.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
            d2.setTextColor(getResources().getColor(R.color.white));
            wd2 = 1;
        } else {
            d2.setBackgroundColor(getResources().getColor(R.color.white));
            d2.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
            wd2 = 0;
        }
        if (blackListNo.d3 == 1) {
            d3.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
            d3.setTextColor(getResources().getColor(R.color.white));
            wd3 = 1;
        } else {
            d3.setBackgroundColor(getResources().getColor(R.color.white));
            d3.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
            wd3 = 0;
        }
        if (blackListNo.d4 == 1) {
            d4.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
            d4.setTextColor(getResources().getColor(R.color.white));
            wd4 = 1;
        } else {
            d4.setBackgroundColor(getResources().getColor(R.color.white));
            d4.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
            wd4 = 0;
        }
        if (blackListNo.d5 == 1) {
            d5.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
            d5.setTextColor(getResources().getColor(R.color.white));
            wd5 = 1;
        } else {
            d5.setBackgroundColor(getResources().getColor(R.color.white));
            d5.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
            wd5 = 0;
        }
        if (blackListNo.d6 == 1) {
            d6.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
            d6.setTextColor(getResources().getColor(R.color.white));
            wd6 = 1;
        } else {
            d6.setBackgroundColor(getResources().getColor(R.color.white));
            d6.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
            wd6 = 0;
        }
        if (blackListNo.d7 == 1) {
            d7.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
            d7.setTextColor(getResources().getColor(R.color.white));
            wd7 = 1;
        } else {
            d7.setBackgroundColor(getResources().getColor(R.color.white));
            d7.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
            wd7 = 0;
        }
    }

    private void loadCountries() {
        for (int i = 0; i < CountryCodes.m_Codes.length; i++) {
            contryWithCodes.add(new ContryWithCode(CountryCodes.m_Countries[i], CountryCodes.m_Codes[i]));
        }
    }


    private void loadContactList() {
        Cursor cursor = null;
        try {
            cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            int contactIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
            int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);
            cursor.moveToFirst();
            do {
                String idContact = cursor.getString(contactIdIdx);
                String name = cursor.getString(nameIdx);
                String phoneNumber = cursor.getString(phoneNumberIdx);
                listContact.add(new ContactNoWithName(name, phoneNumber));
            } while (cursor.moveToNext());
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void selectAllDays() {
        d1.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
        d1.setTextColor(getResources().getColor(R.color.white));
        wd1 = 1;
        d2.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
        d2.setTextColor(getResources().getColor(R.color.white));
        wd2 = 1;
        d3.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
        d3.setTextColor(getResources().getColor(R.color.white));
        wd3 = 1;
        d4.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
        d4.setTextColor(getResources().getColor(R.color.white));
        wd4 = 1;
        d5.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
        d5.setTextColor(getResources().getColor(R.color.white));
        wd5 = 1;
        d6.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
        d6.setTextColor(getResources().getColor(R.color.white));
        wd6 = 1;
        d7.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
        d7.setTextColor(getResources().getColor(R.color.white));
        wd7 = 1;
    }

    public void onDaySelect(View v) {
        if (v == d1) {
            if (wd1 == 0) {
                d1.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
                d1.setTextColor(getResources().getColor(R.color.white));
                wd1 = 1;
            } else {
                d1.setBackgroundColor(getResources().getColor(R.color.white));
                d1.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
                wd1 = 0;
            }
        } else if (v == d2) {
            if (wd2 == 0) {
                d2.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
                d2.setTextColor(getResources().getColor(R.color.white));
                wd2 = 1;
            } else {
                d2.setBackgroundColor(getResources().getColor(R.color.white));
                d2.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
                wd2 = 0;
            }

        } else if (v == d3) {
            if (wd3 == 0) {
                d3.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
                d3.setTextColor(getResources().getColor(R.color.white));
                wd3 = 1;
            } else {
                d3.setBackgroundColor(getResources().getColor(R.color.white));
                d3.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
                wd3 = 0;
            }
        } else if (v == d4) {
            if (wd4 == 0) {
                d4.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
                d4.setTextColor(getResources().getColor(R.color.white));
                wd4 = 1;
            } else {
                d4.setBackgroundColor(getResources().getColor(R.color.white));
                d4.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
                wd4 = 0;
            }

        } else if (v == d5) {
            if (wd5 == 0) {
                d5.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
                d5.setTextColor(getResources().getColor(R.color.white));
                wd5 = 1;
            } else {
                d5.setBackgroundColor(getResources().getColor(R.color.white));
                d5.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
                wd5 = 0;
            }

        } else if (v == d6) {
            if (wd6 == 0) {
                d6.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
                d6.setTextColor(getResources().getColor(R.color.white));
                wd6 = 1;
            } else {
                d6.setBackgroundColor(getResources().getColor(R.color.white));
                d6.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
                wd6 = 0;
            }

        } else if (v == d7) {
            if (wd7 == 0) {
                d7.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_info));
                d7.setTextColor(getResources().getColor(R.color.white));
                wd7 = 1;
            } else {
                d7.setBackgroundColor(getResources().getColor(R.color.white));
                d7.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
                wd7 = 0;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == update_btn) {
            // All input fields are mandatory, so made a check
            if (country_code_auto.getText().toString().trim().length() > 0 && phone_auto.getText().toString().trim().length() > 0) {

                // Then, set all the values from user input
                String phoneNo;
                if (blackListNo.phoneNumber != null)
                    phoneNo = blackListNo.phoneNumber;
                else
                    phoneNo = phone_auto.getText().toString();

                if (phoneNo.contains(" ")) {
                    phoneNo = phoneNo.replace(" ", "");
                }


                blackListNo.countryCode = "+" + country_code_auto.getText().toString();
                blackListNo.phoneNumber = phoneNo;
                blackListNo.startTime = tvDisplayStartTime.getText().toString();
                if (checkBoxEndTime.isChecked())
                    blackListNo.endTime = tvDisplayEndTime.getText().toString();
                else
                    blackListNo.endTime = null;

                if (checkBoxRepeat.isChecked()) {
                    blackListNo.repeat = 1;
                    blackListNo.d1 = wd1;
                    blackListNo.d2 = wd2;
                    blackListNo.d3 = wd3;
                    blackListNo.d4 = wd4;
                    blackListNo.d5 = wd5;
                    blackListNo.d6 = wd6;
                    blackListNo.d7 = wd7;
                } else {
                    blackListNo.repeat = 0;
                    blackListNo.d1 = 0;
                    blackListNo.d2 = 0;
                    blackListNo.d3 = 0;
                    blackListNo.d4 = 0;
                    blackListNo.d5 = 0;
                    blackListNo.d6 = 0;
                    blackListNo.d7 = 0;
                }

                System.out.println("...............................................................");
                System.out.println("id :" + blackListNo.id);
                System.out.println("cc :" + blackListNo.countryCode);
                System.out.println("no :" + blackListNo.phoneNumber);
                System.out.println("name :" + blackListNo.contactName);
                System.out.println("stime :" + blackListNo.startTime);
                System.out.println("etime :" + blackListNo.endTime);
                System.out.println("repeat :" + blackListNo.repeat);
                System.out.println("sun :" + blackListNo.d1);
                System.out.println("mon :" + blackListNo.d2);
                System.out.println("tue :" + blackListNo.d3);
                System.out.println("wen :" + blackListNo.d4);
                System.out.println("thu :" + blackListNo.d5);
                System.out.println("fri :" + blackListNo.d6);
                System.out.println("sat :" + blackListNo.d7);
                System.out.println("count :" + blackListNo.missCallCount);
                System.out.println("date :" + blackListNo.date);


                // Insert the object to the database
                blackListDao.updateBlock(blackListNo);


                // Show the success message to user
                showDialog();
            }
            // Show a dialog with appropriate message in case input fields are blank
            else {
                showMessageDialog("Fill country code & phone number...");
            }
        } else if (v == cancel_btn) {
            finish();
        } else if (v == tvChangeStartTime) {
            timePickerDialogStart.show(getFragmentManager(), tag);

        } else if (v == tvChangeEndTime) {
            if (checkBoxEndTime.isChecked()) {
                timePickerDialogEnd.show(getFragmentManager(), tag);
            }

        } else if (v == btnShowContactList) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactPickerIntent, REQUEST_CONTACT_NUMBER);
        } else if (v == tvChangeCountryCode) {
            showDialogList();
        }

    }


    // Clear the entered text
    private void reset() {
        country_code_auto.setText("");
        phone_auto.setText("");
        tvDisplayStartTime.setText("");
        tvDisplayEndTime.setText("");
        checkBoxRepeat.setSelected(false);
        unselectDays();
    }

    private void unselectDays() {

        wd1 = 0;
        wd2 = 0;
        wd3 = 0;
        wd4 = 0;
        wd5 = 0;
        wd6 = 0;
        wd7 = 0;
        d1.setBackgroundColor(getResources().getColor(R.color.white));
        d1.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));

        d2.setBackgroundColor(getResources().getColor(R.color.white));
        d2.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));

        d3.setBackgroundColor(getResources().getColor(R.color.white));
        d3.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));

        d4.setBackgroundColor(getResources().getColor(R.color.white));
        d4.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));

        d5.setBackgroundColor(getResources().getColor(R.color.white));
        d5.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));

        d6.setBackgroundColor(getResources().getColor(R.color.white));
        d6.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));

        d7.setBackgroundColor(getResources().getColor(R.color.white));
        d7.setTextColor(getResources().getColor(R.color.bootstrap_brand_info));
    }

    private void showDialog() {
        // After submission, Dialog opens up with "Success" message. So, build the AlartBox first
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the appropriate message into it.
        alertDialogBuilder.setMessage("Update number successfully...");

        // Add a positive button and it's action. In our case action would be, just hide the dialog box ,
        // and erase the user inputs.
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });


        // Now, create the Dialog and show it.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showMessageDialog(final String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    final TimePickerDialog timePickerDialogStart = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

            Object c = pad3(hourOfDay);

            tvDisplayStartTime.setText(new StringBuilder().append(pad2(hourOfDay)).append(":").append(pad(minute)).append(c));
            tvDisplayStartTime.setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
        }
    }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);

    final TimePickerDialog timePickerDialogEnd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

            Object c = pad3(hourOfDay);

            tvDisplayEndTime.setText(new StringBuilder().append(pad2(hourOfDay)).append(":").append(pad(minute)).append(c));
            tvDisplayEndTime.setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
        }
    }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false);


    private static String pad3(int c) {
        if (c == 12)
            return " PM";
        if (c == 00)
            return " AM";
        if (c > 12)
            return " PM";
        else
            return " AM";
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private static String pad2(int c) {
        if (c == 12)
            return String.valueOf(c);
        if (c == 00)
            return String.valueOf(c + 12);
        if (c > 12)
            return String.valueOf(c - 12);
        else
            return String.valueOf(c);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ContactNoWithName contactNoWithName = listContact.get(position);
        phone_auto.setText(contactNoWithName.name);
        blackListNo.phoneNumber = contactNoWithName.no;
        blackListNo.contactName = contactNoWithName.name;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null && requestCode == REQUEST_CONTACT_NUMBER) {
                Uri uriOfPhoneNumberRecord = data.getData();
                String idOfPhoneRecord = uriOfPhoneNumberRecord.getLastPathSegment();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[]{idOfPhoneRecord}, null);

                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        String formattedPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String formattedName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        phone_auto.setText(formattedName);
                        blackListNo.phoneNumber = formattedPhoneNumber;
                        blackListNo.contactName = formattedName;
                        Log.d("TestActivity", String.format("The selected phone number is: %s", formattedPhoneNumber + formattedName));
                    }
                    cursor.close();
                }
            } else {
                Log.w("TestActivity", "WARNING: Corrupted request response");
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.i("TestActivity", "Popup canceled by user.");
        } else {
            Log.w("TestActivity", "WARNING: Unknown resultCode");
        }
    }

    void showDialogList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select Country Code");

        final ArrayAdapter<ContryWithCode> arrayAdapter = new ArrayAdapter<ContryWithCode>(this, android.R.layout.select_dialog_singlechoice);
        for (ContryWithCode contryWithCode : contryWithCodes)
            arrayAdapter.add(contryWithCode);

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContryWithCode item = arrayAdapter.getItem(which);
                        country_code_auto.setText(item.code);
                    }
                });
        builderSingle.show();
    }

    public class ContryWithCode {
        String country;
        String code;

        public ContryWithCode(String country, String code) {
            this.country = country;
            this.code = code;
        }

        @Override
        public String toString() {
            return country + " - " + code;
        }
    }

    public class ContactNoWithName {
        String name;
        String no;

        public ContactNoWithName(String name, String no) {
            this.name = name;
            this.no = no;
        }

        @Override
        public String toString() {
            return name + "\n" + no;
        }
    }

}
