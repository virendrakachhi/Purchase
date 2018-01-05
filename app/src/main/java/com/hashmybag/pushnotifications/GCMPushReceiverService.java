package com.hashmybag.pushnotifications;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.hashmybag.ChatActivity;
import com.hashmybag.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is used for implementing GCM for generating unique Id for the device.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-25
 */

public class GCMPushReceiverService extends GcmListenerService {

    String product_id, product_code, product_price, product_description, product_title, product_image, currency_type, created_at, store_id, updated_at;

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        //Displaying a notiffication with the message id

        try {
            JSONObject bundle = new JSONObject(data.getString("product"));
            product_id = bundle.getString("id");
            product_code = bundle.getString("code");
            product_price = bundle.getString("price");
            product_description = bundle.getString("description");
            product_title = bundle.getString("name");
            currency_type = bundle.getString("currency_type");
            created_at = bundle.getString("created_at");
            product_image = bundle.getString("image_url");
            updated_at = bundle.getString("updated_at");
            store_id = bundle.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String chat_user_id = data.getString("chat_user_id");
        String chat_user_photo = data.getString("chat_user_photo");
        String chat_user_title = data.getString("chat_user_title");
        String google_msg_id = data.getString("google.message_id");
        String communication_id = data.getString("communications_id");


        sendNotification(product_description, chat_user_title, chat_user_id, chat_user_photo, communication_id, product_id, product_code, product_price, currency_type, created_at, product_title, product_image, updated_at, store_id);
    }

    /**
     * Push notifications handled for GCM
     *
     * @param message
     */

    private void sendNotification(String message, String chat_user_title, String chat_user_id, String chat_user_photo, String communication_id, String product_id, String product_code, String product_price, String currency_type, String created_at, String product_title, String product_image, String updated_at, String store_id) {

        int requestCode = 0;

        ArrayList<String> arrayList1 = new ArrayList<String>();
        arrayList1.add(chat_user_id);
        arrayList1.add(chat_user_photo);
        arrayList1.add(chat_user_title);
        arrayList1.add(communication_id);
        arrayList1.add(created_at);
        arrayList1.add("Store");

        ArrayList<String> arrayList2 = new ArrayList<String>();
        arrayList2.add(store_id);
        arrayList2.add(product_title);
        arrayList2.add(message);
        arrayList2.add(product_image);
        arrayList2.add(created_at);
        arrayList2.add(product_id);
        arrayList2.add(product_code);
        arrayList2.add(product_title);
        arrayList2.add(message);
        arrayList2.add(product_image);
        arrayList2.add(product_price);
        arrayList2.add(updated_at);
        arrayList2.add(currency_type);
        arrayList2.add("false");
        arrayList2.add(store_id);


        arrayList2.add("false");
        arrayList2.add("false");
        arrayList2.add("false");



                    /*Setting all data into the list and sending intent to ChatActivity for
                    * intialise chating, to the Friend or Merchant*/

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putStringArrayListExtra("data", arrayList1);
        intent.putStringArrayListExtra("stream", arrayList2);
        intent.putExtra("from_stream", true);

        //Intent intent = new Intent(context, AllFragmentsActivity.class);
        // intent.putExtra("from", "notification");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.notification_bag)
                .setLargeIcon(getBitmapFromURL(chat_user_photo))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentTitle("HashMyBag")
                .setContentIntent(pendingIntent);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        noBuilder.setSound(sound);
        noBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

    private void setPushNotification(String chat_user_name, String message) {

        ArrayList<String> arrayList1 = new ArrayList<String>();
       /* arrayList1.add(wishlistBean.getStoreId());
        arrayList1.add(wishlistBean.getStorePhoto());
        arrayList1.add(wishlistBean.getStoreTitle());
        arrayList1.add(wishlistBean.getCommunicationId());
        arrayList1.add(wishlistBean.getStoreTitle());
        arrayList1.add(wishlistBean.getStreamTime());
        arrayList1.add("merchant");*/

        ArrayList<String> arrayList2 = new ArrayList<String>();
        /*arrayList2.add(streamBean.getStreamType());
        arrayList2.add(wishlistBean.getProductId());
        arrayList2.add(wishlistBean.getProductCode());
        arrayList2.add(wishlistBean.getProductName());
        arrayList2.add(wishlistBean.getProductDescription());
        arrayList2.add(wishlistBean.getProductImage());
        arrayList2.add(wishlistBean.getProductPrice());*
        arrayList2.add(wishlistBean.getProductCreateAt());*
        arrayList2.add(wishlistBean.getProductUpdate());*
        arrayList2.add(wishlistBean.getCurrencyType());*
        arrayList2.add("true");
        arrayList2.add(wishlistBean.getProductStoreId());*/

        Bitmap bitmap = getBitmapFromURL("https://graph.facebook.com/YOUR_USER_ID/picture?type=large");
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        String topActivityName = tasks.get(0).topActivity.getClassName();
        if (!(topActivityName.equalsIgnoreCase("com.hashmybag.ChatActivity"))) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putStringArrayListExtra("data", arrayList1);
            intent.putStringArrayListExtra("stream", arrayList2);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            int dummyuniqueInt = new Random().nextInt(543254);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, dummyuniqueInt, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setLargeIcon(bitmap)
                            .setSmallIcon(R.mipmap.notification_bag)
                            .setContentTitle(chat_user_name)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

            NotificationManager mNotificationManager;

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            mNotificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, mBuilder.build());

        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
