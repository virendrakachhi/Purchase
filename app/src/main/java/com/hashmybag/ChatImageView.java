package com.hashmybag;

/**
 * This class is handling all images coming in the chatting
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ChatImageView extends AppCompatActivity {

    ImageView imageView, backButton;

    boolean isImageFitToScreen;
    ImageLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ChatImageView.this.setTheme(R.style.NextTheme);
        setContentView(R.layout.activity_chat_image_view);


        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("image_uri");

        initImageLoader();

        imageView = (ImageView) findViewById(R.id.imageViewChat);
        backButton = (ImageView) findViewById(R.id.backButton);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.with(getApplicationContext())
                .load(imageUri)
                .into(imageView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Initialising image loader for setting up all the images in chatting layout
     */

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
}

