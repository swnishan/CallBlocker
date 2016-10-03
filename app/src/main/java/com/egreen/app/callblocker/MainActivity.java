package com.egreen.app.callblocker;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.beardedhen.androidbootstrap.BootstrapButton;
import com.egreen.app.callblocker.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.samples.quickstart.analytics.AnalyticsApplication;

public class MainActivity extends ActionBarActivity implements OnClickListener, OnItemLongClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = MainActivity.class.getName();
    // Declaration all on screen components of the Main screen
    private BootstrapButton add_blacklist_btn;
    public ListView listview;
    private FloatingActionButton fab;
    private View footerView;
    private AdView mAdView;

    // Object of BlacklistDAO to query to database
    private BlacklistDAO blackListDao;
    private MissCallHistoryDAO missCallHistoryDAO;

    // It holds the list of Blacklist objects fetched from Database
    public static List<com.egreen.app.callblocker.Blacklist> blockList = new ArrayList<>();
    public static List<MissCallHistory> callHistoryList = new ArrayList<>();


    // This holds the value of the row number, which user has selected for further action
    private int selectedRecordPosition = -1;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the shared Tracker instance.
        com.egreen.app.callblocker.AnalyticsApplication application = (com.egreen.app.callblocker.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // Initialization of the button of the Main screen
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Attachment of onClickListner for it
        fab.setOnClickListener(this);

        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.no_number_footer, listview, false);
        footerView.setOnClickListener(this);

        // Initialization of the listview of the Main screen to display black listed phone numbers
        listview = (ListView) findViewById(R.id.listview);


        // Attach OnItemLongClickListener to track user action and perform accordingly
        listview.setOnItemLongClickListener(this);

    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void populateNoRecordMsg() {
        // If, no record found in the database, appropriate message needs to be displayed.
        if (blockList.size() == 0) {
            System.out.println("block lIst count " + blockList.size());
            if (listview.getFooterViewsCount() == 0) {
                listview.addFooterView(footerView);
            }

        }
    }

    @Override
    public void onClick(View v) {
        // Render AddToBlocklistActivity screen once click on "Add" Button
        if (v == fab || v == footerView) {
            startActivity(new Intent(this, com.egreen.app.callblocker.AddToBlocklistActivity.class));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // If the pressed row is not a header, update selectedRecordPosition and
        // show dialog for further selection
        Log.i(TAG, position + "");
        if (position > -1) {
            selectedRecordPosition = position;
            showDialog();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("onResume");
        String name = TAG;

        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // Initialize the DAO object
        blackListDao = new BlacklistDAO(this);
        missCallHistoryDAO = new MissCallHistoryDAO(this);

        // Fetch the list of Black listed numbers from Database using DAO object
        blockList = blackListDao.getAllBlacklist();
        callHistoryList = missCallHistoryDAO.getAllMissCallList();

        //Now, link the  CustomArrayAdapter with the ListView
        listview.setAdapter(new CustomArrayAdapter(this, R.layout.list_item, blockList));

        // If, no record found in the database, appropriate message needs to be displayed.
        if (blockList.size() != 0) {
            System.out.println("block lIst count " + blockList.size());
            listview.removeFooterView(footerView);
        } else {
            System.out.println("block lIst count " + blockList.size());
            populateNoRecordMsg();
        }

        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    private void showDialog() {
        // Before deletion of the long pressed record, need to confirm with the user. So, build the AlartBox first
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set the appropriate message into it.
        alertDialogBuilder.setMessage("Are you want to unblock the number?");

        // Add a positive button and it's action. In our case action would be deletion of the data
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {

                            blackListDao.delete(blockList.get(selectedRecordPosition));

                            // Removing the same from the List to remove from display as well
                            blockList.remove(selectedRecordPosition);
                            listview.invalidateViews();

                            // Reset the value of selectedRecordPosition
                            selectedRecordPosition = -1;
                            if (blockList.size() != 0) {
                                listview.removeFooterView(footerView);
                            } else {
                                populateNoRecordMsg();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        // Add a negative button and it's action. In our case, just hide the dialog box
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        // Now, create the Dialog and show it.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.info:
//                Intent intent = new Intent(this, com.google.samples.quickstart.analytics.MainActivity.class);
//                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class CustomArrayAdapter extends ArrayAdapter<String> {

        private LayoutInflater inflater;

        // This would hold the database objects i.e. Blacklist
        private List<com.egreen.app.callblocker.Blacklist> records;

        @SuppressWarnings("unchecked")
        public CustomArrayAdapter(Context context, int resource, @SuppressWarnings("rawtypes") List objects) {
            super(context, resource, objects);

            this.records = objects;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //Reuse the view to make the scroll effect smooth
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_rows, parent, false);

            // Fetch phone number from the database object
            final com.egreen.app.callblocker.Blacklist phoneNumber = records.get(position);

            // Set to screen component to display results
            if (phoneNumber.contactName == null) {
                ((TextView) convertView.findViewById(R.id.txtPhoneNo)).setText(phoneNumber.phoneNumber);
            } else {
                ((TextView) convertView.findViewById(R.id.txtPhoneNo)).setText(phoneNumber.contactName);
            }

            System.out.println(phoneNumber.missCallCount);
            ((TextView) convertView.findViewById(R.id.txtMissCallCount)).setText(phoneNumber.missCallCount + "");

            repeatingDate(phoneNumber, convertView);

            if (phoneNumber.endTime == null) {
                ((TextView) convertView.findViewById(R.id.txtBlockTime)).setText(phoneNumber.startTime + " - " + "towards");
            } else {
                ((TextView) convertView.findViewById(R.id.txtBlockTime)).setText(phoneNumber.startTime + " - " + phoneNumber.endTime);
            }

            CardView cardView = (CardView) convertView.findViewById(R.id.cardView);
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    selectedRecordPosition = position;
                    MainActivity.this.showDialog();
                    return true;
                }
            });

            Button btnChangeNo = (Button) convertView.findViewById(R.id.btnChangeNo);
            btnChangeNo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, UpdateBlocklistActivity.class);
                    intent.putExtra("bundle", records.get(position));
                    startActivity(intent);
                }
            });

            return convertView;
        }

        void repeatingDate(com.egreen.app.callblocker.Blacklist phoneNumber, View convertView) {

            if (phoneNumber.d1 == 1) {
                ((TextView) convertView.findViewById(R.id.txtsu)).setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
            }
            if (phoneNumber.d2 == 1) {
                ((TextView) convertView.findViewById(R.id.txtmo)).setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
            }
            if (phoneNumber.d3 == 1) {
                ((TextView) convertView.findViewById(R.id.txtte)).setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
            }
            if (phoneNumber.d4 == 1) {
                ((TextView) convertView.findViewById(R.id.txtw)).setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
            }
            if (phoneNumber.d5 == 1) {
                ((TextView) convertView.findViewById(R.id.txtth)).setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
            }
            if (phoneNumber.d6 == 1) {
                ((TextView) convertView.findViewById(R.id.txtf)).setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
            }
            if (phoneNumber.d7 == 1) {
                ((TextView) convertView.findViewById(R.id.txtsa)).setTextColor(getResources().getColor(R.color.bootstrap_brand_primary));
            }
        }
    }
}
