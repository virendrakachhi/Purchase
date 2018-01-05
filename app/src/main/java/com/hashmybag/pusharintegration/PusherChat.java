package com.hashmybag.pusharintegration;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.hashmybag.ChatActivity;
import com.hashmybag.R;
import com.hashmybag.beans.ChatingBean;
import com.hashmybag.beans.CommunicationIdbean;
import com.hashmybag.beans.InboxBean;
import com.hashmybag.beans.MyFriendsBean;
import com.hashmybag.beans.OnlineBean;
import com.hashmybag.databasehandle.DatabaseHandler;
import com.hashmybag.fragments.ChatsFragment;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;
import com.hashmybag.util.TimeDifferentiation;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PresenceChannel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.User;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This class is used for implementing pusher in to the app for chat feature to the app.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-21
 */

public class PusherChat {
    public static PusherChat myPusherChat;
    public static ArrayList<OnlineBean> onlineUser = new ArrayList<>();
    Pusher pusher;
    String socketId;
    PresenceChannel presenceChannel;
    Context context;
    String id;
    DatabaseHandler db;
    Set<MyFriendsBean> contactList = new HashSet<>();
    TimeDifferentiation td;
    List<CommunicationIdbean> commIdList = new ArrayList<>();
    private String comm_id;
    private String created_at;
    private String chat_id;
    private String text_message;
    private String communication_id;
    private String auth;
    private String reciever_id;
    private ArrayList<InboxBean> inboxList = new ArrayList<>();
    private String stringDate;

    /**
     * This method includes the initialisation of pusher object and connecting to the pusher server.
     *
     * @param context
     */

    public PusherChat(final Context context) {
        this.context = context;
        db = new DatabaseHandler(context);
        td = new TimeDifferentiation();
        id = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_ID);
        final HttpAuthorizer authorizer = new HttpAuthorizer(WebServiceDetails.PUSHAR_AUTHORIZATION);
        PusherOptions options = new PusherOptions().setEncrypted(true).setWssPort(443).setAuthorizer(authorizer);
        pusher = new Pusher(Constants.PUSHER_KEY, options);

        /**
         * ConnectionListener to listen all the connection regarding activities
         * i.g connection state changed and all
         */

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    socketId = pusher.getConnection().getSocketId();

                    authorizer.setHeaders(getMapAuthorizationHeaders());
                    authorizer.setQueryStringParameters(getMapAuthorizationHeaders1());

                    /**
                     * Initialising channel name which will be subscribed by HMB user
                     * and it will also get listening events when user gets online/offline
                     */

                    presenceChannel = pusher.subscribePresence(Constants.CHANNEL_NAME, new PresenceChannelEventListener() {
                        @Override
                        public void onUsersInformationReceived(String channelName, Set<User> users) {
                            User user;
                            String userId;
                            Iterator<User> userIterator = users.iterator();
                            while (userIterator.hasNext()) {
                                OnlineBean onlineBean = new OnlineBean();
                                user = userIterator.next();
                                userId = user.getId();
                                onlineBean.setOnlineUser(userId);
                                onlineBean.setUserStatus("true");
                                onlineUser.add(onlineBean);
                                Log.d("", "");
                            }
                            Log.d("", "");
                        }

                        @Override
                        public void userSubscribed(String channelName, User user) {
                            Intent intent = new Intent();
                            intent.putExtra("userid", user.getId());
                            intent.putExtra("Status", "true");
                            intent.setAction("com.hashmybag.OnlineStatus");
                            context.sendBroadcast(intent);
                            int ch = 0;
                            for (int i = 0; i < onlineUser.size(); i++) {
                                OnlineBean onlineBean = onlineUser.get(i);
                                if (onlineBean.getOnlineUser().equals(user.getId())) {
                                    onlineBean.setUserStatus("true");
                                    ch = 1;
                                    break;
                                }
                            }


                            if (ch < 1) {
                                OnlineBean onlineBean = new OnlineBean();
                                onlineBean.setUserStatus("true");
                                onlineBean.setOnlineUser(user.getId());
                                onlineUser.add(onlineBean);
                            }

                            Log.d("", "");

                        }

                        @Override
                        public void userUnsubscribed(String channelName, User user) {
                            user.getId();
                            Intent intent = new Intent();
                            intent.putExtra("userid", user.getId());
                            intent.putExtra("Status", "false");
                            intent.setAction("com.hashmybag.OnlineStatus");
                            context.sendBroadcast(intent);

                            for (int i = 0; i < onlineUser.size(); i++) {
                                OnlineBean onlineBean = onlineUser.get(i);
                                if (onlineBean.getOnlineUser().equals(user.getId())) {
                                    onlineBean.setUserStatus("false");
                                    break;
                                }
                            }


                        }

                        @Override
                        public void onAuthenticationFailure(String message, Exception e) {
                            Log.d("", "");
                        }

                        @Override
                        public void onSubscriptionSucceeded(String channelName) {
                            Log.d("", "");
                        }

                        @Override
                        public void onEvent(String channelName, String eventName, String data) {
                            Log.d("", "");
                        }

                    });

