package com.hashmybag;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hashmybag.adapters.ListAdapter;
import com.hashmybag.beans.AllStoreBean;
import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is used for searching nearby stores and map.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-27
 */

public class NearByStore extends AppCompatActivity {
    ListView listView;
    ImageView back_arrow;
    List<String> storeList;
    ArrayList<AllStoreBean> similarStoreList = new ArrayList<>();
    ListAdapter adapter;
    double latitude, longitude;
    ConnectionDetector cd;
    CircleImageView store_pic;
    TextView store_name, store_address;
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildAlertMessageNoGps();
        }
    };
    private GoogleMap googleMap;
    private String message;
    /**
     * Handler to handle JSON response coming from similar stores API.
     */

    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.SIMILAR_STORES_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        message = jsonResponse.optString("message");

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            JSONObject user = jsonResponse.getJSONObject("data");
                            JSONArray stores = user.getJSONArray("stores");
                            for (int i = 0; i < stores.length(); i++) {
                                JSONObject jsonResponse1 = stores.getJSONObject(i);
                                String stror_id = jsonResponse1.optString("id");
                                String stror_email = jsonResponse1.optString("email");
                                String stror_photo = jsonResponse1.optString("photo");
                                String stror_latitude = jsonResponse1.optString("latitude");
                                String stror_longitude = jsonResponse1.optString("longitude");
                                String stror_address1 = jsonResponse1.optString("address1");
                                String stror_title = jsonResponse1.optString("title");
                                String stror_description = jsonResponse1.optString("description");
                                String mobile_number = jsonResponse1.optString("mobile_number");
                                String category_id = jsonResponse1.optString("category_id");
                                String store_active = jsonResponse1.optString("active");
                                String twitter_username = jsonResponse1.optString("twitter_username");
                                String facebook_page = jsonResponse1.optString("facebook_page");
                                String instagram_user_name = jsonResponse1.optString("instagram_user_name");
                                String following = jsonResponse1.optString("following");


                                AllStoreBean allStoreBean = new AllStoreBean();
                                allStoreBean.setStore_id(stror_id);
                                allStoreBean.setStore_email(stror_email);
                                allStoreBean.setStore_photo(stror_photo);
                                allStoreBean.setStore_latitude(stror_latitude);
                                allStoreBean.setStore_longitude(stror_longitude);
                                allStoreBean.setStore_address1(stror_address1);
                                allStoreBean.setStore_title(stror_title);
                                allStoreBean.setStore_description(stror_description);
                                allStoreBean.setMobile_number(mobile_number);
                                allStoreBean.setStore_category_id(category_id);
                                allStoreBean.setStore_active(store_active);
                                allStoreBean.setStore_twitter_username(twitter_username);
                                allStoreBean.setStore_facebook_page(facebook_page);
                                allStoreBean.setStore_instagram_user_name(instagram_user_name);
                                allStoreBean.setStore_following(following);
                                similarStoreList.add(allStoreBean);

                                LatLng latLng = new LatLng(Double.valueOf(stror_latitude), Double.valueOf(stror_longitude));
                                MarkerOptions marker = new MarkerOptions().position(latLng).title(stror_title);
                                marker.snippet(stror_address1);
                                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.storeicon));
                                googleMap.addMarker(marker);
                            }

                            adapter = new ListAdapter(NearByStore.this, similarStoreList);
                            listView.setAdapter(adapter);


                            //   Toast.makeText(NearByFriend.this,message,Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(NearByStore.this, "Mobile was Wrong!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NearByStore.this.setTheme(R.style.NextTheme);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.near_by);
        storeList = new ArrayList<>();
        storeList = getIntent().getStringArrayListExtra("data");

        listView = (ListView) findViewById(R.id.near_by_list);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        getSimilarStores();


        store_pic = (CircleImageView) findViewById(R.id.store_pic);
        store_name = (TextView) findViewById(R.id.store_name);
        store_address = (TextView) findViewById(R.id.store_address);

        store_name.setText(storeList.get(5));
        store_address.setText(storeList.get(4));
        Picasso.with(this)
                .load(storeList.get(1)).resize(80, 80)
                .centerCrop().error(R.mipmap.notification_bag)
                .into(store_pic);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                cd = new ConnectionDetector(getApplicationContext());
                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent intent = new Intent(NearByStore.this, StoreDetail.class);
                    intent.putExtra("id", similarStoreList.get(position).getStore_id());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No Network Found", Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            // Loading map
            initilizeMap();


        } catch (Exception e) {
            e.printStackTrace();
        }
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Initializing google Map
     */

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();


            // latitude and longitude
            latitude = Double.parseDouble(storeList.get(2));
            longitude = Double.parseDouble(storeList.get(3));

            // create marker
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(storeList.get(5));
            // adding marker
            marker.snippet(storeList.get(4));
            marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.storeicon));

            googleMap.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(12).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * API/web call for getting similar stores
     */

    private void getSimilarStores() {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("search_by", "similiar_store");
            jsonObject.put("store_id", storeList.get(0));
            jsonObject.put("per_page", "20");
            jsonObject.put("offset", "0");


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.SIMILAR_STORES_PID, false, WebServiceDetails.SIMILAR_STORES).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();

        registerReceiver(locationReciever, new IntentFilter("com.hashmybag.location"));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (!check()) {
                buildAlertMessageNoGps();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationReciever);
    }

}
