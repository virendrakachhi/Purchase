package com.hashmybag.util;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.hashmybag.R;

/**
 * Created by cp-android on 3/5/16.
 */
public class LocationTracker extends Service implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private final Context mContext;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    public LocationTracker(Context context) {
        this.mContext = context;
        getLocation();


    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                try {
                    Intent intent1 = new Intent();
                    intent1.setAction("com.hashmybag.locationalert");
                    mContext.sendBroadcast(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        boolean permission = ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                        boolean permission1 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                        if (permission && permission1) {
                            // TODO: Consider calling
                            Log.d("GPS Enabled", "GPS Enabled");

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    return location;
                                }

                            }
                        } else {
                            Toast.makeText(mContext, "You have denied permission for Reading Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        } catch (SecurityException se) {
            se.printStackTrace();
            Toast.makeText(mContext, "You have denied permission for Reading Location", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Unable to find location", Toast.LENGTH_SHORT).show();
        }


        return location;
    }

    private void openDialog() {
       /* ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        String topActivityName = tasks.get(0).topActivity.getClassName();
        if (!topActivityName.equals("com.hashmybag.pushnotifications.Alert")) {
            Intent gotoa = new Intent(mContext, Alert.class);
            gotoa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(gotoa);
        }
        Log.d("Current Activity", topActivityName);
*/
    }

    public void turnGPSOn() {
        try {
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", true);
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
            openDialog();
        }


    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppTheme);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            // Showing Alert Message
            alertDialog.show();


           /* AlertDialog alert = alertDialog.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.show();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
