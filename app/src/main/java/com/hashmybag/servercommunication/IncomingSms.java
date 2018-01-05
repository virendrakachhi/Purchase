package com.hashmybag.servercommunication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;

import com.hashmybag.OtpActivity;
import com.hashmybag.R;
import com.hashmybag.util.NotificationView;

/**
 * This class is used for handling incoming messages.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-05-10
 */
public class IncomingSms extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
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
                        ;

                        OtpActivity Sms = new OtpActivity();


                        //Sms.recivedSms(otp);
                        Notification();

                    }

                }
            }

        } catch (NullPointerException e)

        {
            e.printStackTrace();
        }
    }

    /**
     * Handling notifications to the messages.
     */

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


}
