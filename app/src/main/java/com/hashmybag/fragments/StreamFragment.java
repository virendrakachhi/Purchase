package com.hashmybag.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hashmybag.MainActivity;
import com.hashmybag.R;
import com.hashmybag.adapters.StreamAdapter;
import com.hashmybag.beans.StreamBean;
import com.hashmybag.databasehandle.DatabaseHandler;
import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is used for showing all streams for HMB user *
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-27
 */

public class StreamFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ImageView back_arrow;
    TabScreen tabScreen;
    ConnectionDetector cd;
    RelativeLayout headerLayout;
    DatabaseHandler db;
    ArrayList<StreamBean> arrayList = new ArrayList<>();
    ArrayList<StreamBean> streamList = new ArrayList<>();
    ListView listView;
    TextView no_item;
    SwipeRefreshLayout refreshLayout;
    Context context;
    private StreamAdapter adapter;
    private String message;
    private Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.GET_STREAM_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        message = jsonResponse.optString("message");
                        arrayList.clear();
                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            JSONObject data = jsonResponse.getJSONObject("data");
                            JSONArray streams = data.getJSONArray("streams");
                            for (int i = 0; i < streams.length(); i++) {
                                JSONObject jsonResponse1 = streams.getJSONObject(i);

                                String ci = jsonResponse1.getJSONObject("stream").getString("channel_info");
                                if (!ci.equals("null")) {

                                    StreamBean streamBeans = new StreamBean();
                                    String streams_id = jsonResponse1.optString("id");
                                    String streams_communication_id = jsonResponse1.optString("communication_id");
                                    String streams_created_at = jsonResponse1.optString("created_at");
                                    String streams_status = jsonResponse1.optString("status");

                                    streamBeans.setStreamId(streams_id);
                                    streamBeans.setCommId(streams_communication_id);
                                    streamBeans.setCreateAt(streams_created_at);


                                    JSONObject stream = jsonResponse1.getJSONObject("stream");
                                    String stream_broadcast_id = stream.optString("broadcast_id");
                                    String stream_title = stream.optString("title");
                                    String stream_body = stream.optString("body");
                                    String stream_attachment_url = stream.optString("attachment_url");
                                    String stream_channel_id = stream.optString("channel_id");

                                    streamBeans.setBroadCastId(stream_broadcast_id);
                                    streamBeans.setTitle(stream_title);
                                    streamBeans.setDescription(stream_body);
                                    streamBeans.setImage_url(stream_attachment_url);
                                    streamBeans.setChannelId(stream_channel_id);

                                    JSONObject stream_channel_info = stream.getJSONObject("channel_info");

                                    String channel_info_id = stream_channel_info.optString("id");
                                    String channel_info_name = stream_channel_info.optString("name");
                                    String channel_info_description = stream_channel_info.optString("description");
                                    String channel_info_created_at = stream_channel_info.optString("created_at");
                                    String channel_info_updated_at = stream_channel_info.optString("updated_at");
                                    String channel_info_store_id = stream_channel_info.optString("store_id");
                                    String channel_info_broadcasts_count = stream_channel_info.optString("broadcasts_count");

                                    streamBeans.setChannelId(channel_info_id);
                                    streamBeans.setStreamType(channel_info_name);
                                    streamBeans.setUpdateAt(channel_info_updated_at);
                                    //streamBeans.setCreateAt(channel_info_created_at);


                                    String product_info_string = stream.optString("product_info");
                                    if (!product_info_string.equals("null")) {
                                        JSONObject stream_product_info = stream.getJSONObject("product_info");

                                        String product_info_id = stream_product_info.optString("id");
                                        String product_info_code = stream_product_info.optString("code");
                                        String product_info_name = stream_product_info.optString("name");
                                        String product_info_description = stream_product_info.optString("description");
                                        String product_info_image_url = stream_product_info.optString("image_url");
                                        String product_info_store_id = stream_product_info.optString("store_id");
                                        String product_info_price = stream_product_info.optString("price");
                                        String product_info_created_at = stream_product_info.optString("created_at");
                                        String product_info_updated_at = stream_product_info.optString("updated_at");
                                        String product_info_currency_type = stream_product_info.optString("currency_type");
                                        String product_info_chat_id = stream_product_info.optString("chat_id");
                                        boolean product_info_in_wish_list = stream_product_info.optBoolean("in_wish_list");

                                        streamBeans.setProductId(product_info_id);
                                        streamBeans.setProductCode(product_info_code);
                                        streamBeans.setProductDesc(product_info_description);
                                        streamBeans.setProducImageUrl(product_info_image_url);
                                        streamBeans.setProductName(product_info_name);
                                        streamBeans.setPrice(product_info_price);
                                        streamBeans.setCurrencytype(product_info_currency_type);
                                        streamBeans.setInWhishlist(product_info_in_wish_list);

                                    }
                                    streamBeans.setCodOption(false);
                                    streamBeans.setCardOption(false);
                                    streamBeans.setWalletOption(false);
                                    if (channel_info_name.equalsIgnoreCase("Products")) {
                                        String payment_option_string = stream.optString("payment_option");
                                        if (!payment_option_string.equals("null")) {
                                            JSONObject payment_option = stream.getJSONObject("payment_option");
                                            String payment_option_id = payment_option.getString("id");
                                            boolean payment_option_cod = payment_option.getBoolean("cod");
                                            boolean payment_option_card = payment_option.getBoolean("card");
                                            boolean payment_option_wallet = payment_option.getBoolean("wallet");
                                            streamBeans.setPaymentOptionId(payment_option_id);
                                            streamBeans.setCodOption(payment_option_cod);
                                            streamBeans.setCardOption(payment_option_card);
                                            streamBeans.setWalletOption(payment_option_wallet);
                                        }
                                    }

                                    JSONObject stream_store_info = jsonResponse1.getJSONObject("store_info");
                                    String store_info_id = stream_store_info.optString("id");
                                    String store_info_title = stream_store_info.optString("title");
                                    String store_info_photo = stream_store_info.optString("photo");
                                    String store_info_email = stream_store_info.optString("email");
                                    String store_info_description = stream_store_info.optString("description");

                                    streamBeans.setStoreId(store_info_id);
                                    streamBeans.setStorePhoto(store_info_photo);
                                    streamBeans.setStoreDesc(store_info_description);
                                    streamBeans.setStoreTitle(store_info_title);
                                    streamBeans.setStoreEmail(store_info_email);

                                    arrayList.add(streamBeans);
                                }
                            }


                            streamList.clear();
                            streamList.addAll(arrayList);
                            if (streamList.size() < 1) {
                                no_item.setVisibility(View.VISIBLE);
                            } else {
                                no_item.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();

                        } else if (status.equals("401")) {
                            startActivity(new Intent(getContext(), MainActivity.class));
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(Constants.CUSTOMER_LOGIN_OR_NOT, false);
                        }
                    } catch (NullPointerException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    } catch (JSONException e) {

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
            refreshLayout.setRefreshing(false);
        }

    };


        /*Calling web service to get Streams with wait*/

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stream_layout, container, false);
        listView = (ListView) view.findViewById(R.id.list_item);
        context = getActivity();
        db = new DatabaseHandler(getContext());
        cd = new ConnectionDetector(getContext());
        tabScreen = new TabScreen();
        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        headerLayout.setVisibility(View.VISIBLE);
        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        no_item = (TextView) view.findViewById(R.id.no_item);
        adapter = new StreamAdapter(streamList, getActivity());
        listView.setAdapter(adapter);
        // listView.setOnItemClickListener(this);

        /**
         *Pull to refresh list implemented
         */

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getAllStream();

            }
        });

        hideKeyboard(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllStream();
     /*   getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                *//*Check, If internet is connected or not!*//*

                getAllStream();
              *//*  Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    if(first_time==true){
                        getAllStream();
                        first_time = false;
                    }


                } else {
                    Toast.makeText(getContext(), "No Network Found!", Toast.LENGTH_LONG).show();

                }*//*
            }
        });*/

    }

    /*Handler to handle JSON response from get Streams API*/

    private void getAllStream() {

        new WebRequestTask(getActivity(), _handler, Constants.GET_METHOD,
                WebServiceDetails.GET_STREAM_PID, false, WebServiceDetails.GET_STREAM).execute();
    }

    @Override
    public void onRefresh() {
        getAllStream();
    }
}
