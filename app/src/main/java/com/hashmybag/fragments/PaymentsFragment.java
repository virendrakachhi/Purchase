package com.hashmybag.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.R;
import com.hashmybag.adapters.PaymentAdapter;
import com.hashmybag.beans.PaymentBean;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is used for handling Payments History *
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-22
 */

public class PaymentsFragment extends Fragment {

    FragmentManager fragmentManager;
    ArrayList<PaymentBean> arrayList = new ArrayList<>();
    ImageView back_arrow;
    FragmentTransaction fragmentTransaction;
    TabScreen tabScreen;
    RelativeLayout headerLayout;
    ListView listView;
    PaymentAdapter adapter;
    TextView empltyView;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.GET_SALES_HISTORY_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    android.util.Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        arrayList.clear();
                        String status = jsonResponse.optString("status");
                        String message = jsonResponse.optString("message");

                        if (status.equalsIgnoreCase("200")) {
                            JSONObject user = jsonResponse.getJSONObject("data");
                            JSONArray sales_histories = user.getJSONArray("sales_histories");
                            for (int i = 0; i < sales_histories.length(); i++) {
                                JSONObject jsonResponse1 = sales_histories.getJSONObject(i);
                                String sales_id = jsonResponse1.optString("id");
                                String pay_price = jsonResponse1.optString("price");
                                String store_id = jsonResponse1.optString("store_id");
                                String store_name = jsonResponse1.optString("store_name");

                                String customer_id = jsonResponse1.optString("customer_id");
                                String created_at = jsonResponse1.optString("created_at");

                                JSONObject product = jsonResponse1.getJSONObject("product");
                                String product_id = product.optString("id");
                                String product_code = product.optString("code");
                                String product_name = product.optString("name");
                                String product_description = product.optString("description");
                                String image_url = product.optString("image_url");
                                String price_product = product.optString("price");
                                String currency_type = product.optString("currency_type");

                                PaymentBean myObj = new PaymentBean();
                                myObj.setObj_image(image_url);
                                myObj.setObj_name(product_name);
                                myObj.setObj_from(store_name);
                                myObj.setObj_date(created_at);

                                if (currency_type.equalsIgnoreCase("INR"))
                                    myObj.setObj_price("INR " + pay_price);
                                else
                                    myObj.setObj_price("$" + pay_price);


                                arrayList.add(myObj);


                            }

                            adapter = new PaymentAdapter(arrayList, getActivity());
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "User does not have Payment!", Toast.LENGTH_LONG).show();
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

            /* check if no view has focus */
            View v = ((Activity) ctx).getCurrentFocus();
            if (v == null)
                return;

            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Web service call for getting sales/payment history */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_layout, container, false);

        listView = (ListView) view.findViewById(R.id.list_item);
        hideKeyboard(getActivity());

        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.GONE);
        empltyView = (TextView) view.findViewById(R.id.empltyView);

        fragmentManager = getActivity().getSupportFragmentManager();
        tabScreen = new TabScreen();

        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, tabScreen);
                fragmentTransaction.commit();
            }
        });

        getPaymentHistory();
        listView.setEmptyView(empltyView);

        return view;
    }

    /*Handler to handle sales/payment histoy*/

    private void getPaymentHistory() {

        new WebRequestTask(getActivity(), _handler, Constants.GET_METHOD,
                WebServiceDetails.GET_SALES_HISTORY_PID, true, WebServiceDetails.GET_SALES_HISTORY).execute();

    }

}