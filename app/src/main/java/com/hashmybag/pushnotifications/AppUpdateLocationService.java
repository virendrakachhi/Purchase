package com.hashmybag.pushnotifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used for implementing UpdateLocation feature to the app.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-05-27
 */

public class AppUpdateLocationService extends Service implements LocationListener {
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


                            // Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "We will send you new offer based on your wishlist items.", Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Context context;
    private String USER_ID;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        new CountDownTimer(Long.MAX_VALUE, 1000 * 60) {
            public void onTick(long millisUntilFinished) {

                USER_ID = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_ID);

               /* //String lat="23.181467";
                //String lon="79.986407";
                Location locationp=getPreviousLocation();
                Location locationc = new LocationTracker(context).getLocation();
                  if ((USER_ID != null) && (locationp != null)&& (locationc != null)) {
                      double latitude = locationc.getLatitude();
                      double longitude = locationc.getLongitude();
                      Log.d("P Loc User", USER_ID + " " + latitude + " " + longitude);
                      Log.v("C Latitude :",String.valueOf(locationp.getLatitude()));
                      Log.v("C Longitude :",String.valueOf(locationp.getLongitude()));

                    //getting distance fro latlong
                    double distance = distance(locationp.getLatitude(),locationp.getLongitude(), latitude, longitude);

                    //verify that distance upto 15km from your previous location
                    if (distance > 15) {
                        getWhishListOffer(latitude,longitude);
                        SharedpreferenceUtility.getInstance(context).putString(Constants.CUSTOMER_LATITUDE, String.valueOf(latitude));
                        SharedpreferenceUtility.getInstance(context).putString(Constants.CUSTOMER_LONGITUDE, String.valueOf(longitude));
                    }
                }else {
                    Toast.makeText(context,"Unable to find distance",Toast.LENGTH_SHORT).show();
                }*/
            }

            public void onFinish() {
            }

        }.start();

        return START_STICKY;
    }

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
     * Handler to handle JSON response  coming from the get OTP API
     * OTP handling
     */
    private Location getPreviousLocation() {

        String lat = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_LATITUDE);
        String lon = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_LONGITUDE);
        if ((lat == null) || (lat.equalsIgnoreCase("")) || (lat.equalsIgnoreCase("null"))) {
            if ((lon == null) || (lon.equalsIgnoreCase("")) || (lon.equalsIgnoreCase("null"))) {
              /*  Location location = new LocationTracker(context).getLocation();
                if (location != null) {
                    SharedpreferenceUtility.getInstance(context).putString(Constants.CUSTOMER_LATITUDE, String.valueOf(location.getLongitude()));
                    SharedpreferenceUtility.getInstance(context).putString(Constants.CUSTOMER_LONGITUDE, String.valueOf(location.getLongitude()));
                    return location;
                }else{
                    return null;
                }*/
            }
        } else {
            Location loc = new Location("test");
            loc.setLatitude(Double.parseDouble(lat));
            loc.setLongitude(Double.parseDouble(lon));
            return loc;
        }
        return null;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
