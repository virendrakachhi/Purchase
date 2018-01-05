package com.hashmybag.servercommunication;

/**
 * This class is used for handling all the URLS we are using in the entire application
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-08-13
 */

public class WebServiceDetails {
    //PIDs
    public static final int SEND_OTP_PID = 1;
    //http://stage.hashmybag.com/api/v1/
    public static final int OTP_VERIFICATION_PID = 2;
    //http://stage.hashmybag.com/api/v1/users
    public static final int GET_ALL_STORES_PID = 3;
    //http://stage.hashmybag.com/api/v1/users/login
    public static final int FOLLOW_PID = 4;
    //http://stage.hashmybag.com/api/v1/stores
    public static final int UNFOLLOW_PID = 5;
    //http://stage.hashmybag.com/api/v1/stores/search
    public static final int SEARCH_STORES_BY_TITLE_PID = 6;
    //http://stage.hashmybag.com/api/v1/stores/search
    public static final int SIMILAR_STORES_PID = 7;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int GET_PROFILE_INFO_PID = 8;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int GET_SUBSCRIBED_LIST_PID = 9;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int GET_WHISHLIST_PID = 10;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int GET_WHISHLIST_REMOVE_PID = 11;
    public static final int UPDATE_PROFILE_PID = 12;
    //http://stage.hashmybag.com/api/v1/customers/get_streams
    public static final int GET_STORE_INFO_PID = 13;
    //http://stage.hashmybag.com/api/v1/stores/
    public static final int CHANGE_TOGGLE_ENABLE_PID = 15;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int CHANGE_TOGGLE_DISABLE_PID = 16;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int GET_SALES_HISTORY_PID = 17;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int CREATE_SALES_HISTORY_PID = 18;
    public static final int CHECKING_MYBAG_FRIEND_PID = 19;
    //http://stage.hashmybag.com/api/v1/sales_histories
    public static final int GET_STREAM_PID = 20;
    //http://stage.hashmybag.com/api/v1/friends/check_friends
    public static final int GET_COMMUNICATION_PID = 21;
    //http://stage.hashmybag.com/api/v1/customers/get_streams
    public static final int CREATE_CHAT_PID = 22;
    public static final int CREATE_COMMUNICATION_PID = 23;
    //http://stage.hashmybag.com/api/v1/customers/get_streams
    public static final int CHAT_HISTORY_PID = 24;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int GET_PAYTM_BALANCE_PID = 25;
    //http://stage.hashmybag.com/api/v1/pusher/auth
    public static final int DELETE_STREAM_PID = 26;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int GCM_URL_PID = 27;
    //http://stage.hashmybag.com/api/v1/customers/
    public static final int UPLOAD_IMAGE_IN_CLOUD = 28;
    //http://stage.hashmybag.com/api/v1/streams/:id/seen
    public static final int WHISHLIST_OFFERS_PID = 29;
    //http://stage.hashmybag.com/api/v1/device_info
    public static final int ADD_WHISHLIST_PID = 30;
    //http://stage.hashmybag.com/api/v1/device_info
    //Base url
    public static String BASE_URL = "http://api.hashmybag.com/api/v1/";
    //http://stage.hashmybag.com/api/v1/get_wishlist_update
    //Register URL
    public static String SEND_OTP = BASE_URL + "users";
    //http://stage.hashmybag.com/api/v1/get_communications
    //Login URL
    public static String OTP_VERIFICATION = BASE_URL + "users/login";
    //Geting All store
    public static String GETALL_STORES = BASE_URL + "stores";
    //Get Similar Stores
    public static String SIMILAR_STORES = BASE_URL + "stores/search";
    //Search stores by title
    public static String SEARCH_STORES_BY_TITLE = BASE_URL + "stores/search";
    //Set Follow
    public static String FOLLOW_URL = BASE_URL + "customers/";
    //Set Unfollow
    public static String UNFOLLOW_URL = BASE_URL + "customers/";
    //Get Subscribed list
    public static String GET_SUBSCRIBED_LIST = BASE_URL + "customers/";
    //Get Wishlist
    public static String GET_WHISHLIST_URL = BASE_URL + "customers/";
    //Remove WishList
    public static String GET_WHISHLIST_REMOVE_URL = BASE_URL + "customers/";
    //update profile
    public static String UPDATE_PROFILE = BASE_URL + "customers/";
    //get any store info
    public static String GET_STORE_INFO = BASE_URL + "stores/";
    //get profile of customer
    public static String GET_PROFILE_INFO = BASE_URL + "customers/";
    //set enable
    public static String CHANGE_TOGGLE_ENABLE = BASE_URL + "customers/";
    //set disable
    public static String CHANGE_TOGGLE_DISABLE = BASE_URL + "customers/";
    //get payment history
    public static String GET_SALES_HISTORY = BASE_URL + "purchased_items";
    //http://stage.hashmybag.com/api/v1/sales_histories
    //http://stage.hashmybag.com/api/v1/purchased_items
    // set payment
    public static String CREATE_SALES_HISTORY = BASE_URL + "sales_histories";
    //checking contact list
    public static String CHECKING_MYBAG_FRIEND = BASE_URL + "friends/check_friends";
    //get All stream
    public static String GET_STREAM = BASE_URL + "customers/get_streams";
    //Add Wishlist
    public static String ADD_WHISHLIST_URL = BASE_URL + "customers/";
    //http://stage.hashmybag.com/api/v1/customers/
    //get All stream
    public static String CREATE_CHAT = BASE_URL + "communications/";
    //Add Wishlist
    public static String CREATE_COMMUNICATION = BASE_URL + "communications";
    public static String PUSHAR_AUTHORIZATION = BASE_URL + "pusher/auth";
    public static String CHAT_HISTORY = BASE_URL + "communications/";
    public static String GET_PAYTM_BALANCE = "https://pguat.paytm.com/oltp/HANDLER_INTERNAL/checkBalance";
    public static String DELETE_STREAM = BASE_URL + "streams/";
    public static String GCM_URL = BASE_URL + "device_info";
    public static String UPDATE_COORDINATES = BASE_URL + "users/";
    public static String WHISHLIST_OFFERS = BASE_URL + "get_wishlist_update";
    public static String GET_COMMUNICATION = BASE_URL + "get_communications";


}