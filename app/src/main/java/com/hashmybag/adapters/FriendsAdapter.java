package com.hashmybag.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hashmybag.R;
import com.hashmybag.beans.MyFriendsBean;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is used for setting BaseAdapter for the friend list*
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-25
 */
public class FriendsAdapter extends BaseAdapter {

    Activity activity;
    List<MyFriendsBean> arrayList;
    ImageLoader loader;
    TextView no_item;

    public FriendsAdapter(Activity context, List<MyFriendsBean> arrayList, TextView no_item) {
        this.activity = context;
        this.arrayList = arrayList;
        this.no_item = no_item;
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
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Setting view for friends list layout
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */


    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        //if (convertView == null) {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.friends_item, null);
        holder = new ViewHolder();

        holder.userImage = (CircleImageView) convertView.findViewById(R.id.friends_image);
        holder.friendsName = (TextView) convertView.findViewById(R.id.friends_name);

        holder.friendsName.setText(arrayList.get(position).getTitle());
        loader = ImageLoader.getInstance();

        loader.displayImage(arrayList.get(position).getPhoto(), holder.userImage,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.userImage.setImageResource(R.drawable.user);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

        //}
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

    private class ViewHolder {
        TextView friendsName;
        CircleImageView userImage;
    }


}