                    /**
                     * This method is implemented for listening presence channel events
                     * i.e when message is arrived to a particular id
                     * message receiving is handling over here
                     */

                    presenceChannel.bind(Constants.EVENT_NAME, new PresenceChannelEventListener() {
                        @Override
                        public void onSubscriptionSucceeded(String channelName) {
                            Log.d(" Here ", " When event!! ");
                        }

                        @Override
                        public void onAuthenticationFailure(String message, Exception e) {
                            Log.d(" Here ", " When event!! ");
                        }

                        @Override
                        public void onUsersInformationReceived(String channelName, Set<User> users) {
                            Log.d(" Here ", " When event!! ");
                        }

                        @Override
                        public void userSubscribed(String channelName, User user) {
                            Log.d(" Here ", " When event!! ");
                        }

                        @Override
                        public void userUnsubscribed(String channelName, User user) {
                            Log.d(" Here ", " When event!! ");
                        }

                        @Override
                        public void onEvent(String channelName, String eventName, final String data) {

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String inboxmessage = "textmessage..";
                                        JSONObject json = new JSONObject(data);
                                        List<ChatingBean> PracticeVO = new ArrayList<ChatingBean>();
                                        JSONObject communication = json.getJSONObject("communication");
                                        String communication_id = communication.getString("id");
                                        String sender_id = communication.getString("sender_id");
                                        String receiver_id = communication.getString("receiver_id");
                                        String product_idi = communication.getString("product_id");
                                        if (checkCommid(communication_id, sender_id, receiver_id)) {
                                            ChatingBean chatingBean = new ChatingBean();
                                            JSONObject chat_user = json.getJSONObject("chat_user");
                                            String chat_user_id = chat_user.getString("id");
                                            String chat_user_name = chat_user.getString("first_name");
                                            String chat_user_title = chat_user.getString("title");
                                            String indicater = json.getString("user_type");
                                            String chat_user_mobile_number = chat_user.getString("mobile_number");
                                            String chat_user_photo = chat_user.getString("photo");
                                            String chat_user_username = chat_user.getString("username");

                                            JSONObject chat = json.getJSONObject("chat");
                                            String message = chat.getString("message");
                                            String attachment_url = chat.getString("attachment_url");
                                            String title = chat.getString("title");
                                            boolean from_stream = chat.getBoolean("from_stream");
                                            String created_at = chat.getString("created_at");
                                            String updated_at = chat.getString("updated_at");


                                            chatingBean.setTitle(title);
                                            chatingBean.setMessageText(message);

                                            if (!chat_user_id.equalsIgnoreCase(id)) {

                                                String pi = json.optString("product");
                                                if (!pi.equalsIgnoreCase("null")) {

                                                    JSONObject product = json.getJSONObject("product");
                                                    String product_id = product.getString("id");
                                                    String product_code = product.getString("code");
                                                    String product_name = product.getString("name");
                                                    String product_description = product.getString("description");
                                                    String product_image_url = product.getString("image_url");
                                                    String product_store_id = product.getString("store_id");
                                                    String product_price = product.getString("price");
                                                    String product_created_at = product.getString("created_at");
                                                    String product_updated_at = product.getString("updated_at");
                                                    String product_currency_type = product.getString("currency_type");
                                                    String product_chat_id = product.getString("chat_id");
                                                    chatingBean.setMsgDate(product_created_at);


                                                    chatingBean.setFavorite(false);
                                                    chatingBean.setProductId(product_id);
                                                    chatingBean.setProductCode(product_code);
                                                    chatingBean.setProductName(product_name);
                                                    chatingBean.setProductDescription(product_description);
                                                    chatingBean.setProductImage(product_image_url);
                                                    chatingBean.setProductPrice(product_price);
                                                    chatingBean.setProductUpdated(product_updated_at);
                                                    chatingBean.setProductedCreated(product_created_at);
                                                    chatingBean.setProductDescription(product_description);
                                                    chatingBean.setChatID(product_chat_id);
                                                    chatingBean.setCurrencyType(product_currency_type);
                                                    chatingBean.setBodyType("Products");
                                                    inboxmessage = "Product";
                                                    chatingBean.setTitle(product_name);
                                                    chatingBean.setMessageText(product_description);


                                                    String po = json.optString("payment_option");
                                                    if (!po.equalsIgnoreCase("null")) {
                                                        JSONObject payment_option = json.getJSONObject("payment_option");
                                                        String payment_option_id = payment_option.getString("id");
                                                        boolean payment_option_cod = payment_option.getBoolean("cod");
                                                        boolean payment_option_card = payment_option.getBoolean("card");
                                                        boolean payment_option_wallet = payment_option.getBoolean("wallet");
                                                        String payment_option_chat_id = payment_option.getString("chat_id");
                                                        String payment_option_price = payment_option.getString("price");

                                                        chatingBean.setPaymentId(payment_option_id);
                                                        chatingBean.setCodOption(payment_option_cod);
                                                        chatingBean.setCardOption(payment_option_card);
                                                        chatingBean.setWalletOption(payment_option_wallet);
                                                        chatingBean.setPaymentPrice(payment_option_price);
                                                        chatingBean.setMsgDate(created_at);

                                                    }
                                                } else {
                                                    JSONObject chat1 = json.getJSONObject("chat");
                                                    String message1 = chat1.getString("message");
                                                    String attachment_url1 = chat1.getString("attachment_url");
                                                    if ((message1 != "null") && (attachment_url1 == "null")) {
                                                        inboxmessage = message1;
                                                    } else if ((message1 == "null") && (attachment_url1 != "null")) {
                                                        inboxmessage = "image";
                                                    } else {
                                                        inboxmessage = "receipt";
                                                    }
                                                }

                                                chatingBean.setDate(td.getDateFormat(created_at));
                                                chatingBean.setMsgDate(td.getMsgDate(created_at));
                                                chatingBean.setImageUrl(attachment_url);
                                                chatingBean.setCommunicationID(communication_id);
                                                chatingBean.setFromStream(from_stream);
                                                chatingBean.setFriendId(id);
                                                chatingBean.setSenderId(chat_user_id);

                                                boolean okAdded = addInboxTable(chat_user_id, inboxmessage, chat_user_photo, communication_id, chat_user_title, indicater, chat_user_username);
                                                if (okAdded) {
                                                    Intent intent1 = new Intent();
                                                    intent1.setAction("com.hashmybag.MessageRecieve");
                                                    context.sendBroadcast(intent1);
                                                    Intent intent = new Intent();
                                                    PracticeVO.add(chatingBean);
                                                    Bundle args = new Bundle();
                                                    args.putSerializable("ARRAYLIST", (Serializable) PracticeVO);
                                                    intent.putExtra("BUNDLE", args);
                                                    intent.setAction("com.hashmybag.Message");
                                                    context.sendBroadcast(intent);
                                                    notification(chat_user_title, inboxmessage, chat_user_id, chat_user_photo, chat_user_title,
                                                            communication_id, indicater);

                                                }


                                            }

                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.d("", message);
            }

        });


    }

