package com.hashmybag.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hashmybag.R;
import com.hashmybag.beans.PaymentBean;
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


/**
 * This class is used for setting BaseAdapter for the payment/sales history list*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-25
 */

public class PaymentAdapter extends BaseAdapter {

    ArrayList<PaymentBean> arrayList = new ArrayList<>();
    Context context;
    ImageLoader loader;

    public PaymentAdapter(ArrayList<PaymentBean> list, Activity a) {
        initImageLoader();

        arrayList = list;
        context = a;
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
     * Setting layout view for payments fragment
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.payment_item, null);

        final ImageView userImage = (ImageView) convertView.findViewById(R.id.obj_image);
        TextView objName = (TextView) convertView.findViewById(R.id.obj_name);
        TextView objDate = (TextView) convertView.findViewById(R.id.obj_date);
        TextView objFrom = (TextView) convertView.findViewById(R.id.obj_from);
        TextView objPrice = (TextView) convertView.findViewById(R.id.obj_price);

        objName.setText(arrayList.get(position).getObj_name());
        objDate.setText(arrayList.get(position).getObj_date());
        objFrom.setText(arrayList.get(position).getObj_from());
        objPrice.setText(arrayList.get(position).getObj_price());

        loader = ImageLoader.getInstance();
        loader.displayImage(arrayList.get(position).getObj_image(), userImage,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        userImage.setImageResource(R.mipmap.notification_bag);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

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
