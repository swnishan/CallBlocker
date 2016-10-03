package com.egreen.app.callblocker;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * Created by npradeep on 10/15/15.
 */
public class MyService extends IntentService {


    public MyService() {
        super("MySrevice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        CallBarring callBarring = new CallBarring();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.PHONE_STATE");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//        filter.addAction("your_action_strings"); //further more
//        filter.addAction("your_action_strings"); //further more

        registerReceiver(callBarring, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