    public static PusherChat getInstance(Context context) {
        if (myPusherChat == null) {
            myPusherChat = new PusherChat(context);
        }
        return myPusherChat;
    }

    /**
     * Adding incoming messages to InboxBean for reusing it again
     *
     * @param id
     * @param message
     * @param photp_url
     * @param comm_id
     * @param title
     * @param indicater
     * @param mobile
     * @return
     */

    private boolean addInboxTable(String id, String message, String photp_url, String comm_id, String title, String indicater, String mobile) {


   /*     for(int i=0;inboxBeanList.size()>i;i++){
          if(comm_id.equalsIgnoreCase(inboxBeanList.get(i).getCommunicationId())){
              InboxBean   inboxBean=inboxBeanList.get(i);
              int count=inboxBeanList.get(i).getSmsCount();
              inboxBean.setSmsCount(count+1);
              ChatsFragment.inboxList.add(i,inboxBean);

          }else{
              InboxBean  inboxBean=new InboxBean();
              inboxBean.setFriendId(id);
              inboxBean.setPhoto(photp_url);
              inboxBean.setFriendName(title);
              inboxBean.setCommunicationId(comm_id);
              inboxBean.setLastMessage(message);
              inboxBean.setSmsCount(1);
              inboxBean.setLastSeen(td.getCurrentDateForInbox());
              inboxBean.setIndicater(indicater);
          }

        }

        inboxList.clear();
        inboxList = db.getInbox();*/

        List<InboxBean> inboxBeanList = ChatsFragment.inboxList;

        boolean ok = false;
        InboxBean inboxBean = new InboxBean();
        inboxBean.setFriendId(id);
        inboxBean.setPhoto(photp_url);
        inboxBean.setFriendName(title);
        inboxBean.setCommunicationId(comm_id);
        inboxBean.setLastMessage(message);
        int chk = 0;
        if (indicater.equals("Store")) {
            inboxBean.setIndicater("Store");
            chk = 1;
        } else {
            Set<MyFriendsBean> contactList = getAllContact();
            for (MyFriendsBean object : contactList) {

                if (object.getMobileNumber().equals(mobile)) {
                    inboxBean.setIndicater("Customer");
                    inboxBean.setFriendName(object.getName());
                    chk = 1;
                }

            }
        }

        if (chk > 0) {

            int ch = 0;
            for (int i = 0; i < inboxBeanList.size(); i++) {
                String friendId = inboxBeanList.get(i).getFriendId();
                if (friendId.equals(id)) {
                    inboxBean.setLastSeen(inboxBeanList.get(i).getLastSeen());
                    inboxBean.setSmsCount(inboxBeanList.get(i).getSmsCount() + 1);
                    //db.updateInbox(inboxBean);
                    ChatsFragment.inboxList.set(i, inboxBean);
                    ch = 1;
                    break;
                }
            }
            if (ch < 1) {
                String ls = td.getCurrentDateForInbox();
                inboxBean.setLastSeen(ls);
                inboxBean.setSmsCount(1);
                ChatsFragment.inboxList.add(inboxBean);

                //  db.setInbox(inboxBean);
            }
            if (inboxBeanList.size() <= 0) {
                String ls = td.getCurrentDateForInbox();
                inboxBean.setLastSeen(ls);
                inboxBean.setSmsCount(1);
                ChatsFragment.inboxList.add(inboxBean);
                //db.setInbox(inboxBean);
            }

            ok = true;
        }
        Log.d("Does Not find number", "");
        return ok;
    }

