package com.hashmybag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.hashmybag.adapters.ChattingAdapter;
import com.hashmybag.beans.ChatingBean;
import com.hashmybag.beans.InboxBean;
import com.hashmybag.beans.OnlineBean;
import com.hashmybag.beans.UnSeenBean;
import com.hashmybag.cloudinaryintegration.CloudinaryImageUploader;
import com.hashmybag.databasehandle.DatabaseHandler;
import com.hashmybag.fragments.ChatsFragment;
import com.hashmybag.pusharintegration.PusherChat;
import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;
import com.hashmybag.util.TimeDifferentiation;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * This class is handling all the chat activities.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;
    public long stringDate;
    public ArrayList<String> streamList;
    ImageView share_button, send_button;
    LinearLayout attchment_layout;
    EditText edittext_send;
    RelativeLayout actionbar_layout;
    CircleImageView friend_image;
    ArrayList<ChatingBean> arrayList;
    ImageView action_camera, action_gallery, back_arrow;
    TextView friend_name, last_seen_friend;
    ArrayList<String> incommingList;
    ArrayList<ChatingBean> chatList;
    String customerId, FriendId, imagePath, imageUrl, finalImage;
    Button cash_on_delivery, buy_now;
    ChattingAdapter chattingAdapter;
    DatabaseHandler db;
    StickyListHeadersListView chat_list;
    Cloudinary cloudinary;
    ImageLoader loader;
    ArrayList<InboxBean> inboxList = new ArrayList<>();
    TimeDifferentiation td;
    UnSeenBean unSeenBean = new UnSeenBean();
    BroadcastReceiver broadcastReceiver_OnlineStatus = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("Status");
            String userid = intent.getStringExtra("userid");

            if (FriendId.equals(userid)) {
                if (status.equals("true")) {
                    last_seen_friend.setText("Online");
                } else {
                    last_seen_friend.setText("");
                }
            }
        }

    };
    /**
     * Broadcast receiver implemented to receive chatting arraylist
     */

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle args = intent.getBundleExtra("BUNDLE");
            ArrayList<ChatingBean> object = (ArrayList<ChatingBean>) args.getSerializable("ARRAYLIST");
            ChatingBean chatingBean = object.get(0);
            if (chatingBean.getSenderId().equalsIgnoreCase(FriendId)) {
                chattingAdapter.add(chatingBean);
                chat_list.setSelection(chatList.size() - 1);
            }
        }
    };
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildAlertMessageNoGps();
        }
    };
    private Bitmap yourSelectedImage;
    private String productId, user_id, productCode, productCreate, productDesc, productName, productImage, productPrice, productCurrency, productUpdate;
    private String email_id, mobile;
    private PusherChat pusherChat;
    private String comm_id, created_at;
    private ArrayList<OnlineBean> onlineList = new ArrayList<>();
    private String indicater;
    /**
     * Handler implemented for handling JSON response coming from create chat and create communication APIs
     */

    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.CREATE_COMMUNICATION_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        String status = jsonResponse.optString("auth");
                        String channel_data = jsonResponse.optString("channel_data");
                        JSONObject communication = jsonResponse.getJSONObject("data").getJSONObject("communication");
                        comm_id = communication.optString("id");
                        created_at = communication.optString("created_at");
                        getChatHistory(comm_id);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case WebServiceDetails.UPLOAD_IMAGE_IN_CLOUD:
                    String responseNotifResponse1 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse1);
                    Log.v("response :", "" + responseNotifResponse1);
                    try {
                        JSONObject jsonObject = new JSONObject(responseNotifResponse1);
                        String imageUrlc = jsonObject.getString("image_url");

                        ChatingBean chatingBean = new ChatingBean();
                        chatingBean.setFriendId(FriendId);
                        chatingBean.setSenderId(customerId);
                        chatingBean.setFromStream(false);
                        chatingBean.setImageUrl(imageUrlc);
                        chatingBean.setDate(td.getCurrentDate());
                        chatingBean.setMsgDate(td.getCurrentDateMsg());
                        postMessage(chatingBean);
                        addInboxTable(FriendId, "image");
                        chattingAdapter.add(chatingBean);
                        chat_list.setSelection(chatList.size() - 1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.CREATE_CHAT_PID:
                    String responseNotifResponse2 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse2);
                    Log.v("response :", "" + responseNotifResponse2);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse2);
                        String status = jsonResponse.optString("status");
                        String msg_new = jsonResponse.optString("message");
                        JSONObject chat = jsonResponse.getJSONObject("data").getJSONObject("chat");
                        //Toast.makeText(getApplication(), "Sent", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case WebServiceDetails.CHAT_HISTORY_PID:
                    String responseNotifResponse3 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse3);
                    Log.v("response :", "" + responseNotifResponse3);
                    try {
                        arrayList = new ArrayList<>();
                        arrayList.clear();
                        chatList.clear();
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse3);
                        String status = jsonResponse.optString("status");
                        final String message1 = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {
                            JSONObject data = jsonResponse.optJSONObject("data");
                            JSONObject communication = data.optJSONObject("communication");
                            JSONArray chats = communication.getJSONArray("chats");
                            for (int i = 0; i < chats.length(); i++) {
                                ChatingBean chatingBean = new ChatingBean();
                                JSONObject chat = chats.getJSONObject(i);
                                String id = chat.optString("id");
                                String message = chat.optString("message");
                                String created_at = chat.optString("created_at");
                                String attachment_url = chat.optString("attachment_url");
                                String broadcast_id = chat.optString("broadcast_id");
                                String communication_id = chat.optString("communication_id");
                                boolean from_stream = chat.optBoolean("from_stream");
                                String title = chat.optString("title");
                                chatingBean.setTitle(title);
                                chatingBean.setMessageText(message);
                                JSONObject user = chat.getJSONObject("user");
                                String user_id = user.optString("id");
                                String user_title = user.optString("title");

                                if (FriendId.equalsIgnoreCase(user_id)) {
                                    chatingBean.setFriendId(customerId);
                                    chatingBean.setSenderId(user_id);
                                } else {
                                    chatingBean.setFriendId(user_id);
                                    chatingBean.setSenderId(customerId);
                                }

                                chatingBean.setCodOption(false);
                                chatingBean.setCardOption(false);
                                chatingBean.setWalletOption(false);
                                String payment_option_string = chat.optString("payment_option");
                                if (!payment_option_string.equals("null")) {
                                    JSONObject payment_option = chat.getJSONObject("payment_option");
                                    String payment_option_id = payment_option.getString("id");
                                    boolean payment_option_cod = payment_option.getBoolean("cod");
                                    boolean payment_option_card = payment_option.getBoolean("card");
                                    boolean payment_option_wallet = payment_option.getBoolean("wallet");
                                    String payment_option_chat_id = payment_option.getString("chat_id");
                                    String payment_option_price = payment_option.getString("price");

                                    chatingBean.setPaymentOption("true");
                                    chatingBean.setPaymentId(payment_option_id);
                                    chatingBean.setCodOption(payment_option_cod);
                                    chatingBean.setCardOption(payment_option_card);
                                    chatingBean.setWalletOption(payment_option_wallet);
                                    chatingBean.setPaymentPrice(payment_option_price);

                                }
                                chatingBean.setFavorite(false);
                                String product_string = chat.optString("product");
                                if (!product_string.equals("")) {
                                    JSONObject product = chat.getJSONObject("product");
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
                                    boolean product_chat_in_wishlist = product.getBoolean("in_wishlist");
                                    chatingBean.setMsgDate(created_at);
                                    chatingBean.setProductId(product_id);
                                    chatingBean.setFavorite(product_chat_in_wishlist);
                                    chatingBean.setProductCode(product_code);
                                    chatingBean.setProductDescription(product_description);
                                    chatingBean.setProductImage(product_image_url);
                                    chatingBean.setProductPrice(product_price);
                                    chatingBean.setProductUpdated(product_updated_at);
                                    chatingBean.setProductedCreated(product_created_at);
                                    chatingBean.setChatID(product_chat_id);
                                    chatingBean.setCurrencyType(product_currency_type);
                                    chatingBean.setFavorite(product_chat_in_wishlist);
                                    chatingBean.setTitle(product_name);
                                    chatingBean.setMessageText(product_description);

                                }

                                chatingBean.setImageUrl(attachment_url);
                                chatingBean.setFromStream(from_stream);
                                chatingBean.setDate(td.getDateFormat(created_at));
                                chatingBean.setMsgDate(td.getMsgDate(created_at));
                                arrayList.add(chatingBean);
                            }

                            int sz = streamList.size();
                            if (sz > 1) {
                                addStreamInChatList();
                            }

                            chatList.addAll(arrayList);
                            chattingAdapter.notifyDataSetChanged();
                            chat_list.setSelection(chatList.size() - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (streamList.size() > 1) {
                            addStreamInChatList();
                        }
                        chatList.addAll(arrayList);
                        chattingAdapter.notifyDataSetChanged();
                        chat_list.setSelection(chatList.size() - 1);
                    }

                    break;
                case WebServiceDetails.FOLLOW_PID:
                    String responseNotifResponse4 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse4);
                    Log.v("response :", "" + responseNotifResponse4);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse4);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            //  Toast.makeText(this, "Following", Toast.LENGTH_SHORT).show();

                            final String message = jsonResponse.optString("message");


                        }
                    } catch (Exception e) {
                        Toast.makeText(ChatActivity.this, "Mobile was Wrong!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Uri mCapturedImageURI;
    private ConnectionDetector cd;
    private int cc = 0;
    private String photo, title;
    private boolean fromstream;
    private Uri fileUri;
    private Boolean isSDPresent;
    private File myfileNew;
    private String root = Environment.getExternalStorageDirectory().toString() + "/HashMyBag";
    private File myDir = new File(root);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatActivity.this.setTheme(R.style.NextTheme);
        setContentView(R.layout.chatting_layout);
        db = new DatabaseHandler(getApplicationContext());
        chatList = new ArrayList<>();
        initImageLoader();
        td = new TimeDifferentiation();
        inboxList = new ArrayList<>();
        incommingList = new ArrayList<String>();
        streamList = new ArrayList<String>();
        setContentView(R.layout.chatting_layout);
        user_id = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_ID);
        email_id = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_EMAIL);
        mobile = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_MOBILE_NUMBER);
        cd = new ConnectionDetector(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        registerReceiver(broadcastReceiver, new IntentFilter("com.hashmybag.Message"));
        registerReceiver(broadcastReceiver_OnlineStatus, new IntentFilter("com.hashmybag.OnlineStatus"));

        pusherChat = PusherChat.getInstance(this);
        onlineList = pusherChat.onlineUser;

        try {
            Intent intent = getIntent();

            incommingList = intent.getStringArrayListExtra("data");
            streamList = intent.getStringArrayListExtra("stream");
            fromstream = intent.getBooleanExtra("from_stream", false);
            FriendId = incommingList.get(0);
            photo = incommingList.get(1);
            title = incommingList.get(2);
            indicater = incommingList.get(5);
            comm_id = incommingList.get(3);

            inboxList.clear();
            inboxList = db.getInbox();


        } catch (Exception e) {
            e.printStackTrace();
        }
        init();

        int i = 0;

        while (i < onlineList.size()) {
            String onlineUserId = onlineList.get(i).getOnlineUser();
            if (onlineUserId.equals(FriendId)) {
                if (onlineList.get(i).getUserStatus().equals("true")) {
                    last_seen_friend.setText("Online");
                } else {
                    last_seen_friend.setText("");
                }
            }
            i++;
        }

        try {
            UnSeenBean unSeenBean = db.getUnSentText(FriendId);
            String id = unSeenBean.getFriendID();
            if (unSeenBean.getFriendID() != null) {
                edittext_send.setText(unSeenBean.getUnSeenText());
                edittext_send.setSelection(edittext_send.getText().length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialising all the ids over here
     */

    private void init() {
        attchment_layout = (LinearLayout) findViewById(R.id.attchment_layout1);
        share_button = (ImageView) findViewById(R.id.share_button);
        send_button = (ImageView) findViewById(R.id.send_button);
        action_camera = (ImageView) findViewById(R.id.action_camera);
        action_gallery = (ImageView) findViewById(R.id.action_gallery);
        friend_image = (CircleImageView) findViewById(R.id.friend_image);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        attchment_layout.setVisibility(View.GONE);
        friend_name = (TextView) findViewById(R.id.friend_name);
        last_seen_friend = (TextView) findViewById(R.id.last_seen_friend);
        actionbar_layout = (RelativeLayout) findViewById(R.id.actionbar_layout);
        chat_list = (StickyListHeadersListView) findViewById(R.id.chat_list);
        edittext_send = (EditText) findViewById(R.id.edittext_send);
        customerId = SharedpreferenceUtility.getInstance(getApplicationContext()).getString(Constants.CUSTOMER_ID);
        chatList.clear();
        chattingAdapter = new ChattingAdapter(this, chatList);
        chat_list.setAdapter(chattingAdapter);
        chat_list.setSelection(chatList.size() - 1);
        getInfoHeader();

        back_arrow.setOnClickListener(this);
        actionbar_layout.setOnClickListener(this);
        share_button.setOnClickListener(this);
        send_button.setOnClickListener(this);
        action_gallery.setOnClickListener(this);
        action_camera.setOnClickListener(this);
        edittext_send.setOnClickListener(this);
        chat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });

        action_camera.setVisibility(View.VISIBLE);
        action_gallery.setVisibility(View.VISIBLE);
        attchment_layout.setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void getInfoHeader() {

        if (!comm_id.equals("null")) {
            getChatHistory(comm_id);
        } else {
            createCommunication(customerId, FriendId);
            Log.d("", "customer id:" + customerId + "Friend id:" + FriendId);
        }
        friend_name.setText(title);
        loader = ImageLoader.getInstance();
        loader.displayImage(photo, friend_image,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        friend_image.setImageResource(R.mipmap.notification_bag);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

    }

    /**
     * following get executed when user presses back button.
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UnSeenBean unSeenBean = new UnSeenBean();
        String text = edittext_send.getText().toString();
        unSeenBean.setUnSeenText(text);
        unSeenBean.setFriendID(FriendId);
        db.setUnSentText(unSeenBean);
        for (int i = 0; inboxList.size() > i; i++) {
            if (inboxList.get(i).getFriendId().equals(FriendId)) {
                db.updateInboxCountAndSeen(FriendId, td.getCurrentDateForInbox());
            }
        }


        List<InboxBean> inboxBeanList = ChatsFragment.inboxList;
        for (int i = 0; i < inboxBeanList.size(); i++) {
            String friendId = inboxBeanList.get(i).getFriendId();
            if (friendId.equals(incommingList.get(0))) {
                // db.updateInbox(inboxBean);
                InboxBean inboxBean = inboxBeanList.get(i);
                inboxBean.setSmsCount(0);
                inboxBean.setLastMessage(inboxBeanList.get(i).getLastMessage());
                ChatsFragment.inboxList.set(i, inboxBean);
                break;
            }
        }
        finish();


    }

    /**
     * Calling create communication API to create communication between to users
     *
     * @param sender_id
     * @param reciever_id
     */

    public void createCommunication(String sender_id, String reciever_id) {

        try {
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sender_id", sender_id);
            jsonObject.put("receiver_id", reciever_id);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("communication", jsonObject);
            json = jsonObject1.toString();

            new WebRequestTask(this, json, _handler, "POST", WebServiceDetails.CREATE_COMMUNICATION_PID,
                    true, WebServiceDetails.CREATE_COMMUNICATION).execute();

            Log.d("", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getChatHistory(String communicartionId) {

        new WebRequestTask(this, _handler, "GET", WebServiceDetails.CHAT_HISTORY_PID,
                true, WebServiceDetails.CHAT_HISTORY + communicartionId).execute();

        Log.d("", "");

    }

    private void setFollow(String store_id) {

        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD, WebServiceDetails.FOLLOW_PID, false, WebServiceDetails.FOLLOW_URL + customerId + "/connect").execute();
    }

    /**
     * All the click events are handled over here
     * i.e Send button, action button gallery, camera and chat send button
     *
     * @param v
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));

                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    String text = edittext_send.getText().toString().trim();
                    if (text.length() == 0) {
                        edittext_send.setError("Please enter text!");
                    } else {
                        edittext_send.setError(null);
                        attchment_layout.setVisibility(View.GONE);
                        ChatingBean chatingBean = new ChatingBean();
                        chatingBean.setFriendId(FriendId);
                        chatingBean.setSenderId(customerId);
                        chatingBean.setCommunicationID(comm_id);
                        chatingBean.setFromStream(false);
                        chatingBean.setMessageText(text);
                        chatingBean.setDate(td.getCurrentDate());
                        chatingBean.setMsgDate(td.getCurrentDateMsg());
                        postMessage(chatingBean);
                        addInboxTable(FriendId, text);
                        chattingAdapter.add(chatingBean);
                        edittext_send.setText("");
                        chat_list.setSelection(chatList.size() - 1);
                        UnSeenBean unSeenBean = new UnSeenBean();
                        unSeenBean.setUnSeenText("");
                        unSeenBean.setFriendID(FriendId);
                        db.setUnSentText(unSeenBean);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Network Found!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.edittext_send:
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
                break;

            case R.id.action_gallery:
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
                cc = 0;
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_camera:
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));

                try {

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    fileUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    startActivityForResult(intent, REQUEST_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "You have denied permission for opening Camera", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.share_button:
                if (cc == 0) {
                    attchment_layout.setVisibility(View.VISIBLE);
                    action_camera.setVisibility(View.VISIBLE);
                    action_gallery.setVisibility(View.VISIBLE);
                    attchment_layout.setBackgroundColor(getResources().getColor(R.color.white));
                    inAnimation();
                    cc = 1;
                } else {
                    outAnimation();
                    attchment_layout.setVisibility(View.GONE);
                    cc = 0;
                }
                break;
            case R.id.back_arrow:
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                UnSeenBean unSeenBean = new UnSeenBean();
                String text = edittext_send.getText().toString();
                unSeenBean.setUnSeenText(text);
                unSeenBean.setFriendID(FriendId);
                db.setUnSentText(unSeenBean);

                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
                for (int i = 0; inboxList.size() > i; i++) {
                    if (inboxList.get(i).getFriendId().equals(FriendId)) {
                        db.updateInboxCountAndSeen(FriendId, td.getCurrentDate());
                    }
                }
                onBackPressed();
                finish();
                break;
            case R.id.actionbar_layout:
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));

                if (indicater.equals("Customer")) {
                    Intent intent1 = new Intent(getApplicationContext(), Profile.class);
                    intent1.putExtra("id", FriendId);
                    intent1.putExtra("from", "chat");
                    intent1.putExtra("name", title);
                    startActivity(intent1);
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), StoreDetail.class);
                    intent1.putExtra("id", FriendId);
                    startActivity(intent1);
                }

                break;

            default:
                attchment_layout.setVisibility(View.INVISIBLE);
                action_camera.setVisibility(View.GONE);
                action_gallery.setVisibility(View.GONE);
                attchment_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
                break;

        }

    }

    /**
     * Animation created for chat attachment layout
     */

    public void inAnimation() {
        Animation hide = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        attchment_layout.startAnimation(hide);

    }

    public void outAnimation() {
        Animation hide = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        attchment_layout.startAnimation(hide);
    }

    /**
     * Creat chat API is called here for creating chat id between two users so that they can trigger messages
     * from one or another end
     *
     * @param chatingBean
     */

    public void postMessage(ChatingBean chatingBean) {
        String json = null;

        try {

            JSONObject object = new JSONObject();
            object.put("user_id", chatingBean.getFriendId());
            object.put("attachment_url", chatingBean.getImageUrl());
            object.put("message", chatingBean.getMessageText());
            object.put("from_stream", "false");
            object.put("communication_id", comm_id);
            JSONObject jsonl = new JSONObject();
            jsonl.put("chat", object);
            json = jsonl.toString();

            new WebRequestTask(this, json, _handler, "POST", WebServiceDetails.CREATE_CHAT_PID, false,
                    WebServiceDetails.CREATE_CHAT + comm_id + "/chats").execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addInboxTable(String id, String message) {

        InboxBean inboxBean = new InboxBean();
        inboxBean.setFriendId(incommingList.get(0));
        inboxBean.setPhoto(incommingList.get(1));
        inboxBean.setFriendName(incommingList.get(2));
        inboxBean.setCommunicationId(comm_id);
        inboxBean.setLastMessage(message);
        String ls = td.getCurrentDateForInbox();
        inboxBean.setLastSeen(ls);
        inboxBean.setIndicater(indicater);
        inboxBean.setSmsCount(0);
        //inboxList.clear();
        //inboxList = db.getInbox();
        List<InboxBean> inboxBeanList = ChatsFragment.inboxList;
        int ch = 0;
        for (int i = 0; i < inboxBeanList.size(); i++) {
            String friendId = inboxBeanList.get(i).getFriendId();
            if (friendId.equals(id)) {
                // db.updateInbox(inboxBean);
                ChatsFragment.inboxList.set(i, inboxBean);
                ch = 1;
                break;
            }
        }


        if (ch < 1) {
            db.setInbox(inboxBean);
        }


    }

    /**
     * This method called after the complete activity called
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {

                try {
                    yourSelectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                    yourSelectedImage = Bitmap.createScaledBitmap(yourSelectedImage, yourSelectedImage.getWidth() / 4, yourSelectedImage.getHeight() / 4, true);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    imagePath = saveImage(yourSelectedImage, timeStamp + ".jpg");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                new CloudinaryImageUploader(this, WebServiceDetails.UPLOAD_IMAGE_IN_CLOUD, _handler, imagePath, true, customerId).execute();

            } else if (requestCode == SELECT_FILE) {

                Uri selectedImage = data.getData();
                imagePath = getPath(selectedImage);
                attchment_layout.setVisibility(View.GONE);
                new CloudinaryImageUploader(this, WebServiceDetails.UPLOAD_IMAGE_IN_CLOUD, _handler, imagePath, true, customerId).execute();
            }

        }

        if (requestCode == 2) {
            if (!check()) {
                buildAlertMessageNoGps();
            }
        }

    }

    public String saveImage(Bitmap finalBitmap, String fileName) {
        isSDPresent = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            myfileNew = new File(myDir, fileName);
            if (myfileNew.exists()) myfileNew.delete();

            try {
                FileOutputStream out = new FileOutputStream(myfileNew);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (myfileNew.exists())
                myfileNew.delete();
            try {
                FileOutputStream fos = new FileOutputStream(myfileNew);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MediaScannerConnection.scanFile(ChatActivity.this,
                new String[]{myfileNew.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("imageeee", "file " + path
                                + " was scanned seccessfully: " + uri);
                    }
                });

        return myfileNew.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver_OnlineStatus);
    }

    /**
     * Getting image path for uploading it to cloudinary after clicking
     *
     * @param uri
     * @return
     */

    private String getPath(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            attchment_layout.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Initializing image loader for setting up all the images.
     */

    private void initImageLoader() {
        try {
            String CACHE_DIR =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(this, CACHE_DIR);

            DisplayImageOptions defaultOptions = new
                    DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new
                    ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(defaultOptions).discCache(new
                            UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());
            ImageLoaderConfiguration config = builder.build();
            loader = ImageLoader.getInstance();
            loader.init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adding streams chat to database for using it afterwards
     * i.e Products,offers,news and social
     */

    public void addStreamInChatList() {
        ChatingBean chatingBean = new ChatingBean();
        setStreamInChat();
        chatingBean.setSenderId(FriendId);
        chatingBean.setTitle(streamList.get(1));
        chatingBean.setMessageText(streamList.get(2));
        chatingBean.setImageUrl(streamList.get(3));
        chatingBean.setProductId(streamList.get(5));
        chatingBean.setProductCode(streamList.get(6));
        chatingBean.setFromStream(true);
        chatingBean.setProductPrice(streamList.get(10));
        chatingBean.setDate(td.getDateFormat(streamList.get(11)));
        chatingBean.setMsgDate(td.getMsgDate(streamList.get(11)));
        chatingBean.setCurrencyType(streamList.get(12));
        chatingBean.setFavorite(Boolean.valueOf(streamList.get(13)));
        chatingBean.setPaymentOption("true");
        chatingBean.setCodOption(Boolean.valueOf(streamList.get(15)));
        chatingBean.setCardOption(Boolean.valueOf(streamList.get(16)));
        chatingBean.setWalletOption(Boolean.valueOf(streamList.get(17)));

        arrayList.add(chatingBean);
    }

    private void setStreamInChat() {
        String json;
        try {

            JSONObject jsonPaymentOption = new JSONObject();
            jsonPaymentOption.put("id", "");
            jsonPaymentOption.put("cod", streamList.get(15));
            jsonPaymentOption.put("card", streamList.get(16));
            jsonPaymentOption.put("wallet", streamList.get(17));
            jsonPaymentOption.put("price", "0.0");

            JSONObject jsonChat = new JSONObject();
            jsonChat.put("user_id", FriendId);
            jsonChat.put("message", streamList.get(2));
            jsonChat.put("title", streamList.get(1));
            jsonChat.put("attachment_url", streamList.get(3));
            jsonChat.put("communication_id", comm_id);
            jsonChat.put("from_stream", "true");
            jsonChat.put("product_id", streamList.get(5));
            jsonChat.put("stream_id", streamList.get(0));


            jsonChat.put("payment_option_attributes", jsonPaymentOption);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chat", jsonChat);

            json = jsonObject.toString();
            new WebRequestTask(this, json, _handler, "POST", WebServiceDetails.CREATE_CHAT_PID, false,
                    WebServiceDetails.CREATE_CHAT + comm_id + "/chats").execute();

            Log.v("", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        if (!check()) {
                            buildAlertMessageNoGps();
                        }
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean check() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(locationReciever, new IntentFilter("com.hashmybag.location"));

    }


}
