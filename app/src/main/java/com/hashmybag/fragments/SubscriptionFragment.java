package com.hashmybag.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hashmybag.R;
import com.hashmybag.adapters.SubscriptionAdapter;
import com.hashmybag.beans.SubscriptionBean;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

/**
 * This class is used for manage stores subscription for the user *
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-22
 */

public class SubscriptionFragment extends Fragment {

    ImageView back_arrow;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TabScreen tabScreen;
    RelativeLayout headerLayout;
    ListView listView;
    ImageLoader loader;
    TextView textView;
    ProgressDialog progressDialog;
    /*Handler to handle JSON response for subscribed stores*/
    String message;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            loader = ImageLoader.getInstance();

            switch (msg.what) {
                case WebServiceDetails.GET_SUBSCRIBED_LIST_PID:
                    String responseNotifResponse = (String) msg.obj;

                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    ArrayList<SubscriptionBean> arrayList1 = new ArrayList<>();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        String status = jsonResponse.optString("status");
                        message = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {

                            JSONObject data = jsonResponse.getJSONObject("data");
                            JSONArray connections = data.getJSONArray("connections");

                            for (int i = 0; i < connections.length(); i++) {
                                JSONObject jsonResponse1 = connections.getJSONObject(i);
                                String stror_id = jsonResponse1.optString("id");
                                String stror_email = jsonResponse1.optString("email");
                                String stror_latitude = jsonResponse1.optString("latitude");
                                String stror_longitude = jsonResponse1.optString("longitude");
                                String stror_address1 = jsonResponse1.optString("address1");
                                String stror_title = jsonResponse1.optString("title");
                                String stror_description = jsonResponse1.optString("description");
                                String stror_photo = jsonResponse1.optString("photo");
                                SubscriptionBean bean = new SubscriptionBean();

                                bean.setStoreName(stror_title);
                                bean.setStoreImage(stror_photo);
                                bean.setStoreAddress(stror_address1);
                                bean.setStoreId(stror_id);
                                arrayList1.add(bean);
                            }

                            SubscriptionAdapter adapter = new SubscriptionAdapter(arrayList1, getActivity());
                            listView.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        //Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    } catch (NullPointerException e) {

                        //Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Function to hide keyboard on click outside the board
     *
     * @param ctx
     */

    public static void hideKeyboard(Context ctx) {


        try {
            InputMethodManager inputManager = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            // check if no view has focus:
            View v = ((Activity) ctx).getCurrentFocus();
            if (v == null)
                return;

            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*Web service call for get subscribed stroes detail*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subsciption_layout, container, false);

        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);
        tabScreen = new TabScreen();
        listView = (ListView) view.findViewById(R.id.list_item);
        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        textView = (TextView) view.findViewById(R.id.empltyView);

        fragmentManager = getActivity().getSupportFragmentManager();
        tabScreen = new TabScreen();

        if (container == null) {
            listView.setVisibility(View.GONE);
        }

        getSubscription();
        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, tabScreen);
                fragmentTransaction.commit();
            }
        });
        hideKeyboard(getActivity());


        listView.setEmptyView(textView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSubscription();
    }

    private void getSubscription() {


        /*new WebRequestTask(getActivity(), _handler, Constants.GET_METHOD, WebServiceDetails.GET_SUBSCRIBED_LIST_PID, true,
                WebServiceDetails.GET_SUBSCRIBED_LIST + id + "/connections").execute();
*/
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String responseString = null;
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    String id = SharedpreferenceUtility.getInstance(getContext()).getString(Constants.CUSTOMER_ID);
                    URI uri = new URI(WebServiceDetails.GET_SUBSCRIBED_LIST + id + "/connections");
                    HttpGet httpGet = new HttpGet(uri);
                    String strConcat = "Test";
                    strConcat = strConcat.concat(":" + "Test");
                    String str = Base64.encodeToString(strConcat.getBytes(), 0);
                    str = str.replace("\n", "");
                    String strReq = "Basic " + str;

                    httpGet.setHeader("Authorization", strReq);
                    httpGet.setHeader("Content-type", "application/json");
                    httpGet.setHeader("Accept", "*/*");

                    if (SharedpreferenceUtility.getInstance(getActivity()).getBoolean(Constants.CUSTOMER_LOGIN_OR_NOT)) {
                        String AUTH_TOKEN = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN);
                        Log.v("", "AUTH-TOKEN: " + AUTH_TOKEN);
                        httpGet.setHeader("AUTH-TOKEN", SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN));
                    }

                    HttpResponse response = httpClient.execute(httpGet);
                    responseString = EntityUtils.toString(response.getEntity());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return responseString;
            }

            @Override
            protected void onPostExecute(String responseNotifResponse) {
                super.onPostExecute(responseNotifResponse);
                progressDialog.dismiss();
                System.out.println("" + responseNotifResponse);
                Log.v("response :", "" + responseNotifResponse);
                ArrayList<SubscriptionBean> arrayList1 = new ArrayList<>();
                try {
                    JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                    String status = jsonResponse.optString("status");
                    message = jsonResponse.optString("message");
                    if (status.equalsIgnoreCase("200")) {

                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray connections = data.getJSONArray("connections");

                        for (int i = 0; i < connections.length(); i++) {
                            JSONObject jsonResponse1 = connections.getJSONObject(i);
                            String stror_id = jsonResponse1.optString("id");
                            String stror_email = jsonResponse1.optString("email");
                            String stror_latitude = jsonResponse1.optString("latitude");
                            String stror_longitude = jsonResponse1.optString("longitude");
                            String stror_address1 = jsonResponse1.optString("address1");
                            String stror_title = jsonResponse1.optString("title");
                            String stror_description = jsonResponse1.optString("description");
                            String stror_photo = jsonResponse1.optString("photo");
                            SubscriptionBean bean = new SubscriptionBean();

                            bean.setStoreName(stror_title);
                            bean.setStoreImage(stror_photo);
                            bean.setStoreAddress(stror_address1);
                            bean.setStoreId(stror_id);
                            arrayList1.add(bean);
                        }

                        SubscriptionAdapter adapter = new SubscriptionAdapter(arrayList1, getActivity());
                        listView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    //Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } catch (NullPointerException e) {

                    //Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();

                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }.execute();


    }


}