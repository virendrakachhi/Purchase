package com.hashmybag.servercommunication;

/**
 * This class is used for calling web services, and
 * all the methods are implemented over here
 * i.e Get,post and  all
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-21
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.List;

public class WebRequestTask extends AsyncTask<Void, Integer, Void> {
    static String link;
    static String method, json;
    ConnectionDetector cd;
    private ProgressDialog dialog;
    private Context context;
    private Handler handler;
    private List<NameValuePair> params;
    private int what;
    private boolean showDialog;
    private String AUTH_TOKEN;


    public WebRequestTask(Context context,
                          Handler handler, String method, int what, boolean showDiaolog, String link) {
        this.context = context;
        this.handler = handler;
        this.what = what;
        this.showDialog = showDiaolog;
        this.link = link;
        this.method = method;
        cd = new ConnectionDetector(context);

    }

    public WebRequestTask(Context context) {
        this.context = context;
    }

    public WebRequestTask(Context context, String json,
                          Handler handler, String method, int what, boolean showDiaolog, String link) {
        this.context = context;
        this.handler = handler;
        this.json = json;
        this.what = what;
        this.showDialog = showDiaolog;
        this.link = link;
        this.method = method;
        cd = new ConnectionDetector(context);


    }

    /**
     * Pre execution for the method and web service call
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (cd.isConnectingToInternet()) {
                if (showDialog) {
                    dialog = new ProgressDialog(context);
                    dialog.setCancelable(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                }
            } else {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Pre execution for the method and web service call
     */

    @Override
    protected void onPostExecute(Void result) {
        try {
            if (showDialog && dialog != null)
                dialog.dismiss();
            super.onPostExecute(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This all are back ground task which will come into the web service call
     * i.e get,post methods
     *
     * @param params
     * @return
     */


    @Override
    protected Void doInBackground(Void... params) {
        HttpClient httpClient = new DefaultHttpClient();

        switch (method) {
            case "GET":
                try {
                    URI uri = new URI(link);
                    HttpGet httpGet = new HttpGet(uri);
                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;

                    httpGet.setHeader("Authorization", strReq);
                    httpGet.setHeader("Content-type", "application/json");
                    httpGet.setHeader("Accept", "*/*");

                    if (SharedpreferenceUtility.getInstance(context).getBoolean(Constants.CUSTOMER_LOGIN_OR_NOT)) {
                        AUTH_TOKEN = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN);
                        Log.v("", "AUTH-TOKEN: " + AUTH_TOKEN);
                        httpGet.setHeader("AUTH-TOKEN", SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN));
                    }

                    HttpResponse response = httpClient.execute(httpGet);
                    String responseString = EntityUtils.toString(response.getEntity());
                    Log.v("", "responsessdf : " + responseString);
                    Message message = handler.obtainMessage();
                    message.obj = responseString;
                    message.what = this.what;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();

                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;

            case "POST":
                HttpPost httpPost = new HttpPost(link);

                try {
                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;
                    httpPost.setHeader("Authorization", strReq);
                    httpPost.setHeader("Content-type", "application/json");
                    httpPost.setHeader("Accept", "*/*");
                    if (SharedpreferenceUtility.getInstance(context).getBoolean(Constants.CUSTOMER_LOGIN_OR_NOT)) {
                        AUTH_TOKEN = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN);
                        Log.v("", "AUTH-TOKEN: " + AUTH_TOKEN);
                        httpPost.setHeader("Auth-Token", SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN));
                    }
                    StringEntity se = new StringEntity(json);
                    httpPost.setEntity(se);
                    HttpResponse response = httpClient.execute(httpPost);
                    String responseString = EntityUtils.toString(response.getEntity());
                    Log.v("", "responsessdf : " + responseString);
                    Message message = handler.obtainMessage();
                    message.obj = responseString;
                    message.what = this.what;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;
            case "PUT":
                HttpPut httpPut = new HttpPut(link);
                try {

                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;

                    httpPut.setHeader("Authorization", strReq);
                    //httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPut);
                    String responseString = EntityUtils.toString(response.getEntity());
                    Log.v("", "responsessdf : " + responseString);
                    Message message = handler.obtainMessage();
                    message.obj = responseString;
                    message.what = this.what;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;
            case "PATCH":
                HttpPatch httpPatch = new HttpPatch(link);
                try {

                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;

                    StringEntity se = new StringEntity(json);

                    httpPatch.setHeader("Authorization", strReq);
                    httpPatch.setHeader("Content-type", "application/json");

                    httpPatch.setHeader("Accept", "*/*");
                    // httpPatch.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    if (SharedpreferenceUtility.getInstance(context).getBoolean(Constants.CUSTOMER_LOGIN_OR_NOT)) {
                        AUTH_TOKEN = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN);
                        httpPatch.setHeader("AUTH-TOKEN", SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN));
                    }
                    httpPatch.setEntity(se);

                    HttpResponse response = httpClient.execute(httpPatch);
                    String responseString = EntityUtils.toString(response.getEntity());
                    Log.v("", "responsessdf : " + responseString);
                    Message message = handler.obtainMessage();
                    message.obj = responseString;
                    message.what = this.what;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;
            case "DELETE":

                HttpDelete httpDelete = new HttpDelete(link);

                try {
                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;

                    httpDelete.setHeader("Authorization", strReq);
                    // httpDelete.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpDelete);
                    String responseString = EntityUtils.toString(response.getEntity());
                    Log.v("", "responsessdf : " + responseString);
                    Message message = handler.obtainMessage();
                    message.obj = responseString;
                    message.what = this.what;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;
            case "OPTIONS":
                HttpOptions httpOptions = new HttpOptions(link);
                try {
                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;
                    httpOptions.setHeader("Authorization", strReq);
                    //   httpOptions.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpOptions);
                    String responseString = EntityUtils.toString(response.getEntity());
                    Log.v("", "responsessdf : " + responseString);
                    Message message = handler.obtainMessage();
                    message.obj = responseString;
                    message.what = this.what;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;
            case "HEAD":
                HttpHead httpHead = new HttpHead(link);

                try {
                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;

                    httpHead.setHeader("Authorization", strReq);
                    // httpHead.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpHead);
                    String responseString = EntityUtils.toString(response.getEntity());
                    Log.v("", "responsessdf : " + responseString);
                    Message message = handler.obtainMessage();
                    message.obj = responseString;
                    message.what = this.what;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();

                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;

        }

        return null;
    }


}

