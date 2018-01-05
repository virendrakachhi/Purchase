package com.hashmybag.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.ChatActivity;
import com.hashmybag.R;
import com.hashmybag.adapters.ChatsAdapter;
import com.hashmybag.beans.InboxBean;
import com.hashmybag.beans.MyFriendsBean;
import com.hashmybag.databasehandle.DatabaseHandler;
import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is used for listing users chat.*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-24
 */

public class ChatsFragment extends Fragment {

    public static ArrayList<InboxBean> inboxList = new ArrayList<>();
    static ChatsAdapter adapter;
    TabScreen tabScreen;
    RelativeLayout headerLayout;
    DatabaseHandler db;
    ConnectionDetector cd;
    Context context;
    TextView textView;
    Set<MyFriendsBean> contactList = new HashSet<>();
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //ArrayList<InboxBean> latestList = db.getInbox();
                // inboxList.clear();
                // inboxList.addAll(latestList);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chats_layout, container, false);
        tabScreen = new TabScreen();
        hideKeyboard(getActivity());
        context = getActivity();
        //db = new DatabaseHandler(getContext());
        //inboxList=db.getInbox();
        ListView listView = (ListView) view.findViewById(R.id.list_item);
        textView = (TextView) view.findViewById(R.id.empltyView);
        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.VISIBLE);
        adapter = new ChatsAdapter(inboxList, getActivity());
        listView.setAdapter(adapter);
        cd = new ConnectionDetector(getContext());

        /*Intiating chat from here, OnClick*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (cd.isConnectingToInternet()) {
                    ArrayList<String> arrayList1 = new ArrayList<String>();
                    InboxBean inboxBean = inboxList.get(position);
                    arrayList1.add(inboxBean.getFriendId());
                    arrayList1.add(inboxBean.getPhoto());
                    arrayList1.add(inboxBean.getFriendName());
                    arrayList1.add(inboxBean.getCommunicationId());
                    arrayList1.add(inboxBean.getLastMessage());
                    arrayList1.add(inboxBean.getIndicater());

                    /*Setting all data into the list and sending intent to ChatActivity for
                    * intialise chating, to the Friend or Merchant*/

                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putStringArrayListExtra("data", arrayList1);
                    intent.putStringArrayListExtra("stream", new ArrayList<String>());
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "No Network Found!", Toast.LENGTH_LONG).show();
                }
            }
        });
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("com.hashmybag.MessageRecieve"));
        listView.setEmptyView(textView);
        return view;
    }

    /*Receiver for incoming message to show into the list of chats*/

    private void getPrevCommunication() {
        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String responseString = null;
                HttpClient httpClient = new DefaultHttpClient();
                try {

                    URI uri = new URI(WebServiceDetails.GET_COMMUNICATION);
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
                        String AUTH_TOKEN = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN);
                        Log.v("", "AUTH-TOKEN: " + AUTH_TOKEN);
                        httpGet.setHeader("AUTH-TOKEN", SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_AUTHENTICATION_TOKEN));
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
                Log.v("response :", "" + responseNotifResponse);
                try {
                    JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                    String status = jsonResponse.optString("status");
                    inboxList.clear();
                    if (status.equalsIgnoreCase("200")) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray communications = data.getJSONArray("communications");
                        for (int i = 0; i < communications.length(); i++) {

                            InboxBean inboxBean = new InboxBean();
                            JSONObject communication = communications.getJSONObject(i);
                            String communication_id = communication.optString("communication_id");

                            String lastchat_string = communication.optString("lastchat");
                            if (!lastchat_string.equalsIgnoreCase("null")) {


                                JSONObject lastchat = communication.getJSONObject("lastchat");
                                String lastchat_id = lastchat.optString("id");
                                String lastchat_user_id = lastchat.optString("user_id");
                                String lastchat_message = lastchat.optString("message");
                                String lastchat_attachment_url = lastchat.optString("attachment_url");
                                String lastchat_communication_id = lastchat.optString("communication_id");
                                String lastchat_created_at = lastchat.optString("created_at");
                                String lastchat_updated_at = lastchat.optString("updated_at");
                                String lastchat_status = lastchat.optString("status");
                                String lastchat_broadcast_id = lastchat.optString("broadcast_id");
                                String lastchat_product_id = lastchat.optString("product_id");
                                String lastchat_from_stream = lastchat.optString("from_stream");
                                String lastchat_title = lastchat.optString("title");

                                int chats_count = communication.optInt("chats_count");

                                JSONObject user = communication.getJSONObject("user");
                                String user_id = user.optString("id");
                                String user_type = user.optString("type");
                                String user_email = user.optString("email");
                                String user_title = user.optString("title");
                                String user_description = user.optString("description");
                                String user_address1 = user.optString("address1");
                                String user_photo = user.optString("photo");
                                String user_mobile_number = user.optString("mobile_number");
                                String user_landline_number = user.optString("landline_number");
                                inboxBean.setFriendId(user_id);
                                inboxBean.setPhoto(user_photo);
                                inboxBean.setFriendName(user_title);
                                inboxBean.setCommunicationId(lastchat_communication_id);
                                inboxBean.setLastMessage(lastchat_message);
                                inboxBean.setSmsCount(chats_count);
                                inboxBean.setLastSeen(lastchat_created_at);
                                inboxBean.setIndicater(user_type);
                                if (user_type.equalsIgnoreCase("Customer")) {
                                    Set<MyFriendsBean> contactList = getAllContact();
                                    for (MyFriendsBean object : contactList) {
                                        if (object.getMobileNumber().equals(user_mobile_number)) {
                                            inboxBean.setFriendName(object.getName());
                                        }
                                    }
                                }

                                inboxList.add(inboxBean);
                            }

                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //Toast.makeText(getActivity(), "No chat available", Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }


            }
        }.execute();


    }

    @Override
    public void onResume() {
        super.onResume();
        // ArrayList<InboxBean> latestList = db.getInbox();
        //inboxList.clear();
        //inboxList.addAll(latestList);
        // adapter.notifyDataSetChanged();

        getPrevCommunication();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
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

}
