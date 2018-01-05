package com.hashmybag.adapters;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hashmybag.ChatImageView;
import com.hashmybag.R;
import com.hashmybag.WebActivity;
import com.hashmybag.beans.ChatingBean;
import com.hashmybag.paytmintegration.PayTM;
import com.hashmybag.servercommunication.WebRequestTask;
import com.hashmybag.servercommunication.WebServiceDetails;
import com.hashmybag.util.Constants;
import com.hashmybag.util.SharedpreferenceUtility;
import com.hashmybag.util.TimeDifferentiation;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * This class is used as a adapter for chatting list and implemented StickyHeaders in this
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */

public class ChattingAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private static LayoutInflater inflater = null;
    Activity context;
    String cust_id, email_id, mobile;
    List<ChatingBean> chatList;
    String friendId;
    TimeDifferentiation td;
    String dateString;
    ImageLoader loader;
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.CREATE_SALES_HISTORY_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    android.util.Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        String message = jsonResponse.optString("mesage");

                        if (status.equalsIgnoreCase("200")) {
                            Toast.makeText(context, "COD recorded successfully", Toast.LENGTH_SHORT).show();
                        } else if (status.equalsIgnoreCase("404")) {
                            Toast.makeText(context, "You need to follow this store", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Payment does not set in server", Toast.LENGTH_SHORT).show();
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.ADD_WHISHLIST_PID:
                    String responseNotifResponse3 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse3);
                    Log.v("response :", "" + responseNotifResponse3);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse3);
                        String status = jsonResponse.optString("status");
                        String message = jsonResponse.optString("message");
                        if (status.equalsIgnoreCase("200")) {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } else if (status.equalsIgnoreCase("400")) {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Error in Adding wishlist!", Toast.LENGTH_LONG).show();
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
            }
        }


    };

    public ChattingAdapter(Activity applicationContext, List<ChatingBean> chatList) {
        this.chatList = chatList;
        this.context = applicationContext;
        cust_id = SharedpreferenceUtility.getInstance(applicationContext).getString(Constants.CUSTOMER_ID);
        email_id = SharedpreferenceUtility.getInstance(applicationContext).getString(Constants.CUSTOMER_EMAIL);
        mobile = SharedpreferenceUtility.getInstance(applicationContext).getString(Constants.CUSTOMER_MOBILE_NUMBER);
        inflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        td = new TimeDifferentiation();
        initImageLoader();

    }

    public static List<String> extractUrls(String input) {
        List<String> result = new ArrayList<String>();

        Pattern pattern = Pattern.compile(
                "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
                        "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
                        "|mil|biz|info|mobi|name|aero|jobs|museum" +
                        "|travel|[a-z]{2}))(:[\\d]{1,5})?" +
                        "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
                        "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
                        "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                        "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
                        "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setWishList(String productId, String user_id) {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("product_id", productId);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(context, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.ADD_WHISHLIST_PID, true, WebServiceDetails.ADD_WHISHLIST_URL + user_id + "/add_to_wishlist").execute();
    }

    @Override
    public long getHeaderId(int position) {
        if (chatList.size() > 0) {
            try {
                String date = chatList.get(position).getMsgDate().split(" ")[0];
                dateString = date;
                date = date.replace('/', '0');
                return Long.parseLong(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Header view implemented for showing chats header i.e today, yesterday or 26/07/2018
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.header_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.headerText);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        String editedDate = formattedDate.replace('-', '/');

        if (chatList.size() > 0) {
            if (chatList.get(position).getProductedCreated() == null) {
                if (chatList.get(position).getMsgDate() == null) {
                    if (editedDate.equals(chatList.get(position).getDate().split(" ")[0])) {
                        String headerText = "Today";
                        holder.text.setText(headerText);
                    } else {
                        String date = chatList.get(position).getMsgDate();
                        String x[] = date.split(" ");
                        String headerText = x[0];
                        holder.text.setText(headerText);
                    }
                }
                if (editedDate.equals(chatList.get(position).getMsgDate().split(" ")[0])) {
                    String headerText = "Today";
                    holder.text.setText(headerText);
                } else {
                    String date = chatList.get(position).getMsgDate();
                    String x[] = date.split(" ");
                    String headerText = x[0];
                    holder.text.setText(headerText);
                }

            } else {
                if (editedDate.equals(chatList.get(position).getProductedCreated().split(" ")[0])) {
                    String headerText = "Today";
                    holder.text.setText(headerText);
                } else {
                    String date = chatList.get(position).getDate();
                    String x[] = date.split(" ");
                    String headerText = x[0];
                    holder.text.setText(headerText);
                }
            }
        }
        return convertView;
    }

    /**
     * setting chat inside view i.e chat box, messages, images
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;
        if (convertView == null) {

            vi = inflater.inflate(R.layout.activity_chat_singlemessage, null);
            holder = new ViewHolder();

            holder.rightMC = (RelativeLayout) vi.findViewById(R.id.rightMC);
            holder.textR = (TextView) vi.findViewById(R.id.textR);
            holder.timeMR = (TextView) vi.findViewById(R.id.timeMR);
            holder.imageViewRC = (RelativeLayout) vi.findViewById(R.id.imageViewRC);
            holder.imageViewR1 = (ImageView) vi.findViewById(R.id.imageViewR1);
            holder.processRight = (ProgressBar) vi.findViewById(R.id.processing_box_right);

            holder.leftMC = (RelativeLayout) vi.findViewById(R.id.leftMC);
            holder.textL = (TextView) vi.findViewById(R.id.textL);
            holder.timeML = (TextView) vi.findViewById(R.id.timeML);
            holder.imageViewLC = (RelativeLayout) vi.findViewById(R.id.imageViewLC);
            holder.imageViewL1 = (ImageView) vi.findViewById(R.id.imageViewL1);
            holder.processLeft = (ProgressBar) vi.findViewById(R.id.processing_box_left);

            holder.payment_option = (LinearLayout) vi.findViewById(R.id.payment_option);
            holder.payment_option_cod = (Button) vi.findViewById(R.id.payment_option_cod);
            holder.payment_option_card = (Button) vi.findViewById(R.id.payment_option_card);
            holder.payment_option_wallet = (Button) vi.findViewById(R.id.payment_option_wallet);

            holder.stream_info = (RelativeLayout) vi.findViewById(R.id.stream_info);
            holder.title = (TextView) vi.findViewById(R.id.title);
            holder.desc = (TextView) vi.findViewById(R.id.desc);
            holder.imageView2 = (ImageView) vi.findViewById(R.id.imageView2);
            holder.date = (TextView) vi.findViewById(R.id.date);
            holder.favourite = (ImageView) vi.findViewById(R.id.favourite);
            holder.link = (TextView) vi.findViewById(R.id.link);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        if (chatList.size() <= 0) {
            holder.link.setText("No Data");
        } else {
            loader = ImageLoader.getInstance();
            ChatingBean chatingBean = chatList.get(position);
            if (cust_id.equalsIgnoreCase(chatingBean.getSenderId())) {

                holder.processRight.setVisibility(View.GONE);
                holder.imageViewR1.setVisibility(View.GONE);
                holder.textR.setVisibility(View.GONE);
                holder.processLeft.setVisibility(View.GONE);
                holder.imageView2.setVisibility(View.GONE);
                holder.desc.setVisibility(View.GONE);
                holder.title.setVisibility(View.GONE);
                holder.rightMC.setVisibility(View.GONE);
                holder.leftMC.setVisibility(View.GONE);
                holder.imageViewRC.setVisibility(View.GONE);
                holder.imageViewLC.setVisibility(View.GONE);
                holder.payment_option.setVisibility(View.GONE);
                holder.favourite.setVisibility(View.GONE);
                holder.link.setVisibility(View.GONE);
                holder.stream_info.setVisibility(View.GONE);
                holder.textL.setVisibility(View.GONE);
                holder.payment_option_cod.setVisibility(View.GONE);
                holder.payment_option_wallet.setVisibility(View.GONE);
                holder.payment_option_card.setVisibility(View.GONE);
                holder.payment_option.setVisibility(View.GONE);

                boolean from_stream = chatingBean.isFromStream();

                if (from_stream) {
                    String title = chatingBean.getTitle();
                    String description = chatingBean.getMessageText();
                    String image_url = chatingBean.getImageUrl();
                    boolean isWishList = chatingBean.getFavorite();
                    String time = chatingBean.getDate();
                    String productId = chatingBean.getProductId();
                    String link = "";
                    holder.stream_info.setVisibility(View.VISIBLE);
                    holder.desc.setVisibility(View.VISIBLE);
                    holder.date.setVisibility(View.VISIBLE);

                    if ((!title.equalsIgnoreCase("null")) && (title != null)) {
                        holder.title.setText(title);
                        holder.title.setVisibility(View.VISIBLE);
                    }
                    if ((!description.equalsIgnoreCase("null")) && (description != null)) {
                        holder.desc.setText(description);
                        holder.desc.setVisibility(View.VISIBLE);
                    }
                    if ((!time.equalsIgnoreCase("null")) && (time != null)) {
                        holder.date.setText(time);
                        holder.date.setVisibility(View.VISIBLE);
                    }

                    if (isTwitter(title)) {
                        final List<String> links = extractUrls(chatingBean.getStreamDesc());
                        if (links.size() > 0) {
                            link = links.get(0);
                            holder.link.setVisibility(View.VISIBLE);
                            holder.link.setText(link);
                        }
                    }
                    if ((!image_url.equalsIgnoreCase("")) && (!image_url.equalsIgnoreCase("null")) && (image_url != null)) {
                        holder.imageView2.setVisibility(View.VISIBLE);
                        holder.imageViewRC.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(image_url)
                                .error(R.mipmap.notification_bag)
                                .into(holder.imageView2, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.processRight.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.processRight.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        holder.imageView2.setVisibility(View.GONE);
                    }

                    holder.imageView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(context, ChatImageView.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.putExtra("image_uri", chatList.get(position).getImageUrl());
                            context.startActivity(intent1);
                        }
                    });

                    if ((productId != null) && (!productId.equalsIgnoreCase("null"))) {
                        if (!(String.valueOf(isWishList).equalsIgnoreCase("null")) && (String.valueOf(isWishList) != null)) {
                            if (isWishList) {
                                holder.favourite.setVisibility(View.VISIBLE);
                                holder.favourite.setImageResource(R.drawable.wishlist_red);
                            } else {
                                holder.favourite.setImageResource(R.drawable.wishlist_gray);
                                holder.favourite.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        holder.favourite.setVisibility(View.GONE);
                    }

                    if (chatingBean.getCodOption()) {
                        holder.payment_option.setVisibility(View.VISIBLE);
                        holder.payment_option_cod.setVisibility(View.VISIBLE);
                    }

                    if (chatingBean.getCardOption()) {
                        holder.payment_option_card.setVisibility(View.VISIBLE);
                        holder.payment_option.setVisibility(View.VISIBLE);
                    }
                    if (chatingBean.getWalletOption()) {
                        holder.payment_option_wallet.setVisibility(View.VISIBLE);
                        holder.payment_option.setVisibility(View.VISIBLE);
                    }


                    holder.favourite.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.favourite.setImageResource(R.drawable.wishlist_red);
                                    ChatingBean chatingBean = chatList.get(position);
                                    chatingBean.setFavorite(true);
                                    setWishList(chatingBean.getProductId(), chatingBean.getFriendId());
                                }
                            }
                    );
                    holder.payment_option_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatingBean chatingBean = chatList.get(position);
                            PayTM payTM = new PayTM(context, chatingBean.getProductPrice(), cust_id, email_id, mobile, chatingBean.getProductId(), chatingBean.getSenderId(), "COD");
                            payTM.onStartTransaction();
                        }
                    });
                    holder.payment_option_cod.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatingBean chatingBean = chatList.get(position);
                            String product_id = chatingBean.getProductId();
                            String price = chatingBean.getProductPrice();
                            String sender_id = chatingBean.getSenderId();
                            Log.v("Price", price + "PID" + product_id);
                            setPaymentHistory(price, product_id, sender_id, cust_id);
                            // Toast.makeText(context,"Cash On Delivery",Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.payment_option_wallet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatingBean chatingBean = chatList.get(position);
                            PayTM payTM = new PayTM(context, chatingBean.getProductPrice(), cust_id, email_id, mobile, chatingBean.getProductId(), chatingBean.getSenderId(), "WALLET");
                            payTM.onStartTransaction();
                            // Toast.makeText(context,"Wallet",Toast.LENGTH_SHORT).show();
                        }
                    });
                    final String finalLink = link;
                    holder.link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, WebActivity.class);
                            intent.putExtra("url", finalLink);
                            context.startActivity(intent);
                        }
                    });


                } else {

                    String text = chatingBean.getMessageText();
                    String image_url = chatingBean.getImageUrl();
                    String time = chatingBean.getDate();
                    holder.rightMC.setVisibility(View.VISIBLE);

                    holder.timeMR.setText(time);

                    if ((text != null) && (!text.equalsIgnoreCase("") && (!text.equalsIgnoreCase("null")))) {
                        holder.textR.setText(chatingBean.getMessageText().trim());
                        holder.textR.setVisibility(View.VISIBLE);
                    }
                    if ((image_url != null) && (!image_url.equalsIgnoreCase("")) && (!image_url.equalsIgnoreCase("null"))) {
                        holder.imageViewR1.setVisibility(View.VISIBLE);
                        holder.processRight.setVisibility(View.VISIBLE);
                        holder.imageViewRC.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(image_url)
                                .error(R.mipmap.notification_bag)
                                .into(holder.imageViewR1, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.processRight.setVisibility(View.GONE);
                                        holder.imageViewR1.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.processRight.setVisibility(View.GONE);
                                    }
                                });
                    }

                    holder.imageViewR1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(context, ChatImageView.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.putExtra("image_uri", chatList.get(position).getImageUrl());
                            context.startActivity(intent1);
                        }
                    });


                }

            } else {
                holder.processRight.setVisibility(View.GONE);
                holder.imageViewR1.setVisibility(View.GONE);
                holder.textR.setVisibility(View.GONE);
                holder.processRight.setVisibility(View.GONE);
                holder.imageView2.setVisibility(View.GONE);
                holder.desc.setVisibility(View.GONE);
                holder.title.setVisibility(View.GONE);
                holder.rightMC.setVisibility(View.GONE);
                holder.leftMC.setVisibility(View.GONE);
                holder.payment_option.setVisibility(View.GONE);
                holder.favourite.setVisibility(View.GONE);
                holder.link.setVisibility(View.GONE);
                holder.desc.setVisibility(View.GONE);
                holder.imageViewRC.setVisibility(View.GONE);
                holder.imageViewLC.setVisibility(View.GONE);
                holder.stream_info.setVisibility(View.GONE);
                holder.date.setVisibility(View.GONE);
                holder.textL.setVisibility(View.GONE);
                holder.payment_option_cod.setVisibility(View.GONE);
                holder.payment_option_wallet.setVisibility(View.GONE);
                holder.payment_option_card.setVisibility(View.GONE);
                holder.payment_option.setVisibility(View.GONE);


                if (chatingBean.isFromStream()) {
                    String title = chatingBean.getTitle();
                    String description = chatingBean.getMessageText();
                    String image_url = chatingBean.getImageUrl();
                    boolean isWishList = chatingBean.getFavorite();
                    String time = chatingBean.getDate();
                    String link = "";
                    String productId = chatingBean.getProductId();
                    holder.stream_info.setVisibility(View.VISIBLE);

                    if (isTwitter(title)) {
                        final List<String> links = extractUrls(chatingBean.getStreamDesc());
                        if (links.size() > 0) {
                            link = links.get(0);
                            holder.link.setVisibility(View.VISIBLE);
                            holder.link.setText(link);
                        }

                    }

                    if ((title != null) && (!title.equalsIgnoreCase("")) && (!title.equalsIgnoreCase("null"))) {
                        holder.title.setText(title);
                        holder.title.setVisibility(View.VISIBLE);
                    }
                    if ((time != null) && (!time.equalsIgnoreCase("")) && (!time.equalsIgnoreCase("null"))) {
                        holder.date.setText(time);
                        holder.date.setVisibility(View.VISIBLE);
                    }
                    if ((description != null) && (!description.equalsIgnoreCase("")) && (!description.equalsIgnoreCase("null"))) {
                        holder.desc.setText(description);
                        holder.desc.setVisibility(View.VISIBLE);
                    }
                    if ((image_url != null) && (!image_url.equalsIgnoreCase("")) && (!image_url.equalsIgnoreCase("null"))) {
                        holder.imageView2.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(image_url)
                                .error(R.mipmap.notification_bag)
                                .into(holder.imageView2, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.processRight.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.processRight.setVisibility(View.GONE);
                                    }
                                });

                        if ((productId != null) && (!productId.equalsIgnoreCase("null"))) {
                            if (!(String.valueOf(isWishList).equalsIgnoreCase("null")) && (String.valueOf(isWishList) != null)) {
                                if (isWishList) {
                                    holder.favourite.setVisibility(View.VISIBLE);
                                    holder.favourite.setImageResource(R.drawable.wishlist_red);
                                } else {
                                    holder.favourite.setImageResource(R.drawable.wishlist_gray);
                                    holder.favourite.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            holder.favourite.setVisibility(View.GONE);
                        }

                        if (chatingBean.getCodOption()) {
                            holder.payment_option_cod.setVisibility(View.VISIBLE);
                            holder.payment_option.setVisibility(View.VISIBLE);
                        } else {
                            holder.payment_option_cod.setVisibility(View.GONE);
                        }
                        if (chatingBean.getCardOption()) {
                            holder.payment_option_card.setVisibility(View.VISIBLE);
                            holder.payment_option.setVisibility(View.VISIBLE);
                        } else {
                            holder.payment_option_card.setVisibility(View.GONE);
                        }
                        if (chatingBean.getWalletOption()) {
                            holder.payment_option_wallet.setVisibility(View.VISIBLE);
                            holder.payment_option.setVisibility(View.VISIBLE);
                        } else {
                            holder.payment_option_wallet.setVisibility(View.GONE);
                        }
                        holder.imageView2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(context, ChatImageView.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent1.putExtra("image_uri", chatList.get(position).getImageUrl());
                                context.startActivity(intent1);
                            }
                        });
                        holder.favourite.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        holder.favourite.setImageResource(R.drawable.wishlist_red);
                                        ChatingBean chatingBean = chatList.get(position);
                                        chatingBean.setFavorite(true);
                                        setWishList(chatingBean.getProductId(), chatingBean.getFriendId());
                                    }
                                }
                        );
                        holder.payment_option_card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChatingBean chatingBean = chatList.get(position);
                                PayTM payTM = new PayTM(context, chatingBean.getProductPrice(), cust_id, email_id, mobile, chatingBean.getProductId(), chatingBean.getSenderId(), "COD");
                                payTM.onStartTransaction();
                            }
                        });
                        holder.payment_option_cod.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChatingBean chatingBean = chatList.get(position);
                                Log.v("", "");
                                setPaymentHistory(chatingBean.getProductPrice(), chatingBean.getProductId(), chatingBean.getSenderId(), cust_id);
                                // Toast.makeText(context,"Cash On Delivery",Toast.LENGTH_SHORT).show();
                            }
                        });
                        holder.payment_option_wallet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChatingBean chatingBean = chatList.get(position);
                                PayTM payTM = new PayTM(context, chatingBean.getProductPrice(), cust_id, email_id, mobile, chatingBean.getProductId(), chatingBean.getSenderId(), "WALLET");
                                payTM.onStartTransaction();
                                // Toast.makeText(context,"Wallet",Toast.LENGTH_SHORT).show();
                            }
                        });
                        final String finalLink = link;
                        holder.link.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, WebActivity.class);
                                intent.putExtra("url", finalLink);
                                context.startActivity(intent);
                            }
                        });

                    }


                } else {

                    String text = chatingBean.getMessageText();
                    String image_url = chatingBean.getImageUrl();
                    String time = chatingBean.getDate();

                    holder.leftMC.setVisibility(View.VISIBLE);
                    holder.timeML.setVisibility(View.VISIBLE);
                    holder.processLeft.setVisibility(View.VISIBLE);
                    holder.timeML.setText(time);

                    if ((text != null) && (!text.equalsIgnoreCase("")) && (!text.equalsIgnoreCase("null"))) {
                        holder.textL.setText(text);
                        holder.textL.setVisibility(View.VISIBLE);
                    }
                    if ((image_url != null) && (!image_url.equalsIgnoreCase("")) && (!image_url.equalsIgnoreCase("null"))) {
                        holder.imageViewLC.setVisibility(View.VISIBLE);
                        holder.imageViewL1.setVisibility(View.VISIBLE);
                        Picasso.with(context)
                                .load(image_url)
                                .error(R.mipmap.notification_bag)
                                .into(holder.imageViewL1, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.processLeft.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.processLeft.setVisibility(View.GONE);
                                    }
                                });

                        holder.imageViewL1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(context, ChatImageView.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent1.putExtra("image_uri", chatList.get(position).getImageUrl());
                                context.startActivity(intent1);
                            }
                        });

                    }


                }
            }

        }


        return vi;
    }

    private boolean isTwitter(String title) {
        boolean isCorrect = false;
        if (title.equals("Social-Twitter")) {
            isCorrect = true;
        } else {
            isCorrect = false;
        }
        return isCorrect;
    }

    public void add(ChatingBean object) {
        chatList.add(object);
        notifyDataSetChanged();

    }

    private void setPaymentHistory(String price, String product_id, String store_id, String customer_id) {

        String json = "";
        try {
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            JSONObject sales_history = new JSONObject();
            sales_history.put("price", price);
            sales_history.put("product_id", product_id);
            sales_history.put("store_id", store_id);
            sales_history.put("customer_id", customer_id);
            sales_history.put("payment_type", "cod");

            jsonObject.put("sales_history", sales_history);

            json = jsonObject.toString();

            System.out.println("Request Para: " + json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(context, json, _handler, Constants.POST_METHOD,
                WebServiceDetails.CREATE_SALES_HISTORY_PID, true, WebServiceDetails.CREATE_SALES_HISTORY).execute();
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

    public static class ViewHolder {
        public TextView textR, timeMR, textL, timeML, title, desc, date, link;
        public RelativeLayout rightMC, imageViewRC, leftMC, imageViewLC, stream_info;

        public ImageView imageViewR1, imageView2, imageViewL1, favourite;
        public LinearLayout payment_option;
        public ProgressBar processLeft, processRight;
        Button payment_option_cod, payment_option_card, payment_option_wallet;
    }

    class HeaderViewHolder {
        TextView text;
    }


}
