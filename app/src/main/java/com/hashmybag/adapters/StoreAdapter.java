package com.hashmybag.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.ChatImageView;
import com.hashmybag.R;
import com.hashmybag.beans.AllStoreBean;
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
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is used for setting BaseAdapter for the store list*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-28
 */

public class StoreAdapter extends BaseAdapter {

    ArrayList<AllStoreBean> storeList = new ArrayList<>();
    Activity activity;
    TextView subscripton_image_noti;
    AllStoreBean storeBean;
    /**
     * Handler used to handle the JSON response comming from the follow and unfollow API.
     */


    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.FOLLOW_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            Toast.makeText(activity, "Following", Toast.LENGTH_SHORT).show();

                            final String message = jsonResponse.optString("message");

                            int storescr = SharedpreferenceUtility.getInstance(activity).getInt(Constants.SUBSCRIBED_STORE);
                            SharedpreferenceUtility.getInstance(activity).putInt(Constants.SUBSCRIBED_STORE, storescr + 1);
                            subscripton_image_noti.setText(String.valueOf(storescr + 1));
                        }
                    } catch (Exception e) {
                        Toast.makeText(activity, "Mobile was Wrong!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.UNFOLLOW_PID:
                    String responseNotifResponse1 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse1);
                    Log.v("response :", "" + responseNotifResponse1);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse1);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            final String message = jsonResponse.optString("message");
                            Toast.makeText(activity, "Unfollowed", Toast.LENGTH_SHORT).show();

                            int storescr = SharedpreferenceUtility.getInstance(activity).getInt(Constants.SUBSCRIBED_STORE);
                            SharedpreferenceUtility.getInstance(activity).putInt(Constants.SUBSCRIBED_STORE, storescr - 1);
                            subscripton_image_noti.setText(String.valueOf(storescr - 1));
                        }
                    } catch (Exception e) {
                        Toast.makeText(activity, "Mobile was Wrong!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public StoreAdapter(Activity activity, ArrayList<AllStoreBean> arrayList, TextView subscripton_image_noti) {
        this.storeList = arrayList;
        this.activity = activity;
        this.subscripton_image_noti = subscripton_image_noti;
        initImageLoader();
    }

    @Override
    public int getCount() {
        return storeList.size();
    }

    @Override
    public Object getItem(int position) {
        return storeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Creating layout view for store list and setting
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // if(convertView==null) {
        convertView = LayoutInflater.from(activity).inflate(R.layout.store_item, null);

        final CircleImageView userImage = (CircleImageView) convertView.findViewById(R.id.friends_image);
        TextView friendsName = (TextView) convertView.findViewById(R.id.friends_name);
        final ImageView follow_on = (ImageView) convertView.findViewById(R.id.follow_on);
        final ImageView follow_off = (ImageView) convertView.findViewById(R.id.follow_off);

        //final ToggleButton follow_select = (ToggleButton) convertView.findViewById(R.id.toggle_follow);

        storeBean = storeList.get(position);
        friendsName.setText(storeList.get(position).getStore_title());

        if (storeList.get(position).getStore_following().equalsIgnoreCase("true")) {
            // follow_select.setChecked(true);
            follow_on.setVisibility(View.VISIBLE);
            follow_off.setVisibility(View.GONE);

        } else {
            //follow_select.setChecked(false);
            follow_on.setVisibility(View.GONE);
            follow_off.setVisibility(View.VISIBLE);
        }

        /**
         * Check if listView empty
         */

        if (storeList.get(position).getStore_photo().isEmpty()) {
            userImage.setImageResource(R.mipmap.notification_bag);
        } else {
            Picasso.with(activity)
                    .load(storeList.get(position).getStore_photo())
                    .placeholder(R.mipmap.notification_bag).resize(50, 50)
                    .centerCrop().error(R.mipmap.notification_bag)
                    .into(userImage);
        }

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(activity, ChatImageView.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("image_uri", storeList.get(position).getStore_photo());
                activity.startActivity(intent1);
            }
        });
        follow_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnFollow(storeList.get(position).getStore_id());
                follow_on.setVisibility(View.GONE);
                follow_off.setVisibility(View.VISIBLE);
            }
        });
        follow_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFollow(storeList.get(position).getStore_id());
                follow_on.setVisibility(View.VISIBLE);
                follow_off.setVisibility(View.GONE);
            }
        });

            /*follow_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        follow_select.setChecked(true);
                        storeList.get(position).setStore_following(String.valueOf(true));
                       setFollow(storeList.get(position).getStore_id());

                    } else {
                        setUnFollow(storeList.get(position).getStore_id());
                        follow_select.setChecked(false);
                        storeList.get(position).setStore_following(String.valueOf(false));
                    }
                }
            });*/

        //}
        return convertView;
    }

    /**
     * Follow API is called through this Method
     *
     * @param store_id
     */

    private void setFollow(String store_id) {

        String customerId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CUSTOMER_ID);
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(activity, json, _handler, Constants.POST_METHOD, WebServiceDetails.FOLLOW_PID, false, WebServiceDetails.FOLLOW_URL + customerId + "/connect").execute();
    }

    /**
     * UnFollow API is called through this Method
     *
     * @param store_id
     */

    private void setUnFollow(String store_id) {
        String customerId = SharedpreferenceUtility.getInstance(activity).getString(Constants.CUSTOMER_ID);

        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new WebRequestTask(activity, json, _handler, Constants.POST_METHOD, WebServiceDetails.UNFOLLOW_PID, true, WebServiceDetails.UNFOLLOW_URL + customerId + "/disconnect").execute();
    }

    /**
     * ImageLoader: used to set all the images comming from server
     */

    private void initImageLoader() {
        try {
            ImageLoader loader;

            String CACHE_DIR =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(activity, CACHE_DIR);

            DisplayImageOptions defaultOptions = new
                    DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new
                    ImageLoaderConfiguration.Builder(activity)
                    .defaultDisplayImageOptions(defaultOptions).discCache(new
                            UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());
            ImageLoaderConfiguration config = builder.build();
            loader = ImageLoader.getInstance();
            loader.init(config);
        } catch (Exception e) {

        }
    }
}
