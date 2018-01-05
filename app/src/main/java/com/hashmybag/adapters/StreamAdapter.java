package com.hashmybag.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.ChatActivity;
import com.hashmybag.ChatImageView;
import com.hashmybag.R;
import com.hashmybag.beans.StreamBean;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.TimeDifferentiation;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is used for setting BaseAdapter for the streams list*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-27
 */

public class StreamAdapter extends BaseAdapter {
    Integer[] tags = {R.mipmap.offer_tag, R.mipmap.product_tag, R.mipmap.news_tag,
            R.mipmap.fb_icon, R.mipmap.twitter_icon, R.mipmap.insta_icon};
    ArrayList<StreamBean> arrayList = new ArrayList<>();
    Context context;
    ImageLoader loader;
    TimeDifferentiation td;
    /**
     * Handler used to handle the JSON response comming from the delete stream API
     */

    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.DELETE_STREAM_PID:
                    String responseNotifResponse1 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse1);
                    Log.v("response :", "" + responseNotifResponse1);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse1);
                        String status = jsonResponse.optString("status");
                        String message = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;


                default:
                    break;
            }
        }
    };
    private LayoutInflater inflater;

    public StreamAdapter(ArrayList<StreamBean> list, Context a) {

        arrayList = list;
        context = a;
        initImageLoader();
        td = new TimeDifferentiation();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
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
     * Creating layout view for stream listing and setting fragment
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.stream_item, null);

        final CircleImageView itemImage = (CircleImageView) convertView.findViewById(R.id.friends_image);
        TextView title_stream = (TextView) convertView.findViewById(R.id.title_stream);
        TextView stream_body = (TextView) convertView.findViewById(R.id.stream_body);
        TextView created_at = (TextView) convertView.findViewById(R.id.created_at);
        ImageView tagImage = (ImageView) convertView.findViewById(R.id.tags);

        String image_url = null;
        stream_body.setText(arrayList.get(position).getDescription());
        stream_body.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        stream_body.setSingleLine(true);
        stream_body.setMarqueeRepeatLimit(5);
        stream_body.setSelected(true);
        String stream_store_title = arrayList.get(position).getStoreTitle();
        String stream_title = arrayList.get(position).getTitle();
        title_stream.setText(stream_store_title);
        String cd = arrayList.get(position).getCreateAt();
        String date[] = cd.split(" ");
        String ca = date[0];
        created_at.setText(ca);

        switch (arrayList.get(position).getStreamType()) {
            case "News":
                tagImage.setImageResource(tags[2]);
                image_url = arrayList.get(position).getStorePhoto();
                break;
            case "Offers":
                tagImage.setImageResource(tags[0]);
                image_url = arrayList.get(position).getImage_url();
                break;
            case "Products":
                tagImage.setImageResource(tags[1]);
                image_url = arrayList.get(position).getImage_url();
                break;
            case "Social":
                image_url = arrayList.get(position).getStorePhoto();
                if (stream_title.equals("Social-Facebook")) {
                    tagImage.setImageResource(tags[3]);
                } else if (stream_title.equals("Social-Twitter")) {
                    tagImage.setImageResource(tags[4]);
                } else {
                    tagImage.setImageResource(tags[5]);
                }
                break;
        }
        Picasso.with(context)
                .load(image_url).resize(50, 50)
                .centerCrop().error(R.mipmap.notification_bag)
                .into(itemImage);

        final String finalImage_url = image_url;
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(context, ChatImageView.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("image_uri", finalImage_url);
                context.startActivity(intent1);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StreamBean streamBean = arrayList.get(position);

                ArrayList<String> arrayList1 = new ArrayList<String>();
                arrayList1.add(streamBean.getStoreId());
                arrayList1.add(streamBean.getStorePhoto());
                arrayList1.add(streamBean.getStoreTitle());
                arrayList1.add(streamBean.getCommId());
                arrayList1.add(streamBean.getCreateAt());
                arrayList1.add("Store");

                ArrayList<String> arrayList2 = new ArrayList<String>();
                arrayList2.add(streamBean.getStreamId());
                arrayList2.add(streamBean.getTitle());
                arrayList2.add(streamBean.getDescription());
                arrayList2.add(streamBean.getImage_url());
                arrayList2.add(streamBean.getCreateAt());
                arrayList2.add(streamBean.getProductId());
                arrayList2.add(streamBean.getProductCode());
                arrayList2.add(streamBean.getProductName());
                arrayList2.add(streamBean.getProductDesc());
                arrayList2.add(streamBean.getProducImageUrl());
                arrayList2.add(streamBean.getPrice());
                arrayList2.add(streamBean.getUpdateAt());
                arrayList2.add(streamBean.getCurrencytype());
                arrayList2.add(String.valueOf(streamBean.isInWhishlist()));
                arrayList2.add(streamBean.getStoreId());

                /*these are payment option*/
                arrayList2.add(String.valueOf(streamBean.isCodOption()));
                arrayList2.add(String.valueOf(streamBean.isCardOption()));
                arrayList2.add(String.valueOf(streamBean.isWalletOption()));

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putStringArrayListExtra("data", arrayList1);
                intent.putStringArrayListExtra("stream", arrayList2);
                intent.putExtra("from_stream", true);
                context.startActivity(intent);

            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure you want to delete this stream?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String id = arrayList.get(position).getStreamId();
                        deleteStream(id);
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

                return false;
            }
        });
        return convertView;
    }

    /**
     * Method used for web service call to delete streams API
     *
     * @param ID
     */

    public void deleteStream(String ID) {
        new WebRequestTask(context, _handler, Constants.GET_METHOD,
                WebServiceDetails.DELETE_STREAM_PID, true, WebServiceDetails.DELETE_STREAM + ID + "/seen").execute();
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

            DisplayImageOptions defaultOptions = new
                    DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
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
}
