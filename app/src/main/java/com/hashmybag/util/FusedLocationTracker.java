package com.hashmybag.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hashmybag.fragments.MyWishlistFragment;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * This class is used for implimenting location tracking for wishlist offers
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-08-09
 */
public class FusedLocationTracker extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GpsStatus.Listener {

    private static final String TAG = "FusedLocationTracker";
    private static final float DISPLACEMENT = 15 * 1000;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    Context mContext;
    LocationRequest mLocationRequest;
    int status;
    MyWishlistFragment myWishlistFragment;
    /**
     * Handler to handle json response from wishlist offers API
     */

    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.WHISHLIST_OFFERS_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private double latitude, longitude;
    private LocationManager mService;
    private GpsStatus mStatus;

    public FusedLocationTracker() {
    }

    public FusedLocationTracker(Context context) {
        mContext = context;
        getLocation();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    /**
     * Location With google api
     */

    private void getLocation() {

        if (isGooglePlayServicesAvailable()) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        } else {
            Toast.makeText(FusedLocationTracker.this, "Some feature can not run on your device, Google play unavailable!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        mLocationRequest.setInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        myWishlistFragment = new MyWishlistFragment();
        if (isGooglePlayServicesAvailable()) {
            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mService.addGpsStatusListener(this);

        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * Call back listener when connected to API client
     *
     * @param bundle
     */

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        Log.d(TAG, "Location update started .......................");
    }

    /**
     * Location updates will be given to this method
     */

    protected void startLocationUpdates() {
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Listener , if not connected
     *
     * @param i
     */

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Location", "Connection suspended");
    }

    /**
     * Listener method to called when location changes
     *
     * @param location
     */

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        longitude = mCurrentLocation.getLongitude();
        latitude = mCurrentLocation.getLatitude();
        if (myWishlistFragment.wishlistSize > 0) {
            getWhishListOffer(longitude, latitude);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    /**
     * Check for if google play is avaible in device or not
     *
     * @return
     */

    private boolean isGooglePlayServicesAvailable() {
        try {
            status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            return false;
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    /**
     * API to call when location changed which will provide wishlist offers
     *
     * @param latitude
     * @param longitude
     */

    private void getWhishListOffer(double latitude, double longitude) {
        String json = "";
        try {

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.WHISHLIST_OFFERS_PID, true, WebServiceDetails.WHISHLIST_OFFERS).execute();
    }

    /**
     * When GPS turned off or on, will listen over here
     *
     * @param event
     */

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {

            case GpsStatus.GPS_EVENT_STARTED:
                Log.e(TAG, "onGpsStatusChanged started");
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                Intent intent = new Intent("com.hashmybag.location");
                intent.putExtra("gpsStatus", false);
                sendBroadcast(intent);
                break;

        }
    }

    /*private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        if(!check()){
                            buildAlertMessageNoGps();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        if(!check()){
                           // buildAlertMessageNoGps();
                        }
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean check(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled){
            return true;
        }else{
            return false;
        }
    }*/
}
