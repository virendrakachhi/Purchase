package com.hashmybag.paytmintegration;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.paytm.pgsdk.Log;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class is used for handling all the payments regarding paytm.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-16
 */

public class PayTM {
    Context context;
    String MID, CUST_ID, CHANNEL_ID, INDUSTRY_TYPE_ID, WEBSITE, TXN_AMOUNT, THEME, EMAIL, MOBILE_NO, PAYMENT_TYPE;
    String product_id, store_id;
    /**
     * Handling the JSON response coming from the chat history API
     */

    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.CREATE_SALES_HISTORY_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    android.util.Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        String message = jsonResponse.optString("status");
                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {
                            Toast.makeText(context, "Payment received successfully", Toast.LENGTH_LONG).show();
                        } else if (status.equalsIgnoreCase("404")) {
                            Toast.makeText(context, "You need to follow this store", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Payment does not set in server", Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private String auth_token, basic_auth;

    //context,chatingBean.getProductPrice(),cust_id,email_id,mobile,chatingBean.getProductId()
    public PayTM(Activity context, String TXN_AMOUNT, String CUST_ID, String EMAIL, String MOBILE_NO, String product_id, String store_id, String PAYMENT_TYPE) {
        this.context = context;
        this.TXN_AMOUNT = TXN_AMOUNT;
        this.EMAIL = EMAIL;
        this.MOBILE_NO = MOBILE_NO;
        this.CUST_ID = CUST_ID;
        this.product_id = product_id;
        this.store_id = store_id;
        this.PAYMENT_TYPE = PAYMENT_TYPE;
    }

    /**
     * Initialising order Id for initialing PayTm
     *
     * @return
     */

    private String initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        String orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
        return orderId;
    }

    /**
     * Here transaction start to PayTm for any product which will carry TXN AMOUNT and all
     */

    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("ORDER_ID", initOrderId());
        paramMap.put("MID", "7NODES30734077921995");
        paramMap.put("CUST_ID", CUST_ID);
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("WEBSITE", "7NODESWAP");
        paramMap.put("TXN_AMOUNT", TXN_AMOUNT);
        paramMap.put("THEME", "merchant");
        paramMap.put("EMAIL", EMAIL);
        paramMap.put("MOBILE_NO", MOBILE_NO);

        PaytmOrder Order = new PaytmOrder(paramMap);

        Log.d("LOG", "Request Parameter " + paramMap);
        PaytmMerchant Merchant = new PaytmMerchant(
                "http://pay.hashmybag.com/generateChecksum.php",
                "http://pay.hashmybag.com/verifyChecksum.php");

        Service.initialize(Order, Merchant, null);

        Service.startPaymentTransaction(context, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {

                        Log.d("LOG", "Error Message from paytm " + inErrorMessage);

                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        //  Toast.makeText(context, "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
                        setPaymentHistory(TXN_AMOUNT, product_id, store_id, CUST_ID, PAYMENT_TYPE);
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        Log.d("LOG", "Payment Limit Exceeded " + inErrorMessage);
                        Toast.makeText(context, "Payment Limit Exceeded  ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        Log.d("LOG", "Network not Available " + "");

                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {


                        Log.d("LOG", "Client Auth fail " + inErrorMessage);
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                        Log.d("LOG", "Payment Error Loading WebPage " + "");

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Log.d("LOG", "Pressed trans Cancel" + "");


                        // TODO Auto-generated method stub
                    }

                });
    }

    /**
     * Adding funds to the wallet
     */

    public void AddFundinWallet() {
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("ORDER_ID", initOrderId());
        paramMap.put("MID", "DIY12386817555501617");
        paramMap.put("CUST_ID", CUST_ID);
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("WEBSITE", "DIYtestingweb");
        //  paramMap.put("TXN_AMOUNT","200");
        paramMap.put("THEME", "marchent");
        paramMap.put("EMAIL", EMAIL);
        paramMap.put("MOBILE_NO", MOBILE_NO);

       /* paramMap.put("ORDER_ID",initOrderId());
        paramMap.put("MID","7NODES30734077921995");
        paramMap.put("CUST_ID",CUST_ID);
        paramMap.put("CHANNEL_ID","WAP");
        paramMap.put("INDUSTRY_TYPE_ID","Retail");
        paramMap.put("WEBSITE","7NODESWAP");
       // paramMap.put("TXN_AMOUNT",TXN_AMOUNT);
        paramMap.put("THEME","merchant");
        paramMap.put("EMAIL",EMAIL);
        paramMap.put("MOBILE_NO",MOBILE_NO);*/

     /*   https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp
        https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp*/

        PaytmOrder Order = new PaytmOrder(paramMap);

        Log.d("LOG", "Request Parameter " + paramMap);
        PaytmMerchant Merchant = new PaytmMerchant(
                "http://pay.hashmybag.com/generateChecksum.php",
                "http://pay.hashmybag.com/verifyChecksum.php");

        Service.initialize(Order, Merchant, null);

        Service.startPaymentTransaction(context, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void onTransactionSuccess(Bundle bundle) {
                        Log.d("", "");
                    }

                    @Override
                    public void onTransactionFailure(String s, Bundle bundle) {
                        Log.d("", "");
                    }

                    @Override
                    public void networkNotAvailable() {
                        Log.d("", "");
                    }

                    @Override
                    public void clientAuthenticationFailed(String s) {
                        Log.d("", "");
                    }

                    @Override
                    public void someUIErrorOccurred(String s) {
                        Log.d("", "");
                    }

                    @Override
                    public void onErrorLoadingWebPage(int i, String s, String s1) {
                        Log.d("", "");
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Log.d("", "");
                    }


                });
    }

    /**
     * Web service call to enter the payment history
     *
     * @param price
     * @param product_id
     * @param store_id
     * @param customer_id
     */

    private void setPaymentHistory(String price, String product_id, String store_id, String customer_id, String payment_type) {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            JSONObject sales_history = new JSONObject();
            sales_history.put("price", price);
            sales_history.put("product_id", product_id);
            sales_history.put("store_id", store_id);
            sales_history.put("customer_id", customer_id);
            sales_history.put("payment_type", payment_type);


            jsonObject.put("sales_history", sales_history);

            json = jsonObject.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(context, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.CREATE_SALES_HISTORY_PID, true, WebServiceDetails.CREATE_SALES_HISTORY).execute();
    }


}
