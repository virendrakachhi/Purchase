package com.hashmybag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hashmybag.adapters.ListAdapter;
import com.hashmybag.beans.AllStoreBean;
import com.hashmybag.beans.ChannelBean;
import com.hashmybag.beans.CommunicationBean;
import com.hashmybag.beans.StoreInfoBean;
import com.hashmybag.servercommunication.ConnectionDetector;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;
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
import java.util.ArrayList;

/**
 * This class is used for showing store details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */

public class StoreDetail extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    ListView listView;
    ConnectionDetector cd;
    ListAdapter adapter;
    ImageView back_arrow, nike_image, chatLink, chatLinkDe;
    ArrayList<AllStoreBean> similarStoreList = new ArrayList<>();
    ArrayList<ChannelBean> channellist = new ArrayList<>();
    ArrayList<StoreInfoBean> storeinfoList = new ArrayList<>();
    ImageView off_togP, off_togN, off_togO, red_toggleP, red_toggleN, red_toggleO;
    ToggleButton toggle_offers, toggle_product, toggle_news, follow_toggle;
    ImageLoader loader;
    String store_id;
    TextView follow_text, address_text, name_text, friend_name;
    int click = 0;
    BroadcastReceiver locationReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            buildAlertMessageNoGps();
        }
    };
    private RelativeLayout mainLayout;
    private String store_image_url;
    private String storeId, storeImage, storeTitle, commId;
    /**
     * Handler to handle all the web service called above.
     * i.e similar stores, get all stores, follow, unfollow etc.
     */

    private String message;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.SIMILAR_STORES_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    similarStoreList.clear();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);
                        message = jsonResponse.optString("message");

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            JSONObject user = jsonResponse.getJSONObject("data");
                            JSONArray stores = user.getJSONArray("stores");
                            for (int i = 0; i < stores.length(); i++) {
                                AllStoreBean allStoreBean = new AllStoreBean();
                                JSONObject jsonResponse1 = stores.getJSONObject(i);
                                String stror_id = jsonResponse1.optString("id");
                                String stror_email = jsonResponse1.optString("email");
                                String stror_photo = jsonResponse1.optString("photo");
                                String stror_latitude = jsonResponse1.optString("latitude");
                                String stror_longitude = jsonResponse1.optString("longitude");
                                String stror_address1 = jsonResponse1.optString("address1");
                                String stror_title = jsonResponse1.optString("title");
                                String stror_description = jsonResponse1.optString("description");
                                String mobile_number = jsonResponse1.optString("mobile_number");
                                String category_id = jsonResponse1.optString("category_id");
                                String store_active = jsonResponse1.optString("active");
                                String twitter_username = jsonResponse1.optString("twitter_username");
                                String facebook_page = jsonResponse1.optString("facebook_page");
                                String instagram_user_name = jsonResponse1.optString("instagram_user_name");
                                String following = jsonResponse1.optString("following");


                                allStoreBean.setStore_id(stror_id);
                                allStoreBean.setStore_email(stror_email);
                                allStoreBean.setStore_photo(stror_photo);
                                allStoreBean.setStore_latitude(stror_latitude);
                                allStoreBean.setStore_longitude(stror_longitude);
                                allStoreBean.setStore_address1(stror_address1);
                                allStoreBean.setStore_title(stror_title);
                                allStoreBean.setStore_description(stror_description);
                                allStoreBean.setMobile_number(mobile_number);
                                allStoreBean.setStore_category_id(category_id);
                                allStoreBean.setStore_active(store_active);
                                allStoreBean.setStore_twitter_username(twitter_username);
                                allStoreBean.setStore_facebook_page(facebook_page);
                                allStoreBean.setStore_instagram_user_name(instagram_user_name);
                                allStoreBean.setStore_following(following);
                                similarStoreList.add(allStoreBean);


                            }

                            adapter = new ListAdapter(StoreDetail.this, similarStoreList);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            //   Toast.makeText(NearByFriend.this,message,Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(StoreDetail.this, "Unable to get similar store!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.GET_STORE_INFO_PID:
                    String responseNotifResponse1 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse1);
                    Log.v("response :", "" + responseNotifResponse1);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse1);

                        String status = jsonResponse.optString("status");
                        message = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {

                            JSONObject user = jsonResponse.getJSONObject("data");
                            JSONObject store = user.getJSONObject("store");
                            String title = store.optString("title");
                            String description = store.optString("description");
                            String email = store.optString("email");
                            String location = store.optString("location");
                            String address1 = store.optString("address1");
                            String photo = store.optString("photo");
                            String mobile_number = store.optString("mobile_number");
                            String landline_number = store.optString("landline_number");
                            String latitude = store.optString("latitude");
                            String longitude = store.optString("longitude");
                            String active = store.optString("active");
                            String identity_proof = store.optString("identity_proof");
                            String registration_certificate = store.optString("registration_certificate");
                            String twitter_username = store.optString("twitter_username");
                            String facebook_page = store.optString("facebook_page");
                            String instagram_user_name = store.optString("instagram_user_name");
                            String authentication_token = store.optString("authentication_token");
                            String id1 = store.optString("id");
                            String communication_id = store.optString("communication_id");
                            String following = store.optString("following");

                            storeId = id1;
                            storeTitle = title;
                            storeImage = photo;
                            commId = communication_id;

                            if (following.equals("true")) {
                                chatLink.setVisibility(View.VISIBLE);
                            } else {
                                chatLinkDe.setVisibility(View.VISIBLE);
                            }


                            JSONArray channels = store.getJSONArray("channels");
                            for (int i = 0; i < channels.length(); i++) {
                                JSONObject jsonChannels = channels.getJSONObject(i);
                                String channel_id = jsonChannels.optString("id");
                                String channel_name = jsonChannels.optString("name");
                                String channel_description = jsonChannels.optString("description");
                                String channel_store_id = jsonChannels.optString("store_id");
                                String channel_broadcasts_count = jsonChannels.optString("broadcasts_count");
                                String channel_enabled = jsonChannels.optString("enabled");

                                if (channel_name.equalsIgnoreCase("Products") || channel_name.equalsIgnoreCase("Offers") || channel_name.equalsIgnoreCase("News")) {
                                    ChannelBean channelBean = new ChannelBean();
                                    channelBean.setChannel_id(channel_id);
                                    channelBean.setChannel_name(channel_name);
                                    channelBean.setChannel_description(channel_description);
                                    channelBean.setChannel_store_id(channel_store_id);
                                    channelBean.setChannel_broadcasts_count(channel_broadcasts_count);
                                    channelBean.setChannel_enabled(channel_enabled);
                                    channellist.add(channelBean);

                                }

                            }

                            String id = store.optString("id");

                            JSONObject category = store.getJSONObject("category");
                            String category_id = category.optString("id");
                            String category_name = category.optString("name");
                            String category_created_at = category.optString("created_at");
                            String category_updated_at = category.optString("updated_at");

                            String unread_msg_count = store.optString("unread_msg_count");

                           /* JSONArray unread_communications = store.getJSONArray("unread_communications");
                            for(int i=0;i<unread_communications.length();i++) {
                                JSONObject jsonCommunication = unread_communications.getJSONObject(i);

                                String channel_id=jsonCommunication.optString("id");
                                String channel_name=jsonCommunication.optString("name");
                                String channel_store_id=jsonCommunication.optString("store_id");
                                String channel_broadcasts_count=jsonCommunication.optString("broadcasts_count");
                                CommunicationBean communicationBean=new CommunicationBean();
                                communicationBean.setChannel_id(channel_id);
                                communicationBean.setChannel_name(channel_name);
                                communicationBean.setChannel_store_id(channel_store_id);
                                communicationBean.setChannel_broadcasts_count(channel_broadcasts_count);
                                communicationList.add(i,communicationBean);
                            }*/
                            StoreInfoBean storeinfobean = new StoreInfoBean();
                            storeinfobean.setTitle(title);
                            storeinfobean.setDescription(description);
                            storeinfobean.setEmail(email);
                            storeinfobean.setLocation(location);
                            storeinfobean.setAddress1(address1);
                            storeinfobean.setPhoto(photo);
                            storeinfobean.setMobile_number(mobile_number);
                            storeinfobean.setLandline_number(landline_number);
                            storeinfobean.setLatitude(latitude);
                            storeinfobean.setLongitude(longitude);
                            storeinfobean.setActive(active);
                            storeinfobean.setIdentity_proof(identity_proof);
                            storeinfobean.setRegistration_certificate(registration_certificate);
                            storeinfobean.setTwitter_username(twitter_username);
                            storeinfobean.setFacebook_page(facebook_page);
                            storeinfobean.setInstagram_user_name(instagram_user_name);
                            storeinfobean.setAuthentication_token(authentication_token);
                            storeinfobean.setChannelBeanArrayList(channellist);
                            storeinfobean.setId(id);
                            storeinfobean.setCategory_id(category_id);
                            storeinfobean.setCategory_name(category_name);
                            storeinfobean.setCategory_created_at(category_created_at);
                            storeinfobean.setCategory_updated_at(category_updated_at);
                            storeinfobean.setFollowing(following);
                            storeinfobean.setUnread_msg_count(unread_msg_count);
                            //storeinfobean.setCommunicationBeanArrayList(communicationList);
                            storeinfoList.add(storeinfobean);

                            setData();
                            getSimilarStores(store_id);

                            //Toast.makeText(StoreDetail.this,message,Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(StoreDetail.this, "Unable to get Store info!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.FOLLOW_PID:
                    String responseNotifResponse2 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse2);
                    Log.v("response :", "" + responseNotifResponse2);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse2);
                        message = jsonResponse.optString("message");

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            Toast.makeText(StoreDetail.this, "Following", Toast.LENGTH_SHORT).show();
                            off_togO.setVisibility(View.GONE);
                            red_toggleO.setVisibility(View.VISIBLE);
                            off_togN.setVisibility(View.GONE);
                            red_toggleN.setVisibility(View.VISIBLE);
                            off_togP.setVisibility(View.GONE);
                            red_toggleP.setVisibility(View.VISIBLE);
                            int storescr = SharedpreferenceUtility.getInstance(StoreDetail.this).getInt(Constants.SUBSCRIBED_STORE);
                            SharedpreferenceUtility.getInstance(StoreDetail.this).putInt(Constants.SUBSCRIBED_STORE, storescr + 1);


                        }
                    } catch (Exception e) {
                        Toast.makeText(StoreDetail.this, "Data unable to get!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.UNFOLLOW_PID:
                    String responseNotifResponse3 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse3);
                    Log.v("response :", "" + responseNotifResponse3);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse3);
                        message = jsonResponse.optString("message");

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            Toast.makeText(StoreDetail.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                            off_togO.setVisibility(View.VISIBLE);
                            red_toggleO.setVisibility(View.GONE);
                            off_togN.setVisibility(View.VISIBLE);
                            red_toggleN.setVisibility(View.GONE);
                            off_togP.setVisibility(View.VISIBLE);
                            red_toggleP.setVisibility(View.GONE);

                            int storescr = SharedpreferenceUtility.getInstance(StoreDetail.this).getInt(Constants.SUBSCRIBED_STORE);
                            SharedpreferenceUtility.getInstance(StoreDetail.this).putInt(Constants.SUBSCRIBED_STORE, storescr - 1);


                        }
                    } catch (Exception e) {
                        Toast.makeText(StoreDetail.this, "unable to set unfollow!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.CHANGE_TOGGLE_DISABLE_PID:
                    String responseNotifResponse4 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse4);
                    Log.v("response :", "" + responseNotifResponse4);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse4);
                        message = jsonResponse.optString("message");
                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            //Toast.makeText(StoreDetail.this, "Disabled", Toast.LENGTH_SHORT).show();
                            switch (click) {
                                case 0:
                                    off_togP.setVisibility(View.VISIBLE);
                                    red_toggleP.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    off_togN.setVisibility(View.VISIBLE);
                                    red_toggleN.setVisibility(View.GONE);
                                    break;
                                case 2:
                                    off_togO.setVisibility(View.VISIBLE);
                                    red_toggleO.setVisibility(View.GONE);
                                    break;
                            }

                        } else if (status.equalsIgnoreCase("400")) {
                            Toast.makeText(StoreDetail.this, message, Toast.LENGTH_LONG).show();
                         /*   toggle_product.setChecked(false);
                            toggle_news.setChecked(false);
                            toggle_offers.setChecked(false);
*/
                            switch (click) {
                                case 0:
                                    off_togP.setVisibility(View.GONE);
                                    red_toggleP.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    off_togN.setVisibility(View.GONE);
                                    red_toggleN.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    off_togO.setVisibility(View.GONE);
                                    red_toggleO.setVisibility(View.VISIBLE);
                                    break;
                            }

                        } else {

                            Toast.makeText(StoreDetail.this, message, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(StoreDetail.this, "Data unable to get", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.CHANGE_TOGGLE_ENABLE_PID:
                    String responseNotifResponse5 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse5);
                    Log.v("response :", "" + responseNotifResponse5);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse5);

                        String messageNew = jsonResponse.optString("message");
                        String status = jsonResponse.optString("status");

                        if (status.equalsIgnoreCase("200")) {

                            // Toast.makeText(StoreDetail.this, "Enabled", Toast.LENGTH_SHORT).show();

                            switch (click) {
                                case 0:
                                    off_togP.setVisibility(View.GONE);
                                    red_toggleP.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    off_togN.setVisibility(View.GONE);
                                    red_toggleN.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    off_togO.setVisibility(View.GONE);
                                    red_toggleO.setVisibility(View.VISIBLE);
                                    break;
                            }
                        } else if (status.equalsIgnoreCase("400")) {
                            Toast.makeText(StoreDetail.this, messageNew, Toast.LENGTH_LONG).show();
                           /* toggle_product.setChecked(true);
                            toggle_news.setChecked(true);
                            toggle_offers.setChecked(true);
*/
                            switch (click) {
                                case 0:
                                    off_togP.setVisibility(View.VISIBLE);
                                    red_toggleP.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    off_togN.setVisibility(View.VISIBLE);
                                    red_toggleN.setVisibility(View.GONE);
                                    break;
                                case 2:
                                    off_togO.setVisibility(View.VISIBLE);
                                    red_toggleO.setVisibility(View.GONE);
                                    break;
                            }
                        } else {
                            Toast.makeText(StoreDetail.this, messageNew, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(StoreDetail.this, "Mobile was Wrong!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;


                default:
                    break;
            }
        }
    };
    private ArrayList<CommunicationBean> communicationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StoreDetail.this.setTheme(R.style.NextTheme);
        setContentView(R.layout.store_detail);
        initImageLoader();
        follow_toggle = (ToggleButton) findViewById(R.id.toggle_followed);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        cd = new ConnectionDetector(this);

        follow_text = (TextView) findViewById(R.id.follow1);

        init();

        try {
            Intent intent = getIntent();
            store_id = intent.getStringExtra("id");
            getAllDetails(store_id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (storeinfoList.size() != 0)
            setData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void init() {
        listView = (ListView) findViewById(R.id.similar_store1);
        address_text = (TextView) findViewById(R.id.address);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        chatLink = (ImageView) findViewById(R.id.chat_link);
        chatLinkDe = (ImageView) findViewById(R.id.chat_link_de);
        off_togN = (ImageView) findViewById(R.id.off_togN);
        off_togO = (ImageView) findViewById(R.id.off_togO);
        off_togP = (ImageView) findViewById(R.id.off_togP);
        red_toggleP = (ImageView) findViewById(R.id.red_toggleP);
        red_toggleN = (ImageView) findViewById(R.id.red_toggleN);
        red_toggleO = (ImageView) findViewById(R.id.red_toggleO);

        name_text = (TextView) findViewById(R.id.name);
        friend_name = (TextView) findViewById(R.id.friend_name);
       /* toggle_product = (ToggleButton) findViewById(R.id.toggle_product);
        toggle_news = (ToggleButton) findViewById(R.id.toggle_news);
        toggle_offers = (ToggleButton) findViewById(R.id.toggle_offers);
*/
        back_arrow.setOnClickListener(this);
        chatLink.setOnClickListener(this);
        nike_image = (ImageView) findViewById(R.id.nike_image);
        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    Intent intent = new Intent(getApplicationContext(), StoreDetail.class);
                    intent.putExtra("id", similarStoreList.get(position).getStore_id());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No Network Found", Toast.LENGTH_LONG).show();
                }
            }
        });

        nike_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(StoreDetail.this, ChatImageView.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("image_uri", store_image_url);
                startActivity(intent1);

            }
        });

        chatLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList1 = new ArrayList<String>();
                arrayList1.add(storeId);
                arrayList1.add(storeImage);
                arrayList1.add(storeTitle);
                arrayList1.add(commId);
                arrayList1.add("null");
                arrayList1.add("store");


                /*Setting all data into the list and sending intent to ChatActivity for
                 * intialise chating, to the Friend or Merchant*/

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putStringArrayListExtra("data", arrayList1);
                intent.putStringArrayListExtra("stream", new ArrayList<String>());
                startActivity(intent);
            }
        });
    }

    /**
     * Setting store details including name, id , image etc.
     */

    private void setData() {
        store_image_url = storeinfoList.get(0).getPhoto();
        loader.displayImage(store_image_url, nike_image,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        nike_image.setImageResource(R.mipmap.notification_bag);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

        address_text.setText(storeinfoList.get(0).getLocation());
        friend_name.setText(storeinfoList.get(0).getTitle());
        name_text.setText(storeinfoList.get(0).getTitle());
        if (storeinfoList.get(0).getFollowing().equalsIgnoreCase("true")) {
            follow_toggle.setChecked(true);
            follow_text.setText("Following");
        } else {
            follow_toggle.setChecked(false);
            follow_text.setText("Follow");
        }

        //for Offer check
        final ArrayList<ChannelBean> channelbean = storeinfoList.get(0).getChannelBeanArrayList();
        for (int i = 0; i < channelbean.size(); i++) {
            ChannelBean channels = channelbean.get(i);
            if (channels.getChannel_name().equalsIgnoreCase("Products")) {
                if (channels.getChannel_enabled().equalsIgnoreCase("true")) {
                    //toggle_product.setChecked(false);
                    off_togP.setVisibility(View.GONE);
                    red_toggleP.setVisibility(View.VISIBLE);

                } else {
                    //toggle_product.setChecked(true);
                    off_togP.setVisibility(View.VISIBLE);
                    red_toggleP.setVisibility(View.GONE);

                }
            } else if (channels.getChannel_name().equalsIgnoreCase("Offers")) {
                if (channels.getChannel_enabled().equalsIgnoreCase("true")) {
                    // toggle_offers.setChecked(false);
                    off_togO.setVisibility(View.GONE);
                    red_toggleO.setVisibility(View.VISIBLE);

                } else {
                    // toggle_offers.setChecked(true);
                    off_togO.setVisibility(View.VISIBLE);
                    red_toggleO.setVisibility(View.GONE);

                }
            } else if (channels.getChannel_name().equalsIgnoreCase("News")) {
                if (channels.getChannel_enabled().equalsIgnoreCase("true")) {
                    //toggle_news.setChecked(false);
                    off_togN.setVisibility(View.GONE);
                    red_toggleN.setVisibility(View.VISIBLE);

                } else {
                    // toggle_news.setChecked(true);
                    off_togN.setVisibility(View.VISIBLE);
                    red_toggleN.setVisibility(View.GONE);
                }
            }
        }

        follow_toggle.setOnCheckedChangeListener(this);
        red_toggleN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 1;
                off_togN.setVisibility(View.VISIBLE);
                red_toggleN.setVisibility(View.GONE);
                String channel_id = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("News"))
                        channel_id = channelbean.get(i).getChannel_id();
                }
                changeDisable(channel_id);

            }
        });
        red_toggleP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 0;

                off_togP.setVisibility(View.VISIBLE);
                red_toggleP.setVisibility(View.GONE);
                String channel_id = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("Products"))
                        channel_id = channelbean.get(i).getChannel_id();
                }
                changeDisable(channel_id);

            }
        });
        red_toggleO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 2;

                off_togO.setVisibility(View.VISIBLE);
                red_toggleO.setVisibility(View.GONE);
                String channel_id = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("Offers"))
                        channel_id = channelbean.get(i).getChannel_id();
                }
                changeDisable(channel_id);

            }
        });

        off_togN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 1;

                off_togN.setVisibility(View.GONE);
                red_toggleN.setVisibility(View.VISIBLE);
                String channel_id = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("News"))
                        channel_id = channelbean.get(i).getChannel_id();
                }
                changeEnable(channel_id);

            }
        });
        off_togP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 0;

                off_togP.setVisibility(View.GONE);
                red_toggleP.setVisibility(View.VISIBLE);
                String channel_id = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("Products"))
                        channel_id = channelbean.get(i).getChannel_id();
                }
                changeEnable(channel_id);

            }
        });
        off_togO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = 2;

                off_togO.setVisibility(View.GONE);
                red_toggleO.setVisibility(View.VISIBLE);
                String channel_id = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("Offers"))
                        channel_id = channelbean.get(i).getChannel_id();
                }
                changeEnable(channel_id);

            }
        });
        mainLayout.setVisibility(View.VISIBLE);


    }

    /**
     * Web service call to API for following the particular store
     *
     * @param store_id
     */

    private void setFollow(String store_id) {

        String customerId = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_ID);
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD, WebServiceDetails.FOLLOW_PID, false,
                WebServiceDetails.FOLLOW_URL + customerId + "/connect").execute();
    }

    /**
     * Web service call for unfollowing the particular store.
     *
     * @param store_id
     */

    private void setUnFollow(String store_id) {

        String customerId = SharedpreferenceUtility.getInstance(this).getString(Constants.CUSTOMER_ID);

        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD, WebServiceDetails.UNFOLLOW_PID, false,
                WebServiceDetails.UNFOLLOW_URL + customerId + "/disconnect").execute();
    }

    /**
     * Web service call for getting all stores information and details
     *
     * @param id
     */

    private void getAllDetails(String id) {

        new WebRequestTask(StoreDetail.this, _handler, Constants.GET_METHOD, WebServiceDetails.GET_STORE_INFO_PID, true,
                WebServiceDetails.GET_STORE_INFO + id).execute();
    }

    /**
     * API call for getting similar stores
     *
     * @param id
     */

    private void getSimilarStores(String id) {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("search_by", "similiar_store");
            jsonObject.put("store_id", id);
            jsonObject.put("per_page", "20");
            jsonObject.put("offset", "0");


            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.SIMILAR_STORES_PID, false, WebServiceDetails.SIMILAR_STORES).execute();
    }

    /**
     * Web service call for changing status of stores
     *
     * @param channel_id
     */

    private void changeEnable(String channel_id) {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channel_id", channel_id);
            jsonObject.put("status", "enable");

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.CHANGE_TOGGLE_ENABLE_PID, false, WebServiceDetails.CHANGE_TOGGLE_ENABLE + store_id + "/change_channel_status").execute();
    }

    /**
     * Change status disable API call for getting response
     *
     * @param channel_id
     */

    private void changeDisable(String channel_id) {

        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channel_id", channel_id);
            jsonObject.put("status", "disable");
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(this, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.CHANGE_TOGGLE_DISABLE_PID, false, WebServiceDetails.CHANGE_TOGGLE_DISABLE + store_id + "/change_channel_status").execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                finish();
                break;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

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

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ArrayList<ChannelBean> channelbean = storeinfoList.get(0).getChannelBeanArrayList();


        switch (buttonView.getId()) {

            case R.id.off_togN:
                String channel_id = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("Products"))
                        channel_id = channelbean.get(i).getChannel_id();
                }
                if (isChecked) {

                    toggle_product.setChecked(true);
                    //Toast.makeText(this,"Disable",Toast.LENGTH_LONG).show();
                    changeDisable(channel_id);

                } else {

                    toggle_product.setChecked(false);
                    //    Toast.makeText(this,"Enable",Toast.LENGTH_LONG).show();
                    changeEnable(channel_id);
                }

                break;
            case R.id.off_togP:
                String channel_id1 = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("News"))
                        channel_id1 = channelbean.get(i).getChannel_id();

                }

                if (isChecked) {

                    toggle_news.setChecked(true);
                    //   Toast.makeText(this,"Disable",Toast.LENGTH_LONG).show();
                    changeDisable(channel_id1);


                } else {

                    toggle_news.setChecked(false);
                    //  Toast.makeText(this,"Enable",Toast.LENGTH_LONG).show();
                    changeEnable(channel_id1);

                }

                break;
            case R.id.off_togO:
                String channel_id2 = null;
                for (int i = 0; i < channelbean.size(); i++) {
                    if (channelbean.get(i).getChannel_name().equalsIgnoreCase("Offers"))
                        channel_id2 = channelbean.get(i).getChannel_id();

                }
                if (isChecked) {

                    toggle_offers.setChecked(true);
                    //   Toast.makeText(this,"Disable",Toast.LENGTH_LONG).show();
                    changeDisable(channel_id2);

                } else {

                    toggle_offers.setChecked(false);
                    //    Toast.makeText(this,"Enable",Toast.LENGTH_LONG).show();
                    changeEnable(channel_id2);

                }

                break;
            case R.id.toggle_followed:
                if (isChecked) {

                    follow_toggle.setChecked(true);
                    setFollow(store_id);
                    follow_text.setText("Following");
                    off_togO.setVisibility(View.GONE);
                    off_togN.setVisibility(View.GONE);
                    off_togP.setVisibility(View.GONE);
                    red_toggleP.setVisibility(View.VISIBLE);
                    red_toggleO.setVisibility(View.VISIBLE);
                    red_toggleN.setVisibility(View.VISIBLE);
                    chatLink.setVisibility(View.VISIBLE);
                    chatLinkDe.setVisibility(View.GONE);


                } else {
                    follow_text.setText("Follow");
                    setUnFollow(store_id);
                    off_togO.setVisibility(View.VISIBLE);
                    off_togN.setVisibility(View.VISIBLE);
                    off_togP.setVisibility(View.VISIBLE);
                    red_toggleP.setVisibility(View.GONE);
                    red_toggleO.setVisibility(View.GONE);
                    red_toggleN.setVisibility(View.GONE);
                    chatLink.setVisibility(View.GONE);
                    chatLinkDe.setVisibility(View.VISIBLE);

                }

                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(locationReciever, new IntentFilter("com.hashmybag.location"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationReciever);
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
                            Toast.makeText(StoreDetail.this, "GPS is not enabled!", Toast.LENGTH_SHORT).show();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (!check()) {
                Toast.makeText(StoreDetail.this, "GPS is not enabled!", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
