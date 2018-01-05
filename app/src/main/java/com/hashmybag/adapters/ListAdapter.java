package com.hashmybag.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hashmybag.R;
import com.hashmybag.beans.AllStoreBean;
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
 * This class is used as a adapter for listing rows n columns for store.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */


public class ListAdapter extends BaseAdapter {
    Activity activity;
    ArrayList<AllStoreBean> arrayList;
    ImageLoader loader;
    private LayoutInflater inflater;

    public ListAdapter(Activity activity, ArrayList<AllStoreBean> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        initImageLoader();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_similar_store, null);

            TextView title = (TextView) convertView.findViewById(R.id.store_name);
            TextView address = (TextView) convertView.findViewById(R.id.store_address);

            final CircleImageView imageView = (CircleImageView) convertView.findViewById(R.id.store_pic);
            // getting movie data for the row
            AllStoreBean m = arrayList.get(position);

            title.setText(m.getStore_title());
            address.setText(m.getStore_address1());


            //  imageView.setImageResource(arrayList.get(position).getStror_photo());
            loader.displayImage(arrayList.get(position).getStore_photo(), imageView,
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            imageView.setImageResource(R.mipmap.notification_bag);
                            super.onLoadingStarted(imageUri, view);
                        }
                    });

        }

        return convertView;
    }

    /**
     * Image loader : Method used to save all the images in the list
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
