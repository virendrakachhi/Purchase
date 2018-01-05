package com.hashmybag.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class MarshMallowPermission {
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 71;
    public static final int RECEIVE_SMS_PERMISSION_REQUEST_CODE = 72;
    public static final int READ_SMS_PERMISSION_REQUEST_CODE = 73;
    public static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 74;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 75;
    public static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 76;
    Activity activity;

    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionForExternalStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForRecieveSMS() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForReadSMS() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForLocation() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForContacts() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "ExternalStorage permission needed for recording. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForRecieveSMS() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_SMS)) {
            Toast.makeText(activity, "Recieve Sms Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForReadSMS() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
            Toast.makeText(activity, "Read Sms permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(activity, "Fine Location permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            Toast.makeText(activity, "Camera permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

    }

    public void requestPermissionForReadContact() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(activity, "Read Contact permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
        }

    }


}



