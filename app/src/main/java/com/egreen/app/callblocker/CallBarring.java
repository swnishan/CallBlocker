package com.egreen.app.callblocker;

import java.lang.reflect.Method;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.UiThread;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
;


// Extend the class from BroadcastReceiver to listen when there is a incoming call
public class CallBarring extends BroadcastReceiver {
    private static final String TAG = CallBarring.class.getName();
    // This String will hold the incoming phone number
    private String incomingNumber;
    private BlacklistDAO blackListDao;
    private List<Blacklist> blockList;

    public CallBarring() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // Initialize the DAO object
        blackListDao = new BlacklistDAO(context);

        // Fetch the list of Black listed numbers from Database using DAO object
        blockList = blackListDao.getAllBlacklist();

        Log.i(TAG, intent.getAction());

        // If, the received action is not a type of "Phone_State", ignore it
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {

            // Fetch the number of incoming call
            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int isBlockNo = 0;
            if (incomingNumber != null) {
                for (Blacklist blacklist : blockList) {
                    if (new Blacklist(incomingNumber).equals(blacklist)) {
                        isBlockNo = 1;
                        Log.i("number", incomingNumber);
                        disconnectPhoneItelephony(context);
                        blackListDao.plusCallCount(blacklist.phoneNumber, blacklist.missCallCount + 1);
                        System.out.println();
                        incomingNumber = null;
                        break;
                    }
                }
                if (isBlockNo == 0) {
                    connectPhoneItelephony(context);
                }
            }
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            System.out.println("number end");
            telephonyService.showCallScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            System.out.println("number end");
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
