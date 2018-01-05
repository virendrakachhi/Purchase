package com.hashmybag;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.hashmybag.adapters.ImageAdapter;
import com.hashmybag.beans.AllStoreBean;
import com.hashmybag.databasehandle.DatabaseHandler;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is handling HMB users profile
 * including image,name,number etc
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-27
 */

public class Profile extends AppCompatActivity implements View.OnClickListener {
    HorizontalListView listView;
    TextView following, name, last_seen, phone, email;
    ImageView back_arrow, profile_pic, redButton;
    ImageLoader loader;
    RelativeLayout profile_layout;
    String id, from, namef;
    DatabaseHandler db;
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildAlertMessageNoGps();
        }
    };
    private ArrayList<AllStoreBean> allFollowedList = new ArrayList<>();
    /**
     * Handler to handle JSON response coming from profile info API
     */

    private String message, responseNotifResponse;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.GET_PROFILE_INFO_PID:
                    responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        message = jsonResponse.optString("message");
                        allFollowedList.clear();
                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            JSONObject data = jsonResponse.getJSONObject("data");
                            JSONObject customer = data.getJSONObject("customer");

                            String customer_id = customer.optString("id");
                            String customer_email = customer.optString("email");
                            String customer_username = customer.optString("username");
                            String customer_authentication_token = customer.optString("authentication_token");
                            String customer_sign_up_stream = customer.optString("sign_up_stream");
                            String customer_latitude = customer.optString("latitude");
                            String customer_longitude = customer.optString("longitude");
                            String customer_address1 = customer.optString("address1");
                            String customer_location = customer.optString("location");
                            String customer_f_name = customer.optString("first_name");
                            String customer_l_name = customer.optString("last_name");
                            String customer_title = customer.optString("title");
                            String customer_description = customer.optString("description");
                            String customer_mobile_number = customer.optString("mobile_number");
                            String customer_landline_number = customer.optString("landline_number");
                            String customer_active = customer.optString("active");
                            String customer_photo = customer.optString("photo");


                            if (customer_photo != null) {
                                loader.displayImage(customer_photo, profile_pic,
                                        new SimpleImageLoadingListener() {
                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {
                                                super.onLoadingStarted(imageUri, view);
                                            }
                                        });
                            }

                            //if(customer_title !=null)
                            name.setText(namef);
                            if (customer_email != null)
                                email.setText(customer_email);
                            if (customer_mobile_number != null)
                                phone.setText(customer_mobile_number);


                           /* if(lastseen_ !=null)
                                last_seen.setText(lastseen_);
*/

                            if (from.equalsIgnoreCase("chat")) {
                                JSONArray connections = customer.getJSONArray("connections");
                                for (int i = 0; i < connections.length(); i++) {
                                    JSONObject jsonResponse1 = connections.getJSONObject(i);
                                    String id = jsonResponse1.optString("id");
                                    String email = jsonResponse1.optString("email");
                                    String title = jsonResponse1.optString("title");
                                    String description = jsonResponse1.optString("description");
                                    String address1 = jsonResponse1.optString("address1");
                                    String photo = jsonResponse1.optString("photo");
                                    String latitude = jsonResponse1.optString("latitude");
                                    String longitude = jsonResponse1.optString("longitude");
                                    String active = jsonResponse1.optString("active");
                                    String location = jsonResponse1.optString("location");
                                    String mobile_number = jsonResponse1.optString("mobile_number");
                                    String landline_number = jsonResponse1.optString("landline_number");

                                    AllStoreBean storeBean = new AllStoreBean();
                                    storeBean.setStore_id(id);
                                    storeBean.setStore_email(email);
                                    storeBean.setStore_title(title);
                                    storeBean.setStore_description(description);
                                    storeBean.setStore_address1(address1);
                                    storeBean.setStore_photo(photo);
                                    storeBean.setStore_latitude(latitude);
                                    storeBean.setStore_longitude(longitude);
                                    storeBean.setStore_active(active);
                                    storeBean.setStore_location(location);
                                    storeBean.setMobile_number(mobile_number);
                                    storeBean.setStore_landline(landline_number);
                                    allFollowedList.add(storeBean);
                                }
                            } else {
                                JSONArray connections = data.getJSONArray("connections");
                                for (int i = 0; i < connections.length(); i++) {
                                    JSONObject jsonResponse1 = connections.getJSONObject(i);
                                    String id = jsonResponse1.optString("id");
                                    String email = jsonResponse1.optString("email");
                                    String title = jsonResponse1.optString("title");
                                    String description = jsonResponse1.optString("description");
                                    String address1 = jsonResponse1.optString("address1");
                                    String photo = jsonResponse1.optString("photo");
                                    String latitude = jsonResponse1.optString("latitude");
                                    String longitude = jsonResponse1.optString("longitude");
                                    String active = jsonResponse1.optString("active");
                                    String location = jsonResponse1.optString("location");
                                    String mobile_number = jsonResponse1.optString("mobile_number");
                                    String landline_number = jsonResponse1.optString("landline_number");

                                    AllStoreBean storeBean = new AllStoreBean();
                                    storeBean.setStore_id(id);
                                    storeBean.setStore_email(email);
                                    storeBean.setStore_title(title);
                                    storeBean.setStore_description(description);
                                    storeBean.setStore_address1(address1);
                                    storeBean.setStore_photo(photo);
                                    storeBean.setStore_latitude(latitude);
                                    storeBean.setStore_longitude(longitude);
                                    storeBean.setStore_active(active);
                                    storeBean.setStore_location(location);
                                    storeBean.setMobile_number(mobile_number);
                                    storeBean.setStore_landline(landline_number);
                                    allFollowedList.add(storeBean);
                                }
                            }
                            profile_layout.setVisibility(View.VISIBLE);

                            if (allFollowedList.size() != 0)
                                listView.setAdapter(new ImageAdapter(Profile.this, allFollowedList));

                            //     Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                        } else if (status.equals("401")) {
                            startActivity(new Intent(Profile.this, MainActivity.class));
                            SharedpreferenceUtility.getInstance(Profile.this).putBoolean(Constants.CUSTOMER_LOGIN_OR_NOT, false);
                        }
                    } catch (JSONException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Profile.this, "Not found any Follow list!", Toast.LENGTH_LONG).show();
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
        Profile.this.setTheme(R.style.NextTheme);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        from = intent.getStringExtra("from");
        namef = intent.getStringExtra("name");

        db = new DatabaseHandler(getApplicationContext());
        init(this);
        initImageLoader();

    }

    /**
     * Method used for getting users profile information from API
     *
     * @param id
     */

    public void getProfileInfo(String id) {

        new WebRequestTask(this, _handler, Constants.GET_METHOD,
                WebServiceDetails.GET_PROFILE_INFO_PID, true, WebServiceDetails.GET_PROFILE_INFO + id).execute();
    }

    public void init(Profile profile) {
        profile.listView = (HorizontalListView) profile.findViewById(R.id.hlistview);
        profile.name = (TextView) profile.findViewById(R.id.name);
        profile.last_seen = (TextView) profile.findViewById(R.id.last_seen);
        profile.phone = (TextView) profile.findViewById(R.id.phone);
        profile.email = (TextView) profile.findViewById(R.id.email12);
        profile.profile_pic = (ImageView) profile.findViewById(R.id.profile_pic);
        profile.following = (TextView) profile.findViewById(R.id.following);
        profile.back_arrow = (ImageView) profile.findViewById(R.id.back_arrow);
        profile.redButton = (ImageView) profile.findViewById(R.id.redButton);
        if (from.equalsIgnoreCase("chat")) {
            profile.redButton.setVisibility(View.VISIBLE);
        } else {
            profile.redButton.setVisibility(View.GONE);

        }
        profile.profile_layout = (RelativeLayout) profile.findViewById(R.id.profile_layout);
        profile.back_arrow.setOnClickListener(profile);
        redButton.setOnClickListener(this);
    }

    private void initImageLoader() {
        try {
            String CACHE_DIR =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(this, CACHE_DIR);

            DisplayImageOptions defaultOptions = new
                    DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new
                    ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(defaultOptions).discCache(new
                            UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());
            ImageLoaderConfiguration config = builder.build();
            loader = ImageLoader.getInstance();
            loader.init(config);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileInfo(id);

        registerReceiver(locationReciever, new IntentFilter("com.hashmybag.location"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                finish();
                break;
            case R.id.redButton:
                /*ArrayList<InboxBean> inboxList=db.getInbox();

                for(int i=0;i<inboxList.size();i++){
                    InboxBean  inboxBean=inboxList.get(i);
                    if(inboxBean.getFriendId().equalsIgnoreCase(id))
                    {
                        ArrayList<String> arrayList1=new ArrayList<String>();
                        arrayList1.add(inboxBean.getFriendId());
                        arrayList1.add(inboxBean.getPhoto());
                        arrayList1.add(inboxBean.getFriendName());
                        arrayList1.add(inboxBean.getCommunicationId());
                        arrayList1.add(inboxBean.getLastMessage());
                        arrayList1.add(inboxBean.getLastSeen());
                        arrayList1.add(inboxBean.getIndicater());
                        onBackPressed();
                    }
                }*/
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (!check()) {
                buildAlertMessageNoGps();
            }
        }
    }


}
