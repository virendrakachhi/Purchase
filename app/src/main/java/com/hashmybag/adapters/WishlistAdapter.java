package com.hashmybag.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.ChatActivity;
import com.hashmybag.R;
import com.hashmybag.beans.WishlistBean;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is used for setting adapter on to the wishlist*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-05-19
 */

public class WishlistAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<WishlistBean> arrayList = new ArrayList<>();
    Context context;
    ImageLoader loader;
    TextView no_item;
    /**
     * Handler used to handle JSON response for remove item from wishlist
     */

    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            ArrayList<WishlistBean> arrayList1 = new ArrayList<>();
            loader = ImageLoader.getInstance();

            switch (msg.what) {
                case WebServiceDetails.GET_WHISHLIST_REMOVE_PID:
                    String responseNotifResponse = (String) msg.obj;

                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            final String message = jsonResponse.optString("message");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "some thing went wrong", Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Toast.makeText(context, "No subscibed stores available", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }
    };

    public WishlistAdapter(ArrayList<WishlistBean> list, Context a, TextView no_item) {

        arrayList = list;
        context = a;
        this.no_item = no_item;
        inflater = (LayoutInflater) a.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initImageLoader();
    }

    @Override
    public int getCount() {
        if (arrayList.size() < 1) {
            no_item.setVisibility(View.VISIBLE);
        } else {
            no_item.setVisibility(View.GONE);
        }
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Creating layout view for wishlist  and setting
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.wislist_item, null);

            holder = new ViewHolder();
            holder.itemImage = (ImageView) vi.findViewById(R.id.item_image);
            holder.itemDetail = (TextView) vi.findViewById(R.id.item_detail);
            holder.itemDate = (TextView) vi.findViewById(R.id.date1);
            holder.item_remove = (Button) vi.findViewById(R.id.item_remove);
            holder.layout = (RelativeLayout) vi.findViewById(R.id.item1);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        String desc = arrayList.get(position).getProductDescription();
        holder.itemDetail.setText(desc);
        String date = arrayList.get(position).getProductCreateAt();
        String dd = date.substring(0, date.length() - 9);
        holder.itemDate.setText(dd);

        loader = ImageLoader.getInstance();

        loader.displayImage(arrayList.get(position).getProductImage(), holder.itemImage,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.itemImage.setImageResource(R.mipmap.notification_bag);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

        holder.item_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure you want to remove this item?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(context,"You clicked yes button",Toast.LENGTH_LONG).show();
                        setWishlistRemove(arrayList.get(position).getProductId());
                        arrayList.remove(position);
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WishlistBean wishlistBean = arrayList.get(position);

                ArrayList<String> arrayList1 = new ArrayList<String>();
                arrayList1.add(wishlistBean.getStoreId());
                arrayList1.add(wishlistBean.getStorePhoto());
                arrayList1.add(wishlistBean.getStoreTitle());
                arrayList1.add(wishlistBean.getCommunicationId());
                arrayList1.add(wishlistBean.getStreamTime());
                arrayList1.add("Store");

                ArrayList<String> arrayList2 = new ArrayList<String>();
                arrayList2.add(wishlistBean.getProductId());
                arrayList2.add(wishlistBean.getProductName());
                arrayList2.add(wishlistBean.getProductDescription());
                arrayList2.add(wishlistBean.getProductImage());
                arrayList2.add(wishlistBean.getProductCreateAt());
                arrayList2.add(wishlistBean.getProductId());
                arrayList2.add(wishlistBean.getProductCode());
                arrayList2.add(wishlistBean.getProductDescription());
                arrayList2.add(wishlistBean.getProductImage());

                arrayList2.add(wishlistBean.getProductPrice());
                arrayList2.add(wishlistBean.getProductCreateAt());
                arrayList2.add(wishlistBean.getProductCreateAt());
                arrayList2.add(wishlistBean.getCurrencyType());
                arrayList2.add("true");
                arrayList2.add(wishlistBean.getProductStoreId());
                arrayList2.add("false");
                arrayList2.add("false");
                arrayList2.add("false");


                Intent intent = new Intent(context, ChatActivity.class);
                intent.putStringArrayListExtra("data", arrayList1);
                intent.putStringArrayListExtra("stream", arrayList2);
                context.startActivity(intent);
            }
        });

        return vi;
    }

    /**
     * Method used for removing items from Wishlist
     *
     * @param item_id
     */

    private void setWishlistRemove(String item_id) {

        String id = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_ID);
        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("product_id", item_id);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(context, json, _handler, Constants.POST_METHOD, WebServiceDetails.GET_WHISHLIST_REMOVE_PID, true, WebServiceDetails.GET_WHISHLIST_REMOVE_URL + id + "/remove_from_wishlist").execute();
    }

    /**
     * Methos used for setting all image
     */

    private void initImageLoader() {
        try {
            String CACHE_DIR =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(context, CACHE_DIR);
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new
                    ImageLoaderConfiguration.Builder(context)
                    .defaultDisplayImageOptions(defaultOptions).discCache(new
                            UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());
            ImageLoaderConfiguration config = builder.build();
            loader = ImageLoader.getInstance();
            loader.init(config);
        } catch (Exception e) {

        }
    }

    public static class ViewHolder {

        public TextView itemDetail;
        public TextView itemDate;
        public Button item_remove;
        public ImageView itemImage;
        public RelativeLayout layout;

    }

}
