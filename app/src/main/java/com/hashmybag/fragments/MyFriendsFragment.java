package com.hashmybag.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.ChatActivity;
import com.hashmybag.R;
import com.hashmybag.adapters.FriendsAdapter;
import com.hashmybag.beans.CommunicationIdbean;
import com.hashmybag.beans.MyFriendsBean;
import com.hashmybag.databasehandle.DatabaseHandler;
import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is used for listing friends*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-22
 */

public class MyFriendsFragment extends Fragment implements AdapterView.OnItemClickListener {
    ConnectionDetector cd;
    ImageView share_button;
    Set<MyFriendsBean> contactList = new HashSet<>();
    List<MyFriendsBean> installedListN = new ArrayList<>();
    List<MyFriendsBean> installedList = new ArrayList<>();

    List<MyFriendsBean> friendsList = new ArrayList<>();
    FragmentManager fragmentManager;
    ImageView back_arrow;
    TextView header_payment, no_item;
    FragmentTransaction fragmentTransaction;
    TabScreen tabScreen;
    RelativeLayout headerLayout, actionbar_layout;
    Context context;
    ListView listView;
    DatabaseHandler db;

    FriendsAdapter adapter;
    ProgressDialog progressDialog;
    private String mobile;
    private String message, responseNotifResponse;

