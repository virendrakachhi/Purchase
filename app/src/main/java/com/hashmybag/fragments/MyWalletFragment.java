package com.hashmybag.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hashmybag.R;
import com.hashmybag.WebActivity;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * This class is used for implimenting wallet*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-22
 */
public class MyWalletFragment extends Fragment {

    ImageView back_arrow;
    FragmentTransaction fragmentTransaction;
    TabScreen tabScreen;
    FragmentManager fragmentManager;
    RelativeLayout headerLayout;
    TextView funds, money;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.GET_PAYTM_BALANCE_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("RESPONSECODE");
                        if (status.equalsIgnoreCase("200")) {

                            money.setText(jsonResponse.optString("WALLETBALANCE"));

                        }
                    } catch (Exception e) {
                        //  Toast.makeText(getActivity(),"Mobile was Wrong!",Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private String cust_id, email_id, mobile;

    /**
     * Function to hide keyboard on click outside the board
     *
     * @param ctx
     */

    public static void hideKeyboard(Context ctx) {


        try {
            InputMethodManager inputManager = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            /*check if no view has focus*/
            View v = ((Activity) ctx).getCurrentFocus();
            if (v == null)
                return;

            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_wallet_layout, container, false);
        hideKeyboard(getActivity());

        fragmentManager = getActivity().getSupportFragmentManager();
        tabScreen = new TabScreen();
        cust_id = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_ID);
        email_id = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_EMAIL);
        mobile = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_MOBILE_NUMBER);

        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        funds = (TextView) view.findViewById(R.id.funds);
        money = (TextView) view.findViewById(R.id.money);

        headerLayout.setVisibility(View.GONE);

        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, tabScreen);
                fragmentTransaction.commit();
            }
        });
        funds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PayTM payTM=new  PayTM(getActivity(),"1",cust_id,email_id,mobile,"wallet");
                //payTM.onStartTransaction();
                //payTM.AddFundinWallet();
                // Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paytm.com/paytmwallet"));
                // startActivity(i);
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", "https://paytm.com/paytmwallet");
                startActivity(intent);
            }
        });
        //getWalletbalance();
        return view;
    }

    /*Method used for showing wallet,current balance*/

    private String initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
        return orderId;
    }

    /*Handler for handling response to get Balance from wallet*/

    public void getWalletbalance() {
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("TOKEN", "");
            jsonObject.put("MID", "7NODES30734077921995");
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(getActivity(), _handler, Constants.GET_METHOD,
                WebServiceDetails.GET_PAYTM_BALANCE_PID, false, (WebServiceDetails.GET_PAYTM_BALANCE + "?jsonData=") + json).execute();
    }

}
