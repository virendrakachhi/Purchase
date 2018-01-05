package com.hashmybag.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.R;
import com.hashmybag.adapters.WishlistAdapter;
import com.hashmybag.beans.WishlistBean;
import com.hashmybag.servercommunication.ConnectionDetector;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is used for handling wishlist *
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-22
 */

public class MyWishlistFragment extends Fragment {

    public int wishlistSize;
    FragmentManager fragmentManager;
    RelativeLayout headerLayout;
    ImageView back_arrow;
    FragmentTransaction fragmentTransaction;
    TabScreen tabScreen;
    ImageLoader loader;
    ImageView itemsImage;
    TextView itemDetail, itemAddDate, emptyView, no_item;
    ListView listView;
    ConnectionDetector cd;
    WishlistAdapter wishlistAdapter;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.GET_WHISHLIST_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    ArrayList<WishlistBean> wishList = new ArrayList<>();
                    wishList.clear();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {

                            final String message = jsonResponse.optString("message");
                            JSONObject data = jsonResponse.getJSONObject("data");
                            JSONArray products = data.getJSONArray("products");

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject product = products.getJSONObject(i);
                                WishlistBean bean = new WishlistBean();
                                String product_id = product.optString("id");
                                String product_code = product.optString("code");
                                String product_name = product.optString("name");
                                String product_description = product.optString("description");
                                String product_image_url = product.optString("image_url");
                                String product_price = product.optString("price");
                                String product_currency_type = product.optString("currency_type");
                                String product_created_at = product.optString("created_at");
                                String product_updated_at = product.optString("updated_at");
                                String product_communication_id = product.optString("communication_id");

                                JSONObject store = product.getJSONObject("store");
                                String store_id = store.optString("id");
                                String store_name = store.optString("name");
                                String store_email = store.optString("email");
                                String store_image = store.optString("image");
                                String store_latitude = store.optString("latitude");
                                String store_longitude = store.optString("longitude");

                                bean.setStoreId(store_id);
                                bean.setStoreTitle(store_name);
                                bean.setStorePhoto(store_image);
                                bean.setCommunicationId(product_communication_id);
                                bean.setStreamTime(product_created_at);
                                bean.setStreamType("product");

                                bean.setProductId(product_id);
                                bean.setProductCode(product_code);
                                bean.setProductName(product_name);
                                bean.setProductDescription(product_description);
                                bean.setProductImage(product_image_url);
                                bean.setProductPrice(product_price);
                                bean.setProductCreateAt(product_created_at);
                                bean.setProductUpdate(product_updated_at);
                                bean.setCurrencyType(product_currency_type);
                                bean.setProductStoreId(store_id);
                                wishList.add(bean);
                            }
                            wishlistAdapter = new WishlistAdapter(wishList, getContext(), no_item);
                            listView.setAdapter(wishlistAdapter);
                            wishlistSize = wishList.size();


                        }
                    } catch (JSONException e) {

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        Toast.makeText(getActivity(), "No subscibed stores available", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
        }
    };

    public static void hideKeyboard(Context ctx) {
        try {
            InputMethodManager inputManager = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            /*check if no view has focus*/

            View v = ((Activity) ctx).getCurrentFocus();
            if (v == null)
                return;

            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_wishlist_layout, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        tabScreen = new TabScreen();
        hideKeyboard(getActivity());

        cd = new ConnectionDetector(getContext());

        Boolean aBoolean = isInternetConnected();
        if (aBoolean != true) {

        } else {
            getWishlist();

        }
        loader = ImageLoader.getInstance();
        initImageLoader();

        itemsImage = (ImageView) view.findViewById(R.id.item_image);
        itemDetail = (TextView) view.findViewById(R.id.item_detail);
        no_item = (TextView) view.findViewById(R.id.no_item);

        itemAddDate = (TextView) view.findViewById(R.id.date1);
        listView = (ListView) view.findViewById(R.id.wishlist_view);
        emptyView = (TextView) view.findViewById(R.id.empltyView);

        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.GONE);

        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, tabScreen);
                fragmentTransaction.commit();
            }
        });


        listView.setEmptyView(emptyView);

        return view;
    }

    /*ImageLoader for setting all images */

    public Boolean isInternetConnected() {

                /*Check, If internet is connected or not!*/
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

            return true;
        } else {
            Toast.makeText(getContext(), "No Network Found!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /* Web service request for gettingi Wishlist */

    private void initImageLoader() {
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(getActivity(), CACHE_DIR);
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(getActivity())
                    .defaultDisplayImageOptions(defaultOptions).discCache(new UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());
            ImageLoaderConfiguration config = builder.build();
            loader.init(config);
        } catch (Exception e) {
        }
    }

    /* Handler to handle JSON response for getting Whishlist */

    private void getWishlist() {
        String id = SharedpreferenceUtility.getInstance(getContext()).getString(Constants.CUSTOMER_ID);
        new WebRequestTask(getActivity(), _handler, Constants.GET_METHOD, WebServiceDetails.GET_WHISHLIST_PID, true, WebServiceDetails.GET_WHISHLIST_URL + id + "/wishlists").execute();

    }

}