    /**
     * Retrieving all the contacts for set chat
     *
     * @return
     */


    public Set<MyFriendsBean> getAllContact() {

        List<MyFriendsBean> list = new ArrayList<>();
        contactList.clear();
        list.clear();
        try {


            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);
            String phone = null;
            String name = null;
            String emailContact = null;
            String emailType = null;
            String image_uri = "";
            Bitmap bitmap = null;
            String temp = "s";
            if (cur.getCount() > 0) {

                while (cur.moveToNext()) {
                    String id = cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts._ID));

                    image_uri = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer
                            .parseInt(cur.getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                        + " = ?", new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            emailContact = emailCur
                                    .getString(emailCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            emailType = emailCur
                                    .getString(emailCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        }
                        emailCur.close();

                        if (image_uri != null) {
                            System.out.println(Uri.parse(image_uri));
                            try {
                                bitmap = MediaStore.Images.Media
                                        .getBitmap(context.getContentResolver(),
                                                Uri.parse(image_uri));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                            MyFriendsBean contactfriend = new MyFriendsBean();
                            contactfriend.setPhoto(image_uri);
                            contactfriend.setName(name);
                            contactfriend.setMobileNumber(phone.replaceAll("[\\D]", ""));
                            contactfriend.setId(id);
                            list.add(contactfriend);


                        }
                        pCur.close();
                    }
                }
            }
            contactList.addAll(clearListFromDuplicateFirstName(list));
        } catch (Exception e) {

        }
        return contactList;
    }

    /**
     * Achieving delicacy error arriving in Name
     *
     * @param list1
     * @return
     */

    private List<MyFriendsBean> clearListFromDuplicateFirstName(List<MyFriendsBean> list1) {

        Map<String, MyFriendsBean> cleanMap = new LinkedHashMap<String, MyFriendsBean>();
        for (int i = 0; i < list1.size(); i++) {
            cleanMap.put(list1.get(i).getMobileNumber(), list1.get(i));
        }
        List<MyFriendsBean> list = new ArrayList<MyFriendsBean>(cleanMap.values());
        return list;
    }

    /**
     * Retrieving single contact from database
     *
     * @return
     */

    public ArrayList<String> getContact() {

        ArrayList<String> alContacts = new ArrayList<String>();
        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        alContacts.add(contactNumber.replaceAll("[\\D]", ""));
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());
        }


        return alContacts;
    }

    /**
     * In the following method check is applied for Communication id.
     *
     * @param commId
     * @param senderId
     * @param recieverId
     * @return
     */

    public boolean checkCommid(String commId, String senderId, String recieverId) {
        boolean l1 = false;
        commIdList = db.getAllCommId();
        for (int i = 0; i < commIdList.size(); i++) {
            if (commId.equalsIgnoreCase(commIdList.get(i).getCommID())) {
                l1 = true;
                break;
            }
        }
        if (!l1) {
            if (recieverId.equalsIgnoreCase(id)) {
                CommunicationIdbean communicationIdbean = new CommunicationIdbean();
                communicationIdbean.setCommID(commId);
                communicationIdbean.setSenderId(senderId);
                communicationIdbean.setRecieverId(recieverId);
                db.setCommunicationId(communicationIdbean);
                l1 = true;
            } else if (recieverId.equalsIgnoreCase(id) || senderId.equalsIgnoreCase(id)) {
                CommunicationIdbean communicationIdbean = new CommunicationIdbean();
                communicationIdbean.setCommID(commId);
                communicationIdbean.setSenderId(senderId);
                communicationIdbean.setRecieverId(recieverId);
                db.setCommunicationId(communicationIdbean);
                l1 = true;
            }
        }


        if (commIdList.size() <= 0) {
            if (recieverId.equalsIgnoreCase(id) || senderId.equalsIgnoreCase(id)) {

                CommunicationIdbean communicationIdbean = new CommunicationIdbean();
                communicationIdbean.setCommID(commId);
                communicationIdbean.setSenderId(senderId);
                communicationIdbean.setRecieverId(recieverId);
                db.setCommunicationId(communicationIdbean);
                l1 = true;
            }
        }
        return l1;
    }

    /**
     * Authorization is needed for the presence channel
     * Which is achieving over here
     * Headers are to be settle for the Authorization
     *
     * @return
     */

    public HashMap<String, String> getMapAuthorizationHeaders() {
        try {

            HashMap<String, String> map = new HashMap<String, String>();
            String authString = "Test:Test";
            String base64 = "";
            try {
                byte[] data = authString.getBytes("UTF-8");
                base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                base64 = "Basic " + base64;
                auth = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN);

            } catch (Exception e) {
                e.printStackTrace();
            }

            map.put("Authorization", base64);
            map.put("Content-type", "application/x-www-form-urlencoded");
            map.put("Accept", "*/*");
            map.put("AUTH-TOKEN", auth);

            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Headers are attached for the authorization URL
     *
     * @return
     */

    public HashMap<String, String> getMapAuthorizationHeaders1() {
        HashMap<String, String> map1 = new HashMap<String, String>();

        try {

            socketId = pusher.getConnection().getSocketId();
            String j = socketId;
            map1.put("channel_name", Constants.CHANNEL_NAME);
            map1.put("socket_id", socketId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map1;

    }

    /**
     * Hanlding notifications which are triggered when messages got arrived
     *
     * @param chat_user_name
     * @param message
     */

    private void notification(String chat_user_name, String message, String id, String photo,
                              String name, String com_id, String indi) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        String topActivityName = tasks.get(0).topActivity.getClassName();
        if (!(topActivityName.equalsIgnoreCase("com.hashmybag.ChatActivity"))) {

            ArrayList<String> arrayList1 = new ArrayList<String>();
            arrayList1.add(id);
            arrayList1.add(photo);
            arrayList1.add(name);
            arrayList1.add(com_id);
            arrayList1.add(message);
            arrayList1.add(indi);

                    /*Setting all data into the list and sending intent to ChatActivity for
                    * intialise chating, to the Friend or Merchant*/

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putStringArrayListExtra("data", arrayList1);
            intent.putStringArrayListExtra("stream", new ArrayList<String>());


            //Intent intent = new Intent(context, AllFragmentsActivity.class);
            // intent.putExtra("from", "notification");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            int dummyuniqueInt = new Random().nextInt(543254);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, dummyuniqueInt, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
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
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, mBuilder.build());

        }
    }

}
