package com.hashmybag.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.hashmybag.R;
import com.hashmybag.StoreDetail;
import com.hashmybag.beans.AllStoreBean;
import com.hashmybag.servercommunication.ConnectionDetector;
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
 * This class is used for setting BaseAdapter to load images in the lists*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-25
 */

public class ImageAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<AllStoreBean> arrayList;
    ImageLoader loader;
    ConnectionDetector cd;
    private LayoutInflater inflater;

    public ImageAdapter(Activity activity, ArrayList<AllStoreBean> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        initImageLoader();
        cd = new ConnectionDetector(activity);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.image_row, null);

            final CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.photo);
            loader = ImageLoader.getInstance();

            loader.displayImage(arrayList.get(position).getStore_photo(), imageView,
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            imageView.setImageResource(R.mipmap.notification_bag);
                            super.onLoadingStarted(imageUri, view);
                        }
                    });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Boolean isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        Intent intent = new Intent(activity, StoreDetail.class);
                        intent.putExtra("id", arrayList.get(position).getStore_id());
                        activity.startActivity(intent);
                    } else {
                        Toast.makeText(activity, "You don't have internet connection!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return convertView;
    }

    /**
     * ImageLoader: used to set all the images
     */

    private void initImageLoader() {
        try {
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