   /*Getting JSON Listing Friends which have already downloaded HMB app, for chat with them*/
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.CHECKING_MYBAG_FRIEND_PID:
                    responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        installedList.clear();
                        String status = jsonResponse.optString("status");
                        message = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {


                            JSONObject data = jsonResponse.getJSONObject("data");
                            JSONArray present_users = data.getJSONArray("present_users");

                            JSONArray users_info = data.getJSONArray("users_info");
                            JSONArray my_friends_lists = data.getJSONArray("my_friends_lists");

                            for (int i = 0; i < my_friends_lists.length(); i++) {


                                JSONObject jsonObject = my_friends_lists.getJSONObject(i);
                                String id = jsonObject.optString("id");
                                String mobile_number = jsonObject.optString("mobile_number");
                                String title = jsonObject.optString("title");
                                String email = jsonObject.optString("email");
                                String latitude = jsonObject.optString("latitude");
                                String longitude = jsonObject.optString("longitude");
                                String photo = jsonObject.optString("photo");
                                MyFriendsBean myFriendsBean = new MyFriendsBean();
                                myFriendsBean.setId(id);
                                myFriendsBean.setMobileNumber(mobile_number);
                                myFriendsBean.setTitle(title);
                                myFriendsBean.setEmail(email);
                                myFriendsBean.setLatitude(latitude);
                                myFriendsBean.setLongitude(longitude);
                                myFriendsBean.setPhoto(photo);
                                List<CommunicationIdbean> commIdList = db.getAllCommId();
                                int ch = 0;
                                for (int j = 0; j < commIdList.size(); j++) {
                                    CommunicationIdbean communicationIdbean = commIdList.get(j);
                                    String commId = commIdList.get(j).getCommID();
                                    String recieverId = commIdList.get(j).getRecieverId();
                                    String senderId = commIdList.get(j).getSenderId();
                                    if (communicationIdbean.getRecieverId().equals(id) || communicationIdbean.getSenderId().equals(id)) {
                                        myFriendsBean.setCommunicationId(communicationIdbean.getCommID());
                                        ch = 1;
                                        break;
                                    }
                                }
                                if (ch < 1) {
                                    myFriendsBean.setCommunicationId("null");
                                }

                                for (MyFriendsBean object : contactList) {
                                    if (object.getMobileNumber().equals(mobile_number)) {
                                        myFriendsBean.setTitle(object.getName());
                                        break;
                                    }
                                }
                                if (!title.equalsIgnoreCase("null") && (title != null)) {
                                    installedList.add(myFriendsBean);
                                }
                            }
                            //JSONArray non_existing_users=data.getJSONArray("non_existing_users");

                            Collections.sort(installedList, new Comparator<MyFriendsBean>() {
                                @Override
                                public int compare(MyFriendsBean lhs, MyFriendsBean rhs) {


                                    return lhs.getTitle().compareTo(rhs.getTitle());
                                }
                            });

                            installedListN.clear();
                            installedListN.addAll(clearListFromDuplicateFirstName(installedList));

                            List<MyFriendsBean> updateListFD = new ArrayList<>();
                            List<MyFriendsBean> saveListFD = new ArrayList<>();
                            updateListFD.clear();
                            saveListFD.clear();
                            saveListFD.addAll(installedList);

                            for (int i = 0; i < installedList.size(); i++) {
                                for (int j = 0; j < friendsList.size(); j++) {
                                    MyFriendsBean myFriendsBeam = installedList.get(i);
                                    String mobile = installedList.get(i).getMobileNumber();
                                    String newmobile = friendsList.get(j).getMobileNumber();
                                    if (mobile.equalsIgnoreCase(newmobile)) {
                                        updateListFD.add(myFriendsBeam);
                                        saveListFD.remove(myFriendsBeam);
                                        break;
                                    }
                                }
                            }

                            if (saveListFD.size() != 0) {
                                db.setAllContacts(saveListFD);
                            }
                            if (updateListFD.size() != 0) {
                                db.updateAllContact(updateListFD);
                            }
                            friendsList.clear();
                            friendsList.addAll(installedListN);

                            adapter.notifyDataSetChanged();
                        } else if (status.equalsIgnoreCase("404")) {
                            Toast.makeText(getActivity(), "No friends Available", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        //   Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    /*This method used for fetching Contacts*/

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

    /*Providing Crrent date and time*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_friends_layout, container, false);
        listView = (ListView) view.findViewById(R.id.list_item);
        db = new DatabaseHandler(getContext());
        friendsList = db.getAllContacts();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching Contacts..");
        progressDialog.setCancelable(true);
        contactList.clear();
        cd = new ConnectionDetector(getContext());
        mobile = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_MOBILE_NUMBER);
        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.GONE);
        context = getContext();
        header_payment = (TextView) view.findViewById(R.id.header_payment);
        actionbar_layout = (RelativeLayout) view.findViewById(R.id.actionbar_layout);
        hideKeyboard(getActivity());

        share_button = (ImageView) view.findViewById(R.id.share_button);
        no_item = (TextView) view.findViewById(R.id.no_item);


        /*SharingIntent used for sharing with friends */

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });


        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getActivity().getSupportFragmentManager();
                tabScreen = new TabScreen();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, tabScreen);
                fragmentTransaction.commit();
            }
        });
        adapter = new FriendsAdapter(getActivity(), friendsList, no_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    /*Sending Intent to the ChatActivity for intialising chat with the friend selected*/

    private void checkFriendForMyBag(Set<MyFriendsBean> contactList) {
        boolean showProgress = false;
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (MyFriendsBean object : contactList) {
                jsonArray.put(object.getMobileNumber());
            }
            jsonObject.put("users", jsonArray);

            /*convert JSONObject to JSON to String*/

            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (friendsList.size() > 0) {
            showProgress = true;
        } else {
            showProgress = false;
        }
        new WebRequestTask(getActivity(), json, _handler, Constants.POST_METHOD,
                WebServiceDetails.CHECKING_MYBAG_FRIEND_PID, showProgress, WebServiceDetails.CHECKING_MYBAG_FRIEND).execute();


    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                /*Check, If internet is connected or not!*/

                    Boolean isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        checkFriendForMyBag(getAllContact());
                    } else {
                        Toast.makeText(getContext(), "You don't have internet connection!", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "You have denied permission for Reading Contact", Toast.LENGTH_LONG).show();

        }


    }

    /*Handler for handling response to get friends already downloaded HMB*/

    public Set<MyFriendsBean> getAllContact() {

        List<MyFriendsBean> list = new ArrayList<>();
        contactList.clear();
        list.clear();
        try {

            ContentResolver cr = getActivity().getContentResolver();

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

                   /* if (image_uri != null) {
                        System.out.println(Uri.parse(image_uri));
                        try {
                            bitmap = MediaStore.Images.Media
                                    .getBitmap(getActivity().getContentResolver(),
                                            Uri.parse(image_uri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            if (mobile.equalsIgnoreCase(phone.replaceAll("[\\D]", ""))) {

                            } else {
                                MyFriendsBean contactfriend = new MyFriendsBean();
                                contactfriend.setPhoto(image_uri);
                                contactfriend.setName(name);
                                contactfriend.setMobileNumber(phone.replaceAll("[\\D]", ""));
                                contactfriend.setId(id);
                                list.add(contactfriend);
                            }


                        }
                        pCur.close();
                    }
                }
            }
            contactList.addAll(clearListFromDuplicateFirstName(list));
        } catch (Exception e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "You have denied permission for Reading Contact", Toast.LENGTH_LONG).show();
                }
            });
        }
        return contactList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        MyFriendsBean myFriendsBean = friendsList.get(position);
        ArrayList<String> arrayList1 = new ArrayList<String>();
        arrayList1.add(myFriendsBean.getId());
        arrayList1.add(myFriendsBean.getPhoto());
        arrayList1.add(myFriendsBean.getTitle());
        arrayList1.add(myFriendsBean.getCommunicationId());
        arrayList1.add("");
        arrayList1.add("Customer");
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putStringArrayListExtra("data", arrayList1);
        intent.putStringArrayListExtra("stream", new ArrayList<String>());
        startActivity(intent);
    }

    /**
     * Clearing names which have their name already in list
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
