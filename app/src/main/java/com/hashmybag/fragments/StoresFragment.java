package com.hashmybag.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.MainActivity;
import com.hashmybag.NearByStore;
import com.hashmybag.R;
import com.hashmybag.StoreDetail;
import com.hashmybag.adapters.StoreAdapter;
import com.hashmybag.beans.AllStoreBean;
import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for showing all stores for HMB user *
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-27
 */

public class StoresFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static boolean isLaunch = false;
    private static boolean isCalling = false;
    EditText searchBox;
    ListView lst_view, lst_view_search;
    ArrayList<AllStoreBean> allStoreList = new ArrayList<>();
    ArrayList<AllStoreBean> allStoresearchList = new ArrayList<>();
    StoreAdapter adapter;
    ImageView search_view;
    RelativeLayout headerLayout;
    ImageView button_search_back;
    ConnectionDetector cd;
    TextView empltyView, subscripton_image_noti;
    SwipeRefreshLayout refreshLayout;
    ArrayList<AllStoreBean> allStoreBeen1 = new ArrayList<>();
    ProgressDialog progressDialog;
    private int off_set = 0;
    private int per_page = 20;
    private String message, responseNotifResponse;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.GET_ALL_STORES_PID:
                    responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        message = jsonResponse.optString("message");
                        String status = jsonResponse.optString("status");
                        allStoreList.clear();
                        if (status.equalsIgnoreCase("200")) {
                            JSONObject data = jsonResponse.getJSONObject("data");

                            int followed_count = data.optInt("followed_count");
                            SharedpreferenceUtility.getInstance(getActivity()).putInt(Constants.SUBSCRIBED_STORE, followed_count);
                            if (!String.valueOf(followed_count).equalsIgnoreCase("")) {
                                if (followed_count > 1) {
                                    subscripton_image_noti.setVisibility(View.VISIBLE);
                                    subscripton_image_noti.setText(String.valueOf(followed_count));
                                } else {
                                    subscripton_image_noti.setVisibility(View.GONE);
                                }
                            } else {
                                subscripton_image_noti.setVisibility(View.GONE);
                            }

                            JSONArray stores = data.getJSONArray("stores");
                            for (int i = 0; i < stores.length(); i++) {
                                JSONObject jsonResponse1 = stores.getJSONObject(i);
                                String stror_id = jsonResponse1.optString("id");
                                String stror_email = jsonResponse1.optString("email");
                                String stror_photo = jsonResponse1.optString("photo");
                                String stror_latitude = jsonResponse1.optString("latitude");
                                String stror_longitude = jsonResponse1.optString("longitude");
                                String stror_address1 = jsonResponse1.optString("address1");
                                String stror_title = jsonResponse1.optString("title");
                                String stror_description = jsonResponse1.optString("description");
                                String mobile_number = jsonResponse1.optString("mobile_number");
                                String category_id = jsonResponse1.optString("category_id");
                                String store_active = jsonResponse1.optString("active");
                                String twitter_username = jsonResponse1.optString("twitter_username");
                                String facebook_page = jsonResponse1.optString("facebook_page");
                                String instagram_user_name = jsonResponse1.optString("instagram_user_name");
                                String following = jsonResponse1.optString("following");

                                AllStoreBean allStoreBean = new AllStoreBean();
                                allStoreBean.setStore_id(stror_id);
                                allStoreBean.setStore_email(stror_email);
                                allStoreBean.setStore_photo(stror_photo);
                                allStoreBean.setStore_latitude(stror_latitude);
                                allStoreBean.setStore_longitude(stror_longitude);
                                allStoreBean.setStore_address1(stror_address1);
                                allStoreBean.setStore_title(stror_title);
                                allStoreBean.setStore_description(stror_description);
                                allStoreBean.setMobile_number(mobile_number);
                                allStoreBean.setStore_category_id(category_id);
                                allStoreBean.setStore_active(store_active);
                                allStoreBean.setStore_twitter_username(twitter_username);
                                allStoreBean.setStore_facebook_page(facebook_page);
                                allStoreBean.setStore_instagram_user_name(instagram_user_name);
                                allStoreBean.setStore_following(following);
                                allStoreList.add(allStoreBean);
                            }
                            lst_view_search.setVisibility(View.GONE);
                            lst_view.setVisibility(View.VISIBLE);
                            //allStoreBeen.addAll(allStoreBeen1);
                            adapter = new StoreAdapter(getActivity(), allStoreList, subscripton_image_noti);
                            lst_view.setAdapter(adapter);

                            refreshLayout.setRefreshing(false);
                            //adapter.notifyDataSetChanged();
                        } else if (status.equals("401")) {
                            refreshLayout.setRefreshing(false);
                            startActivity(new Intent(getContext(), MainActivity.class));
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(Constants.CUSTOMER_LOGIN_OR_NOT, false);
                        }
                    } catch (JSONException v) {
                        v.printStackTrace();
                        //  getStores();
                        off_set = 0;
                        refreshLayout.setRefreshing(false);

                        System.out.print("-----" + responseNotifResponse);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Does not find store!", Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                        System.out.print("-----" + e.getMessage() + "---" + responseNotifResponse);
                        e.printStackTrace();
                        refreshLayout.setRefreshing(false);

                    }

                    break;


                case WebServiceDetails.SEARCH_STORES_BY_TITLE_PID:
                    responseNotifResponse = (String) msg.obj;
                    allStoreList = new ArrayList<AllStoreBean>();
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        message = jsonResponse.optString("message");
                        allStoreList.clear();
                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {
                            allStoresearchList.clear();
                            JSONObject user = jsonResponse.getJSONObject("data");
                            JSONArray stores = user.getJSONArray("stores");
                            for (int i = 0; i < stores.length(); i++) {
                                JSONObject jsonResponse1 = stores.getJSONObject(i);
                                String stror_id = jsonResponse1.optString("id");
                                String stror_email = jsonResponse1.optString("email");
                                String stror_photo = jsonResponse1.optString("photo");
                                String stror_latitude = jsonResponse1.optString("latitude");
                                String stror_longitude = jsonResponse1.optString("longitude");
                                String stror_address1 = jsonResponse1.optString("address1");
                                String stror_title = jsonResponse1.optString("title");
                                String stror_description = jsonResponse1.optString("description");
                                String mobile_number = jsonResponse1.optString("mobile_number");
                                String category_id = jsonResponse1.optString("category_id");
                                String store_active = jsonResponse1.optString("active");
                                String twitter_username = jsonResponse1.optString("twitter_username");
                                String facebook_page = jsonResponse1.optString("facebook_page");
                                String instagram_user_name = jsonResponse1.optString("instagram_user_name");
                                String following = jsonResponse1.optString("following");


                                AllStoreBean allStoreBean = new AllStoreBean();
                                allStoreBean.setStore_id(stror_id);
                                allStoreBean.setStore_email(stror_email);
                                allStoreBean.setStore_photo(stror_photo);
                                allStoreBean.setStore_latitude(stror_latitude);
                                allStoreBean.setStore_longitude(stror_longitude);
                                allStoreBean.setStore_address1(stror_address1);
                                allStoreBean.setStore_title(stror_title);
                                allStoreBean.setStore_description(stror_description);
                                allStoreBean.setMobile_number(mobile_number);
                                allStoreBean.setStore_category_id(category_id);
                                allStoreBean.setStore_active(store_active);
                                allStoreBean.setStore_twitter_username(twitter_username);
                                allStoreBean.setStore_facebook_page(facebook_page);
                                allStoreBean.setStore_instagram_user_name(instagram_user_name);
                                allStoreBean.setStore_following(following);
                                allStoresearchList.add(allStoreBean);
                            }
                            lst_view_search.setVisibility(View.VISIBLE);
                            //lst_view_search.setEmptyView(empltyView);
                            lst_view.setVisibility(View.GONE);
                            lst_view_search.setAdapter(adapter);
                            //allStoreBeen.clear();
                            //allStoreBeen.addAll(allStoreBeen1);
                            adapter = new StoreAdapter(getActivity(), allStoresearchList, subscripton_image_noti);
                            lst_view_search.setAdapter(adapter);


                        } else if (status.equals("401")) {
                            startActivity(new Intent(getContext(), MainActivity.class));
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(Constants.CUSTOMER_LOGIN_OR_NOT, false);
                        }
                    } catch (JSONException v) {
                        Toast.makeText(getContext(), "No Store found  for this title !", Toast.LENGTH_LONG).show();
                        v.printStackTrace();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "No Store found  for this title !", Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stores_layout, container, false);
        searchBox = (EditText) view.findViewById(R.id.search_edittext);
        lst_view = (ListView) view.findViewById(R.id.lst_view);
        lst_view_search = (ListView) view.findViewById(R.id.lst_view_search);
        empltyView = (TextView) view.findViewById(R.id.empltyView);
        cd = new ConnectionDetector(getContext());
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        subscripton_image_noti = (TextView) getActivity().findViewById(R.id.subscripton_image_noti);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);
        adapter = new StoreAdapter(getActivity(), allStoreList, subscripton_image_noti);
        lst_view.setAdapter(adapter);

        button_search_back = (ImageView) view.findViewById(R.id.search_view_back);
        search_view = (ImageView) view.findViewById(R.id.search_view);

        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.VISIBLE);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getStores();
            }
        });


        lst_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent intent = new Intent(getActivity(), StoreDetail.class);
                    intent.putExtra("id", allStoreList.get(position).getStore_id());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "You don't have internet connection!", Toast.LENGTH_LONG).show();
                }

            }
        });

        lst_view_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<String> arrayList1 = new ArrayList<String>();

                /*Setting all details for Stores*/

                arrayList1.add(allStoresearchList.get(position).getStore_id());
                arrayList1.add(allStoresearchList.get(position).getStore_photo());
                arrayList1.add(allStoresearchList.get(position).getStore_latitude());
                arrayList1.add(allStoresearchList.get(position).getStore_longitude());
                arrayList1.add(allStoresearchList.get(position).getStore_address1());
                arrayList1.add(allStoresearchList.get(position).getStore_title());
                arrayList1.add(allStoresearchList.get(position).getStore_description());

                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent intent = new Intent(getActivity(), NearByStore.class);
                    intent.putStringArrayListExtra("data", arrayList1);
                    getActivity().startActivity(intent);


                } else {
                    Toast.makeText(getContext(), "You don't have internet connection!", Toast.LENGTH_LONG).show();
                }

            }
        });


        button_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStores();
                off_set = 0;
                per_page = 20;
                searchBox.setText("");
            }
        });

        /*Search box listener to listen on every key press*/

        searchBox.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchtext = searchBox.getText().toString();
                if (searchtext.equalsIgnoreCase("")) {
                    getStores();
                    //off_set=0;
                } else {
                    searchStore(searchtext);
                }
            }
        });

        searchBox.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                if (v.getId() == searchBox.getId())

                {

                    searchBox.setCursorVisible(true);

                }

            }

        });
        hideKeyboard(getActivity());
        return view;
    }

    /*Web service call for getting all stores for the user*/

    @Override
    public void onRefresh() {
        off_set = off_set + 20;
        //per_page=per_page+20;
        getStores();
    }

    /*Search by title*/

    @Override
    public void onResume() {
        super.onResume();
        searchBox.setText("");
        searchBox.setCursorVisible(false);
        lst_view_search.setVisibility(View.GONE);
        lst_view.setVisibility(View.VISIBLE);
      /*  if (allStoreList.size()>0) {
            adapter = new StoreAdapter(getActivity(), allStoreList);
            lst_view.setAdapter(adapter);
            refreshLayout.setRefreshing(false);
        } else {
            off_set = 0;
            per_page=20;
            getStores();
        }*/
        //if(first_time==true){
        getStores();
        //  first_time = false;
        // }

    }

    protected String addLocationToUrl(String url) {
        if (!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("offset", "0"));
        params.add(new BasicNameValuePair("per_page", "20"));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }

    private void getStores() {
       /* new WebRequestTask(getActivity(), _handler, Constants.GET_METHOD,
                WebServiceDetails.GET_ALL_STORES_PID, false, addLocationToUrl(WebServiceDetails.GETALL_STORES)).execute();
*/
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String responseString = null;
                HttpClient httpClient = new DefaultHttpClient();
                try {

                    URI uri = new URI(addLocationToUrl(WebServiceDetails.GETALL_STORES));
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
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //progressDialog.dismiss();
                System.out.println("" + s);
                Log.v("response :", "" + s);
                try {
                    JSONObject jsonResponse = new JSONObject(s);
                    message = jsonResponse.optString("message");
                    String status = jsonResponse.optString("status");
                    allStoreList.clear();
                    if (status.equalsIgnoreCase("200")) {
                        JSONObject data = jsonResponse.getJSONObject("data");

                        int followed_count = data.optInt("followed_count");
                        SharedpreferenceUtility.getInstance(getActivity()).putInt(Constants.SUBSCRIBED_STORE, followed_count);
                        if (!String.valueOf(followed_count).equalsIgnoreCase("")) {
                            if (followed_count > 1) {
                                subscripton_image_noti.setVisibility(View.VISIBLE);
                                subscripton_image_noti.setText(String.valueOf(followed_count));
                            } else {
                                subscripton_image_noti.setVisibility(View.GONE);
                            }
                        } else {
                            subscripton_image_noti.setVisibility(View.GONE);
                        }

                        JSONArray stores = data.getJSONArray("stores");
                        for (int i = 0; i < stores.length(); i++) {
                            JSONObject jsonResponse1 = stores.getJSONObject(i);
                            String stror_id = jsonResponse1.optString("id");
                            String stror_email = jsonResponse1.optString("email");
                            String stror_photo = jsonResponse1.optString("photo");
                            String stror_latitude = jsonResponse1.optString("latitude");
                            String stror_longitude = jsonResponse1.optString("longitude");
                            String stror_address1 = jsonResponse1.optString("address1");
                            String stror_title = jsonResponse1.optString("title");
                            String stror_description = jsonResponse1.optString("description");
                            String mobile_number = jsonResponse1.optString("mobile_number");
                            String category_id = jsonResponse1.optString("category_id");
                            String store_active = jsonResponse1.optString("active");
                            String twitter_username = jsonResponse1.optString("twitter_username");
                            String facebook_page = jsonResponse1.optString("facebook_page");
                            String instagram_user_name = jsonResponse1.optString("instagram_user_name");
                            String following = jsonResponse1.optString("following");

                            AllStoreBean allStoreBean = new AllStoreBean();
                            allStoreBean.setStore_id(stror_id);
                            allStoreBean.setStore_email(stror_email);
                            allStoreBean.setStore_photo(stror_photo);
                            allStoreBean.setStore_latitude(stror_latitude);
                            allStoreBean.setStore_longitude(stror_longitude);
                            allStoreBean.setStore_address1(stror_address1);
                            allStoreBean.setStore_title(stror_title);
                            allStoreBean.setStore_description(stror_description);
                            allStoreBean.setMobile_number(mobile_number);
                            allStoreBean.setStore_category_id(category_id);
                            allStoreBean.setStore_active(store_active);
                            allStoreBean.setStore_twitter_username(twitter_username);
                            allStoreBean.setStore_facebook_page(facebook_page);
                            allStoreBean.setStore_instagram_user_name(instagram_user_name);
                            allStoreBean.setStore_following(following);
                            allStoreList.add(allStoreBean);
                        }
                        lst_view_search.setVisibility(View.GONE);
                        lst_view.setVisibility(View.VISIBLE);
                        //allStoreBeen.addAll(allStoreBeen1);
                        adapter = new StoreAdapter(getActivity(), allStoreList, subscripton_image_noti);
                        lst_view.setAdapter(adapter);

                        refreshLayout.setRefreshing(false);
                        //adapter.notifyDataSetChanged();
                    } else if (status.equals("401")) {
                        refreshLayout.setRefreshing(false);
                        startActivity(new Intent(getContext(), MainActivity.class));
                        SharedpreferenceUtility.getInstance(getActivity()).putBoolean(Constants.CUSTOMER_LOGIN_OR_NOT, false);
                    }
                } catch (JSONException v) {
                    v.printStackTrace();
                    //  getStores();
                    off_set = 0;
                    refreshLayout.setRefreshing(false);

                    System.out.print("-----" + responseNotifResponse);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Does not find store!", Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                    System.out.print("-----" + e.getMessage() + "---" + responseNotifResponse);
                    e.printStackTrace();
                    refreshLayout.setRefreshing(false);

                }

            }
        }.execute();


    }

    /*Handler to handle JSON response to get all stores and search by title*/

    private void searchStore(String title) {
        String json = "";
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("search_by", "title");
            jsonObject.put("title", title);
            jsonObject.put("per_page", "20");
            jsonObject.put("offset", "0");

            json = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(getActivity(), json, _handler, Constants.POST_METHOD,
                WebServiceDetails.SEARCH_STORES_BY_TITLE_PID, false, WebServiceDetails.SEARCH_STORES_BY_TITLE).execute();
    }


}
