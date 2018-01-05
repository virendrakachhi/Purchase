package com.hashmybag;

/**
 * This is Main class which is executed firstly and handle all launching events in this.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.LocationTracker;
import com.hashmybag.util.SharedpreferenceUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    Button phone_button;
    EditText phone_edit, name_edit, email_edit;
    String otp;
    String name, email, mobile;
    /**
     * Handler to handle JSON response  coming from the get OTP API
     * OTP handling
     */

    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.SEND_OTP_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            String data = jsonResponse.optString("data");
                            final String message = jsonResponse.optString("message");
                            otp = data;


                            Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                            intent.putExtra("phone_number", mobile);
                            intent.putExtra("user_type", "customer");
                            intent.putExtra("otp", otp);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplication(), "Mobile was Wrong!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private ConnectionDetector cd;

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


  /* Email validation */

    public final static boolean isName(CharSequence target) {
        String pattern = "([a-z A-Z ']+\\s?)+[a-z A-Z ']+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(target.toString());
        if (matcher.matches())
            return true;
        else
            return false;
    }
  /* Name validation */

    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MainActivity.this.setTheme(R.style.NextTheme);

        cd = new ConnectionDetector(getApplicationContext());
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getPermission();
        } else {
            crashAnlytics();
        }
        if (SharedpreferenceUtility.getInstance(MainActivity.this).getBoolean(Constants.CUSTOMER_LOGIN_OR_NOT)) {
            Intent intentToHome = new Intent(MainActivity.this, AllFragmentsActivity.class);
            intentToHome.putExtra("from", "main");
            startActivity(intentToHome);
            finish();
        }

        setContentView(R.layout.activity_main);

        phone_button = (Button) findViewById(R.id.signupButton);
        name_edit = (EditText) findViewById(R.id.name_edit);
        email_edit = (EditText) findViewById(R.id.email_edit);
        phone_edit = (EditText) findViewById(R.id.phone_edit);

        phone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        phone_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void sendMessage() {
        name = name_edit.getText().toString();
        email = email_edit.getText().toString();
        mobile = phone_edit.getText().toString();
        if (name.equalsIgnoreCase("")) {
            Toast.makeText(getApplication(), "Please enter Name ", Toast.LENGTH_SHORT).show();
        } else if (email.equalsIgnoreCase("")) {
            Toast.makeText(getApplication(), "Please enter Email id ", Toast.LENGTH_SHORT).show();
        } else if (mobile.equalsIgnoreCase("")) {
            Toast.makeText(getApplication(), "Please enter Mobile number", Toast.LENGTH_SHORT).show();
        } else {
            if (isName(name)) {
                if (isValidEmail(email)) {
                    if (isValidPhoneNumber(mobile)) {
                        Boolean isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            if (check()) {
                                getOtp(name, email, mobile, "customer");
                                phone_button.setClickable(false);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "No Network Found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //phone_edit.setError("Please enter valid Mobile number");
                        Toast.makeText(getApplication(), "Please enter valid Mobile number", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //email_edit.setError("Please enter valid Email id");
                    Toast.makeText(getApplication(), "Please enter valid Email id", Toast.LENGTH_SHORT).show();

                }
            } else {
                //name_edit.setError("Please enter valid Name");
                Toast.makeText(getApplication(), "Please enter valid Name", Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * Permissions for 6.0 and above android devices
     *
     * @return
     */
    private void getPermission() {
        int storagereadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int storagewritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recieveMessagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readMessagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int readContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int locationpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int getaccountPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        int wakelockPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagereadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storagewritePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            crashAnlytics();
        }
        if (recieveMessagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (readMessagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (readContactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (locationpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarsePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (getaccountPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (wakelockPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 10);
        }

    }

    /**
     * Handling result of the permission requested
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == 10) {
            if (grantResults.length == 9) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Read External Storage permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Read External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    crashAnlytics();
                    //Toast.makeText(this, "Write External Storage permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Write External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Recieve Sms permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Recieve Sms permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Read Sms permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Read Sms permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(this, "Access Find Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Access Find Location permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[7] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Access Coarse Location permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Access Coarse Location permission denied", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[8] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Get Account permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Get Account permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void crashAnlytics() {
        //ExceptionHandler.register(this, "http://crashreporting.canopussystems.com/server/collect/hashmybag_error.php");
        //ExceptionHandler.register(this, "http://crashreporting.canopussystems.com/server/collect/server.php");
    }

    /**
     * Web service call for getting OTP after entering phone number
     *
     * @param mobile
     * @param type
     */

    private void getOtp(String name, String email, String mobile, String type) {

        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            Location location = new LocationTracker(MainActivity.this).getLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
            }
            //jsonObject.put("latitude", "23.181467");
            // jsonObject.put("longitude", "79.986407");

            jsonObject.put("title", name);
            jsonObject.put("email", email);
            jsonObject.put("username", mobile);
            jsonObject.put("user_type", type);
            json = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.SEND_OTP_PID, true, WebServiceDetails.SEND_OTP).execute();
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

    private boolean check() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            return true;
        } else {
            buildAlertMessageNoGps();
            return false;
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (check()) {
                getOtp(name, email, mobile, "customer");
                phone_button.setClickable(false);
            }
        }
    }
}
