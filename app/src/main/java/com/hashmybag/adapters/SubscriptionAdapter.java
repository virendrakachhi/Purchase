package com.hashmybag.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hashmybag.R;
import com.hashmybag.StoreDetail;
import com.hashmybag.beans.SubscriptionBean;
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
 * This class is used for setting BaseAdapter for the subscribed store list*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-05-19
 */

public class SubscriptionAdapter extends BaseAdapter {

    ArrayList<SubscriptionBean> arrayList = new ArrayList<>();
    Context context;
    ImageLoader loader;

    public SubscriptionAdapter(ArrayList<SubscriptionBean> list, Context a) {

        arrayList = list;
        context = a;
        initImageLoader();
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
     * Creating layout view for subscription list and setting
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //if (convertView == null) {
        convertView = LayoutInflater.from(context).inflate(R.layout.subscription_item, null);

        final CircleImageView userImage = (CircleImageView) convertView.findViewById(R.id.obj_image);
        TextView objName = (TextView) convertView.findViewById(R.id.obj_name);
        TextView objLocation = (TextView) convertView.findViewById(R.id.obj_location);


        objName.setText(arrayList.get(position).getStoreName());
        objLocation.setText(arrayList.get(position).getStoreAddress());

        loader = ImageLoader.getInstance();

        loader.displayImage(arrayList.get(position).getStoreImage(), userImage,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        userImage.setImageResource(R.mipmap.notification_bag);
                        super.onLoadingStarted(imageUri, view);
                    }
                });
        //   }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StoreDetail.class);
                intent.putExtra("id", arrayList.get(position).getStoreId());
                context.startActivity(intent);
            }
        });
        return convertView;
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