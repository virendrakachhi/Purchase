package com.hashmybag.cloudinaryintegration;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hashmybag.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for handling image uploadings to cloudinary.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-04
 */

public class CloudinaryImageUploader extends AsyncTask<String, Void, Boolean> {
    String imagePath;
    Context context;
    String finalImage, customerId;
    int what;
    private ProgressDialog pDialog;
    private boolean showDialog;
    private Handler handler;
    private Cloudinary mCloudinary;

    /**
     * Constructor for this class which accepting following parameters from callie.
     *
     * @param context
     * @param handler
     * @param imagePath
     * @param showDialog
     * @param customerId
     */

    public CloudinaryImageUploader(Context context, int what, Handler handler, String imagePath, boolean showDialog, String customerId) {
        this.imagePath = imagePath;
        this.context = context;
        this.what = what;
        this.customerId = customerId;
        this.showDialog = showDialog;
        this.handler = handler;
        setCloudnaryCredentials();
    }

    /**
     * Pre Execution of asynctask i.e before uploading image this will execute
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showDialog) {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Uploading Image..");
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Boolean send = true;
        try {
            Long tsLong = System.currentTimeMillis() / 1000;
            String timestamp = tsLong.toString();
            finalImage = timestamp + customerId;
            mCloudinary.uploader().upload(imagePath, ObjectUtils.asMap("public_id", finalImage));
            // mCloudinary.url().generate(user_id+".jpg");

            Log.d("final image path : ", finalImage);
        } catch (Exception e) {
            e.getStackTrace();
            send = false;
        }
        return send;
    }

    /**
     * Post Execution of asynctask i.e after uploading image this will execute
     */

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        pDialog.dismiss();
        JSONObject jsonObject = new JSONObject();
        if (s) {
            String imageUrl = "http://res.cloudinary.com/dk01turm1/image/upload/" + finalImage + ".jpg";
            try {
                jsonObject.put("status", 200);
                jsonObject.put("image_url", imageUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject.put("status", 400);
                jsonObject.put("image_url", "");
            } catch (JSONException ek) {
                ek.printStackTrace();
            }
        }

        Message message = handler.obtainMessage();
        message.obj = jsonObject.toString();
        message.what = this.what;
        handler.sendMessage(message);
    }

    /**
     * Setting cloudinary credentials including key, appsecret, url and cloud name
     */

    private void setCloudnaryCredentials() {
        Map config = new HashMap();
        config.put(Constants.CLOUD_NAME, Constants.CLOUD_NAME_VALUE);
        config.put(Constants.API_KEY, Constants.API_KEY_VALUE);
        config.put(Constants.API_SECRET, Constants.API_SECRET_VALUE);
        mCloudinary = new Cloudinary(Constants.CLOUDINARY_URL);
    }
}
