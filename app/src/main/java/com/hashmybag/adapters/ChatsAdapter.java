package com.hashmybag.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hashmybag.ChatImageView;
import com.hashmybag.R;
import com.hashmybag.beans.InboxBean;
import com.hashmybag.util.TimeDifferentiation;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is used as a adapter for chatting list.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */

public class ChatsAdapter extends BaseAdapter {

    ArrayList<InboxBean> arrayList = new ArrayList<>();
    Activity context;
    ImageLoader loader;
    TimeDifferentiation td;

    public ChatsAdapter(ArrayList<InboxBean> list, Activity a) {
        arrayList = list;
        context = a;
        initImageLoader();
        td = new TimeDifferentiation();
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
     * Setting chat box back ground view.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chats_item, null);
            holder = new ViewHolder();
            holder.userImage = (CircleImageView) convertView.findViewById(R.id.friends_image);
            holder.friendsName = (TextView) convertView.findViewById(R.id.friends_name);
            holder.friendsStatus = (TextView) convertView.findViewById(R.id.friends_status);
            holder.lastSeen = (TextView) convertView.findViewById(R.id.last_seen);
            holder.smscount = (TextView) convertView.findViewById(R.id.smscount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String message = arrayList.get(position).getLastMessage();
        if (message.equalsIgnoreCase("null") || message.equalsIgnoreCase("") || message == null) {
            holder.friendsStatus.setText("image");
        } else {
            holder.friendsStatus.setText(message);
        }


        holder.friendsName.setText(arrayList.get(position).getFriendName());
        String ls = td.getTimeDifference(arrayList.get(position).getLastSeen());
        holder.lastSeen.setText(ls);
        int count = arrayList.get(position).getSmsCount();
        if (count > 0) {
            holder.smscount.setVisibility(View.VISIBLE);
            holder.smscount.setText(String.valueOf(count));
            holder.lastSeen.setTextColor(Color.RED);
        } else {
            holder.smscount.setVisibility(View.GONE);
            holder.lastSeen.setTextColor(Color.GRAY);
        }
        loader = ImageLoader.getInstance();

        final ViewHolder finalHolder = holder;
        loader.displayImage(arrayList.get(position).getPhoto(), holder.userImage,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        finalHolder.userImage.setImageResource(R.mipmap.notification_bag);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(context, ChatImageView.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("image_uri", arrayList.get(position).getPhoto());
                context.startActivity(intent1);
            }
        });

        return convertView;
    }

    /**
     * ImageLoader: used to set all the images
     */

    private void initImageLoader() {
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.temp_tmp";
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

    class ViewHolder {
        TextView friendsName, friendsStatus, lastSeen, smscount;
        CircleImageView userImage;
    }
}

