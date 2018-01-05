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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.hashmybag.databasehandle.DatabaseHandler;
import com.hashmybag.fragments.MyFriendsFragment;
import com.hashmybag.fragments.MyWalletFragment;
import com.hashmybag.fragments.MyWishlistFragment;
import com.hashmybag.fragments.PaymentsFragment;
import com.hashmybag.fragments.SettingFragments;
import com.hashmybag.fragments.SubscriptionFragment;
import com.hashmybag.fragments.TabScreen;
import com.hashmybag.pusharintegration.PusherChat;
import com.hashmybag.pushnotifications.GCMRegistrationIntentService;
import com.hashmybag.servercommunication.ConnectionDetector;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is responsible for handling all the the fragments and main screen of the app
 * Fragments: Subscription, myFriens and all which are on NavigationDrawer
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-10
 */

public class AllFragmentsActivity extends AppCompatActivity {

    LinearLayout Subscription_fragment, MyFriends_fragment, MyWishlist_fragment, MyWallet_fragment, Payments_fragment, Settings_fragment, Logout_know, Full;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MyFriendsFragment myFriendsFragment;
    MyWalletFragment myWalletFragment;
    MyWishlistFragment myWishlistFragment;
    PaymentsFragment paymentsFragment;
    SettingFragments settingFragments;
    SubscriptionFragment subscriptionFragment;
    TabScreen tabScreen;
    DrawerLayout drawerLayout;
    ImageView drawerToggle;
    CircleImageView user_image1;
    RelativeLayout headerLayout, first_relative;
    TextView user_name, user_location, subscripton_image_noti;
    ImageLoader loader;
    DatabaseHandler db;
    ConnectionDetector cd;
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildAlertMessageNoGps();
        }
    };
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /**
     * Handler to handle JSON response  coming from the get OTP API
     * OTP handling
     */
    private String message, responseNotifResponse;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.GCM_URL_PID:
                    responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        message = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {

                        } else {
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AllFragmentsActivity.this.setTheme(R.style.NextTheme);
        setContentView(R.layout.activity_all_fragments);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        try {
            //    startService(new Intent(AllFragmentsActivity.this, FusedLocationTracker.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        PusherChat.getInstance(this);
        db = new DatabaseHandler(getApplicationContext());

        //updating location service

        tabScreen = new TabScreen();
        initImageLoader();
        final String id = SharedpreferenceUtility.getInstance(getApplicationContext()).getString(Constants.CUSTOMER_ID);
        cd = new ConnectionDetector(this);

        myFriendsFragment = new MyFriendsFragment();
        myWalletFragment = new MyWalletFragment();
        myWishlistFragment = new MyWishlistFragment();
        paymentsFragment = new PaymentsFragment();
        settingFragments = new SettingFragments();
        subscriptionFragment = new SubscriptionFragment();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        Subscription_fragment = (LinearLayout) findViewById(R.id.subscription_layout);
        MyFriends_fragment = (LinearLayout) findViewById(R.id.myFriends_layout);
        MyWishlist_fragment = (LinearLayout) findViewById(R.id.myWishlist_layout);
        MyWallet_fragment = (LinearLayout) findViewById(R.id.myWallet_layout);
        Payments_fragment = (LinearLayout) findViewById(R.id.payments_layout);
        Settings_fragment = (LinearLayout) findViewById(R.id.settings_layout);
        Logout_know = (LinearLayout) findViewById(R.id.logout_layout);
        first_relative = (RelativeLayout) findViewById(R.id.first_relative);
        Full = (LinearLayout) findViewById(R.id.full_layout);
        drawerToggle = (ImageView) findViewById(R.id.menu_button);
        user_image1 = (CircleImageView) findViewById(R.id.user_image1);
        user_name = (TextView) findViewById(R.id.user_name);
        subscripton_image_noti = (TextView) findViewById(R.id.subscripton_image_noti);

        user_location = (TextView) findViewById(R.id.user_location);
        String photo = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_PHOTO);
        loader.displayImage(photo, user_image1,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        user_image1.setImageResource(R.mipmap.ic_launcher);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

        final String title = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_TITLE);
        String location = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_LOCATION);
        String address1 = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_ADDRESS1);
        int storescr = SharedpreferenceUtility.getInstance(this).getInt(Constants.SUBSCRIBED_STORE);

        user_name.setText(title);
        if (!location.equalsIgnoreCase("null") && location != null) {
            user_location.setText(location);
        } else {
            user_location.setText(address1);
        }

        if (!String.valueOf(storescr).equalsIgnoreCase("")) {
            if (storescr > 1) {
                subscripton_image_noti.setVisibility(View.VISIBLE);
                subscripton_image_noti.setText(String.valueOf(storescr));
            } else {
                subscripton_image_noti.setVisibility(View.GONE);
            }
        } else {
            subscripton_image_noti.setVisibility(View.GONE);
        }

        headerLayout = (RelativeLayout) findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.VISIBLE);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, tabScreen);
        fragmentTransaction.commit();
        first_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        /**
         * Listening click events on drawer items
         */
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    intent.putExtra("id", id);
                    intent.putExtra("from", "main");
                    intent.putExtra("name", title);
                    startActivity(intent);
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "No Network Found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        user_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                    intent.putExtra("id", id);
                    intent.putExtra("from", "main");

                    startActivity(intent);
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "No Network Found", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /**
         * Drawer toggle implemented over here
         */

        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        });

        /**
         * All the click events are listening for the fragments.
         */

        Subscription_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    fragmentManager.popBackStackImmediate();
                    fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.main_frame, subscriptionFragment);
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MyFriends_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {

                    drawerLayout.closeDrawers();
                    fragmentManager.popBackStackImmediate();
                    fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.main_frame, myFriendsFragment);
                    fragmentTransaction.commit();
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        MyWishlist_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    fragmentManager.popBackStackImmediate();
                    fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.main_frame, myWishlistFragment);
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MyWallet_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    fragmentManager.popBackStackImmediate();
                    fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    Intent intent = new Intent(AllFragmentsActivity.this, WebActivity.class);
                    intent.putExtra("url", "https://paytm.com/paytmwallet");
                    startActivity(intent);
                    // fragmentTransaction.replace(R.id.main_frame, myWalletFragment);
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Payments_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    fragmentManager.popBackStackImmediate();
                    fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.main_frame, paymentsFragment);
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Settings_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    fragmentManager.popBackStackImmediate();
                    fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.main_frame, settingFragments);
                    fragmentTransaction.commit();
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Logout_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.hashmybag");
                    // sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                } else {
                    drawerLayout.closeDrawers();
                    Toast.makeText(getApplicationContext(), "Network Not Available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Log.v("Device Token :", token);

                    // Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();
                    setDeviceInfo(token);
                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };
        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        if (!check()) {
            buildAlertMessageNoGps();
        }
    }

    private void setDeviceInfo(String token) {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_id", token);

            JSONObject device_info = new JSONObject();

            device_info.put("device_info", jsonObject);
            // 4. convert JSONObject to JSON to String
            json = device_info.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.GCM_URL_PID, false, WebServiceDetails.GCM_URL).execute();
    }

    /**
     * Initializing image loader to load all the images coming from server
     */

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
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        unregisterReceiver(locationReciever);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /**
     * If user click back button
     */

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * When application is on hold or minimized
     */


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent1 = new Intent();
        intent1.setAction("com.hashmybag.MessageRecieve");
        sendBroadcast(intent1);

        registerReceiver(locationReciever, new IntentFilter("com.hashmybag.location"));

        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
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
                            Toast.makeText(AllFragmentsActivity.this, "GPS is not enabled!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AllFragmentsActivity.this, "GPS is not enabled!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
