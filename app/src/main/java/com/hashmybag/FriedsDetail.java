package com.hashmybag;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hashmybag.beans.StoreBean;

import java.util.ArrayList;

/**
 * This class is used for showing details of friends
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-27
 */

public class FriedsDetail extends AppCompatActivity implements View.OnClickListener {
    ListView listView;
    ArrayList<StoreBean> arrayList;
    RelativeLayout relative1, relative2, relative3;
    TextView following, friend_name, name, address;
    ImageView nike_image, back_arrow;
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildAlertMessageNoGps();
        }
    };
    private ArrayList<String> arrayList1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FriedsDetail.this.setTheme(R.style.NextTheme);

        setContentView(R.layout.friend_details);

        arrayList = new ArrayList<>();
        arrayList1 = new ArrayList<>();


        try {
            Intent intent = getIntent();
            arrayList1 = intent.getStringArrayListExtra("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // getListItems();
        init();
        // listView.setAdapter(new ListAdapter(this, arrayList));
    }

    /**
     * Initialising button and listview's id
     */

    public void init() {
        listView = (ListView) findViewById(R.id.similar_store);
        relative1 = (RelativeLayout) findViewById(R.id.relative1);
        relative2 = (RelativeLayout) findViewById(R.id.relative2);
        relative3 = (RelativeLayout) findViewById(R.id.relative3);
        friend_name = (TextView) findViewById(R.id.friend_name);
        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);


        try {
            nike_image = (ImageView) findViewById(R.id.nike_image);
            friend_name.setText(arrayList1.get(0) + " Stores");
            name.setText(arrayList1.get(0));
            address.setText(arrayList1.get(3));

        } catch (Exception e) {
            e.printStackTrace();
        }

        following = (TextView) findViewById(R.id.following);
        address.setOnClickListener(this);
        back_arrow.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationReciever);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        if (!check()) {
                            buildAlertMessageNoGps();
                        }
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean check() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(locationReciever, new IntentFilter("com.hashmybag.location"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (!check()) {
                buildAlertMessageNoGps();
            }
        }
    }
}
