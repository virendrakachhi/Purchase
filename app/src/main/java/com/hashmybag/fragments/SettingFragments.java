package com.hashmybag.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.hashmybag.R;
import com.hashmybag.cloudinaryintegration.CloudinaryImageUploader;
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
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This class is used for maintaining user informations/settings *
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-22
 */

public class SettingFragments extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;
    CircleImageView user_image, user_image1;
    ImageView back_arrow, editUser;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TabScreen tabScreen;
    RelativeLayout headerLayout;
    EditText nameUser, contactNo, addressUser, emailID;
    ImageLoader loader;
    Context context;
    TextView user_name, user_location;
    String imagePath, user_id, finalImage, name, email_new, mob, add, imageUrl;
    Cloudinary cloudinary;
    Button save_Button;
    String outMessage;
    private Bitmap yourSelectedImage;
    private Uri fileUri;
    private String message;

    /* Email validation */
    Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WebServiceDetails.UPDATE_PROFILE_PID:
                    String responseNotifResponse = (String) msg.obj;
                    System.out.println("" + responseNotifResponse);
                    Log.v("response :", "" + responseNotifResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseNotifResponse);

                        String status = jsonResponse.optString("status");
                        if (status.equalsIgnoreCase("200")) {
                            message = jsonResponse.optString("message");
                            JSONObject user = jsonResponse.getJSONObject("data");
                            JSONObject jsonResponse1 = user.getJSONObject("customer");
                            String id = jsonResponse1.optString("id");
                            String email = jsonResponse1.optString("email");
                            String username = jsonResponse1.optString("username");
                            String authentication_token = jsonResponse1.optString("authentication_token");
                            String sign_up_stream = jsonResponse1.optString("sign_up_stream");
                            String latitude = jsonResponse1.optString("latitude");
                            String longitude = jsonResponse1.optString("longitude");
                            String address1 = jsonResponse1.optString("address1");
                            String location = jsonResponse1.optString("location");
                            String f_name = jsonResponse1.optString("first_name");
                            String l_name = jsonResponse1.optString("last_name");
                            String title = jsonResponse1.optString("title");
                            String description = jsonResponse1.optString("description");
                            String mobile_number = jsonResponse1.optString("mobile_number");
                            String landline_number = jsonResponse1.optString("landline_number");
                            String active = jsonResponse1.optString("active");
                            String photo = jsonResponse1.optString("photo");

                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_ID, id);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_EMAIL, email);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_USERNAME, username);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_AUTHENTICATION_TOKEN, authentication_token);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_SIGNUP_STREAM, sign_up_stream);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_ADDRESS1, address1);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_LOCATION, location);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_F_NAME, f_name);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_L_NAME, l_name);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_TITLE, title);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_DESCRIPTION, description);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_MOBILE_NUMBER, mobile_number);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_LANDLINE, landline_number);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_ACTIVE, active);
                            SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_PHOTO, photo);
                            SharedpreferenceUtility.getInstance(getActivity()).putBoolean(Constants.CUSTOMER_LOGIN_OR_NOT, true);


                            nameUser.setText(title);
                            emailID.setText(email);
                            contactNo.setText(mobile_number);
                            if ((!address1.equalsIgnoreCase("")) && (address1 != null) && (!address1.equalsIgnoreCase("null"))) {
                                addressUser.setText(address1);
                                user_location.setText(address1);
                            } else {
                                addressUser.setText(location);
                                user_location.setText(location);
                            }
                            user_name.setText(title);
                            loader.displayImage(photo, user_image1,
                                    new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String imageUri, View view) {
                                            super.onLoadingStarted(imageUri, view);
                                        }
                                    });


                            Toast.makeText(getContext(), outMessage, Toast.LENGTH_LONG).show();

                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Problem occured in Profile Updating!", Toast.LENGTH_LONG).show();

                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case WebServiceDetails.UPLOAD_IMAGE_IN_CLOUD:
                    String responseNotifResponse123 = (String) msg.obj;
                    System.out.println("" + responseNotifResponse123);
                    Log.v("response :", "" + responseNotifResponse123);
                    try {
                        JSONObject jsonObject = new JSONObject(responseNotifResponse123);
                        String imageUrlc = jsonObject.getString("image_url");
                        imageUrl = imageUrlc;
                        SharedpreferenceUtility.getInstance(getActivity()).putString(Constants.CUSTOMER_PHOTO, imageUrl);
                        outMessage = "Image Updated Successfully";
                        updateProfile(email_new, add, name, mob, imageUrl);
                        nameUser.setEnabled(false);
                        contactNo.setEnabled(false);
                        emailID.setEnabled(false);
                        addressUser.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Boolean isSDPresent;
    private File myfileNew;

    /*Web service call for updating profile*/
    private String root = Environment.getExternalStorageDirectory().toString() + "/HashMyBag";

    /*Image loader for showing all images*/
    private File myDir = new File(root);

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isName(CharSequence target) {
        String pattern = "([a-z A-Z ']+\\s?)+[a-z A-Z ']+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(target.toString());
        if (matcher.matches())
            return true;
        else
            return false;
    }

    /*Handler to handle JSON response from update profile API*/

    public final static boolean isAddress(CharSequence target) {
        String pattern = "([a-z , 0-9  A-Z]+\\s?)+[a-z , 0-9  A-Z]+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(target.toString());
        if (matcher.matches())
            return true;
        else
            return false;
    }

    /**
     * Function to hide keyboard on click outside the board
     *
     * @param ctx
     */

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

    /*Managing options for uploading images(From gallery or camera)*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setting_layout, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        tabScreen = new TabScreen();
        initImageLoader();

        headerLayout = (RelativeLayout) getActivity().findViewById(R.id.header_layout);
        headerLayout.setVisibility(View.GONE);
        context = getActivity();
        back_arrow = (ImageView) view.findViewById(R.id.back_arrow);
        editUser = (ImageView) view.findViewById(R.id.editUser);

        String photo = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_PHOTO);
        final String f_name = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_F_NAME);
        final String l_name = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_L_NAME);
        final String email = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_EMAIL);
        final String location = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_LOCATION);
        final String address1 = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_ADDRESS1);
        final String mobile = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_MOBILE_NUMBER);
        user_id = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_ID);
        final String title = SharedpreferenceUtility.getInstance(getActivity()).getString(Constants.CUSTOMER_TITLE);

        nameUser = (EditText) view.findViewById(R.id.nameUser);
        emailID = (EditText) view.findViewById(R.id.emailID);

        addressUser = (EditText) view.findViewById(R.id.addressUser);
        contactNo = (EditText) view.findViewById(R.id.contactNo);
        editUser = (ImageView) view.findViewById(R.id.editUser);
        user_image = (CircleImageView) view.findViewById(R.id.user_image);
        save_Button = (Button) view.findViewById(R.id.save_button);

        user_image1 = (CircleImageView) getActivity().findViewById(R.id.user_image1);
        user_name = (TextView) getActivity().findViewById(R.id.user_name);
        user_location = (TextView) getActivity().findViewById(R.id.user_location);

        if (title != null) {
            nameUser.setText(title);
            nameUser.setSelection(nameUser.getText().length());
            user_name.setText(title);

        } else {
            nameUser.setText("No Name");
            user_name.setText("No Name");
        }
        if (email != null) {
            emailID.setText(email);
            emailID.setSelection(emailID.getText().length());

            emailID.setHint("Email Id");
        }
        if (mobile != null) {
            contactNo.setText(mobile);
            contactNo.setSelection(contactNo.getText().length());

        }

        if (!address1.equalsIgnoreCase("") && address1 != null && address1.equalsIgnoreCase(null)) {
            addressUser.setText(address1);
            addressUser.setSelection(addressUser.getText().length());
            user_location.setText(address1);
        } else {
            addressUser.setText(location);
            addressUser.setSelection(addressUser.getText().length());
            user_location.setText(location);
        }

       /* if (location != null) {
            addressUser.setText(location);
            addressUser.setSelection(addressUser.getText().length());
            user_location.setText(location);
        } else {
            addressUser.setText("No Address");
            user_location.setText("No Address");
        }*/

        hideKeyboard(getActivity());

        Map config = new HashMap();
        config.put(Constants.CLOUD_NAME, Constants.CLOUD_NAME_VALUE);
        config.put(Constants.API_KEY, Constants.API_KEY_VALUE);
        config.put(Constants.API_SECRET, Constants.API_SECRET_VALUE);

        cloudinary = new Cloudinary(Constants.CLOUDINARY_URL);

        loader.displayImage(photo, user_image,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                    }
                });
        loader.displayImage(photo, user_image1,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                    }
                });


        back_arrow.setOnClickListener(this);
        user_image.setOnClickListener(this);
        save_Button.setOnClickListener(this);
        editUser.setOnClickListener(this);


        return view;
    }

    private void updateProfile(String email, String address, String title, String mobile, String imageUrl) {

        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
           /* Location location1 = new LocationTracker(getActivity()).getLocation();
            if (location1 != null) {
                double latitude = location1.getLatitude();
                double longitude = location1.getLongitude();
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
            }*/
            jsonObject.put("address1", address);
            jsonObject.put("first_name", title);
            jsonObject.put("title", title);
            jsonObject.put("mobile_number", mobile);
            jsonObject.put("photo", imageUrl);

            JSONObject customer_json = new JSONObject();
            customer_json.put("customer", jsonObject);
            json = customer_json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WebRequestTask(getActivity(), json, _handler, Constants.PATCH_METHOD,
                WebServiceDetails.UPDATE_PROFILE_PID, true, WebServiceDetails.UPDATE_PROFILE + user_id).execute();
    }

    private void initImageLoader() {
        try {
            String CACHE_DIR =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();
            File cacheDir = StorageUtils.getOwnCacheDirectory(getActivity(), CACHE_DIR);

            DisplayImageOptions defaultOptions = new
                    DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new
                    ImageLoaderConfiguration.Builder(getActivity())
                    .defaultDisplayImageOptions(defaultOptions).discCache(new
                            UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());
            ImageLoaderConfiguration config = builder.build();
            loader = ImageLoader.getInstance();
            loader.init(config);

        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editUser:

                nameUser.requestFocus();
                nameUser.setEnabled(true);
                contactNo.setEnabled(true);
                emailID.setEnabled(true);
                addressUser.setEnabled(true);
                save_Button.setEnabled(true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(nameUser, InputMethodManager.SHOW_IMPLICIT);

                break;

            case R.id.back_arrow:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame, tabScreen);
                fragmentTransaction.commit();
                break;

            case R.id.user_image:
                selectImage();
                break;

            case R.id.save_button:

                name = nameUser.getText().toString();
                email_new = emailID.getText().toString();
                add = addressUser.getText().toString();
                mob = contactNo.getText().toString();

                if (name.equals("") || add.equals("") || mob.equals("") || email_new.equals("")) {
                    if (name.equals("")) {
                        nameUser.setError("Name required");
                    }
                    if (add.equals("")) {
                        addressUser.setError("Address required");
                    }
                    if (mob.equals("")) {
                        contactNo.setError("Contact no. required");
                    }
                    if (email_new.equals("")) {
                        emailID.setError("Email id required");
                    }
                } else if (isValidMobile(mob) && isAddress(add) && isName(name) && isValidEmail(email_new)) {
                    imageUrl = SharedpreferenceUtility.getInstance(context).getString(Constants.CUSTOMER_PHOTO);
                    outMessage = "Profile Updated Successfully";
                    updateProfile(email_new, add.trim(), name.trim(), mob, imageUrl);
                    nameUser.setEnabled(false);
                    contactNo.setEnabled(false);
                    emailID.setEnabled(false);
                    addressUser.setEnabled(false);
                    save_Button.setEnabled(false);
                } else {
                    if (!isValidMobile(mob)) {
                        Toast.makeText(getContext(), "Please enter valid number", Toast.LENGTH_SHORT).show();
                    }
                    if (!isAddress(add)) {
                        Toast.makeText(getContext(), "Please enter valid address", Toast.LENGTH_SHORT).show();
                    }
                    if (!isName(name)) {
                        Toast.makeText(getContext(), "Please enter valid Name", Toast.LENGTH_SHORT).show();
                    }
                    if (!isValidEmail(email_new)) {
                        Toast.makeText(getContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();

                    }
                }

                /*Saving edited user details and calling method for web service call*/

                break;

        }

    }

    private boolean isValidMobile(String phone2) {
        boolean check = false;

        if (phone2.length() != 10) {
            check = false;
            // contactNo.setError("Not Valid Number");
        } else {
            check = true;
        }

        return check;
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new
                android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    try {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        fileUri = getActivity().getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                        startActivityForResult(intent, REQUEST_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "You have denied permission for opening Camera", Toast.LENGTH_LONG).show();
                    }

                } else if (items[item].equals("Choose from Gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    yourSelectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
                    yourSelectedImage = Bitmap.createScaledBitmap(yourSelectedImage, yourSelectedImage.getWidth() / 4, yourSelectedImage.getHeight() / 4, true);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    imagePath = saveImage(yourSelectedImage, timeStamp + ".jpg");
                    user_image.setImageBitmap(yourSelectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new CloudinaryImageUploader(getActivity(), WebServiceDetails.UPLOAD_IMAGE_IN_CLOUD, _handler, imagePath, true, user_id).execute();


            } else if (requestCode == SELECT_FILE) {

                Uri selectedImage = data.getData();
                imagePath = getPath(selectedImage);
                decodeFile(imagePath);
                new CloudinaryImageUploader(getActivity(), WebServiceDetails.UPLOAD_IMAGE_IN_CLOUD, _handler, imagePath, true, user_id).execute();

            }

        }

    }

    public String saveImage(Bitmap finalBitmap, String fileName) {
        isSDPresent = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            myfileNew = new File(myDir, fileName);
            if (myfileNew.exists()) myfileNew.delete();

            try {
                FileOutputStream out = new FileOutputStream(myfileNew);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (myfileNew.exists())
                myfileNew.delete();
            try {
                FileOutputStream fos = new FileOutputStream(myfileNew);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MediaScannerConnection.scanFile(getActivity(),
                new String[]{myfileNew.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("imageeee", "file " + path
                                + " was scanned seccessfully: " + uri);
                    }
                });

        return myfileNew.toString();
    }


    /*Getting image path to upload it to Cloudinary*/

    private String getPath(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    /*Handling image ratio*/

    public void decodeFile(String filePath) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        final int REQUIRED_SIZE = 1024;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp <
                    REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        yourSelectedImage = BitmapFactory.decodeFile(filePath, o2);


        user_image.setImageBitmap(yourSelectedImage);

    }
}

