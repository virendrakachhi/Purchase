package com.hashmybag;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
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
import com.hashmybag.util.NotificationView;
import com.hashmybag.util.SharedpreferenceUtility;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpActivity extends Activity {
    private static EditText otp_edit;
    private static String phone_number, user_type;
    TextView wait_text;
    Button proceed_button;
    Context context = this;
    String token;
    BroadcastReceiver otpReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (Object aPdusObj : pdusObj) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                        String senderAddress = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();
                        String name = (senderAddress.substring(senderAddress.length() - 6));//HMYBAG
                        if (name.equalsIgnoreCase("HMYBAG")) {
                            String str = (message.substring(message.length() - 7));
                            String otp = str.substring(0, str.length() - 1);
                            otp_edit.setText(otp);
                            getOtpVerification(otp);
                            //OtpActivity Sms = new OtpActivity();

                            // Sms.recivedSms(otp, context);
                            Notification();

                        }

                    }
                }

            } catch (NullPointerException e)

            {
                e.printStackTrace();
            }
        }
    };
    // Asyntask
    private ConnectionDetector cd;
    private String message, responseNotifResponse;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.OTP_VERIFICATION_PID:
                    responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        message = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {

                            JSONObject user = jsonResponse.getJSONObject("data");
                            JSONObject jsonResponse1 = user.getJSONObject("user");

                            String id = jsonResponse1.optString("id");
                            String email = jsonResponse1.optString("email");
                            String username = jsonResponse1.optString("title");
                            String authentication_token = jsonResponse1.optString("authentication_token");
                            String sign_up_stream = jsonResponse1.optString("sign_up_stream");
                            String latitude = jsonResponse1.optString("latitude");
                            String longitude = jsonResponse1.optString("longitude");
                            String address1 = jsonResponse1.optString("address1");
                            String location = jsonResponse1.optString("location");
                            String f_name = jsonResponse1.optString("first_name");
                            String l_name = jsonResponse1.optString("last_name");
                            String title = jsonResponse1.optString("title");
                            String description = jsonResponse1.optString("description");
                            String mobile_number = jsonResponse1.optString("mobile_number");
                            String landline_number = jsonResponse1.optString("landline_number");
                            String active = jsonResponse1.optString("active");
                            String photo = jsonResponse1.optString("photo");

                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_ID, id);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_EMAIL, email);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_USERNAME, username);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_AUTHENTICATION_TOKEN, authentication_token);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_SIGNUP_STREAM, sign_up_stream);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_LATITUDE, latitude);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_LONGITUDE, longitude);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_ADDRESS1, address1);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_LOCATION, location);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_F_NAME, f_name);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_L_NAME, l_name);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_TITLE, title);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_DESCRIPTION, description);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_MOBILE_NUMBER, mobile_number);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_LANDLINE, landline_number);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_ACTIVE, active);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_PHOTO, photo);
                            SharedpreferenceUtility.getInstance(OtpActivity.this).putBoolean(Constants.CUSTOMER_LOGIN_OR_NOT, true);

                            /*try {
                                if ((latitude == null) && (latitude.equalsIgnoreCase("")) && (latitude.equalsIgnoreCase("null"))) {
                                    if ((longitude == null) && (longitude.equalsIgnoreCase("")) && (longitude.equalsIgnoreCase("null"))) {
                                        Location location1 = new LocationTracker(context).getLocation();
                                        if (location1 != null) {
                                            double latitude1 = location1.getLatitude();
                                            double longitude1 = location1.getLongitude();
                                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_LATITUDE, String.valueOf(latitude1));
                                            SharedpreferenceUtility.getInstance(OtpActivity.this).putString(Constants.CUSTOMER_LONGITUDE, String.valueOf(longitude1));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
*/
                            // registraionIntoGCM();
                            //setDeviceInfo(token);
                            Intent intent = new Intent(OtpActivity.this, AllFragmentsActivity.class);
                            intent.putExtra("from", "main");
                            startActivity(intent);
                            finish();

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

    // Asyntask
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(OtpActivity.this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        OtpActivity.this.setTheme(R.style.NextTheme);
        setContentView(R.layout.activity_otp);
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(otpReciever, intentFilter);

        Intent i = getIntent();
        phone_number = i.getStringExtra("phone_number");
        user_type = i.getStringExtra("user_type");
        wait_text = (TextView) findViewById(R.id.wait_text);
        wait_text.setText("Please wait for the app to read OTP");
        wait_text.setTextColor(Color.parseColor("#ebebeb"));
        proceed_button = (Button) findViewById(R.id.proceedbutton);
        otp_edit = (EditText) findViewById(R.id.otp_edit1);
        // otp_edit.setText(i.getStringExtra("otp"));
        proceed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    getOtpVerification(otp_edit.getText().toString());
                } else {
                    Toast.makeText(OtpActivity.this, "No Network Found", Toast.LENGTH_LONG).show();
                }
            }
        });

        proceed_button.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Boolean isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        getOtpVerification(otp_edit.getText().toString());
                    } else {
                        Toast.makeText(OtpActivity.this, "No Network Found", Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("OtpActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("OtpActivity", "onPause");
    }

    private void getOtpVerification(String otp) {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone_number", phone_number);
            jsonObject.put("otp", otp);
            jsonObject.put("user_type", user_type);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.OTP_VERIFICATION_PID, true, WebServiceDetails.OTP_VERIFICATION).execute();
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

    public void Notification() {
        // Set Notification Title
        String strtitle = context.getString(R.string.customnotificationtitle);
        // Set Notification Text
        String strtext = context.getString(R.string.customnotificationtext);

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, NotificationView.class);
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.mipmap.notification_bag)
                        // Set Ticker Message
                .setTicker(context.getString(R.string.customnotificationticker))
                        // Set Title
                .setContentTitle(context.getString(R.string.customnotificationtitle))
                        // Set Text
                .setContentText(context.getString(R.string.customnotificationtext))
                        // Add an Action Button below Notification
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(otpReciever);
    }

}
