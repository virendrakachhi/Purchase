package com.hashmybag.databasehandle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hashmybag.beans.CommunicationIdbean;
import com.hashmybag.beans.InboxBean;
import com.hashmybag.beans.MyFriendsBean;
import com.hashmybag.beans.UnSeenBean;
import com.hashmybag.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for handling local database for implementing chat, lastseen ,
 * history and many more things which are mention accordingly
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-04
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    /*query=create table BagContact (friendId integer,FriendMobile text,,FriendTitle text,,FriendEmail text,
        FriendLatitude text,FriendLongitude text,FriendName text,FriendPhoto text) */
    public static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + Constants.TABLE_CONTACTS + "("
            + Constants.FRIEND_ID + " TEXT," + Constants.FRIEND_MOBILE + " TEXT,"
            + Constants.FRIEND_TITLE + " TEXT," + Constants.FRIEND_EMAIL + " TEXT,"
            + Constants.FRIEND_LATITUDE + " TEXT," + Constants.FRIEND_LONGITUDE + " TEXT,"
            + Constants.FRIEND_NAME + " TEXT," + Constants.FRIEND_PHOTO + " TEXT," + Constants.FRIEND_STATUS + " TEXT,"
            + Constants.FRIEND_COMMID + " TEXT )";
    public static final String CREATE_CHAT_TABLE = "CREATE TABLE " + Constants.TABLE_CHAT + "(" + Constants._ID + " TEXT ," + Constants.FRIEND_ID + " TEXT ,"
            + Constants.CUSTOMER_USER + " TEXT," + Constants.COMMUNICATION_ID + " TEXT,"
            + Constants.BODYTYPE + " TEXT," + Constants.BODY + " TEXT," + Constants.DATE + " TEXT)";

    //query=create table ChatTable (SendUser integer Primary Key,RecieveUser text,Body text,Date text)
    public static final String CREATE_STREAM_TABLE = "CREATE TABLE " + Constants.TABLE_STREAM + "(" + Constants.FRIEND_ID + " TEXT ,"
            + Constants.CUSTOMER_USER + " TEXT," + Constants.COD_OPTION + " TEXT," + Constants.BUY_OPTION + " TEXT," +
            Constants.WALLET_OPTION + " TEXT," + Constants.PRODUCT_ID + " TEXT,"
            + Constants.FAVOTITE + " TEXT," + Constants.PRODUCT_CODE + " TEXT,"
            + Constants.DESC + " TEXT," + Constants.IMAGE_URL + " TEXT,"
            + Constants.PRICE + " TEXT," + Constants.CREATE_DATE + " TEXT,"
            + Constants.CURRENCY + " TEXT," + Constants.STORE_ID + " TEXT,"
            + Constants.BODYTYPE + " TEXT," + Constants.DATE + " TEXT)";
    /**
     * Creating tables in DataBase;
     * Inbox table, commid table and unsent(chat append) text table
     */

    /*query=create table ChatTable (SendUser integer Primary Key,RecieveUser text,Body text,Date text) */
    public static final String CREATE_INBOX_TABLE = "CREATE TABLE " + Constants.TABLE_INBOX + "("
            + Constants.FRIEND_IDI + " TEXT ," + Constants.PHOTO + " TEXT,"
            + Constants.FRIEND_NAMEI + " TEXT," + Constants.COMMUNICATION_IDI + " TEXT PRIMARY KEY," +
            Constants.LAST_SMS + " TEXT," + Constants.SMSTIME + " DATE," + Constants.INDICATER + " TEXT," + Constants.SMSCOUNT + " TEXT)";
    public static final String CREATE_COMMID_TABLE = "CREATE TABLE " + Constants.TABLE_COMMID + "("
            + Constants.COMMUNICATION_ID + " TEXT ," + Constants.SENSER_ID + " TEXT," + Constants.RECIEVER_ID + " TEXT )";
    public static final String CREATE_UNSENTTEXT_TABLE = "CREATE TABLE " + Constants.TABLE_UNSENTTEXT + "("
            + Constants.FRIEND_ID + " TEXT PRIMARY KEY ," + Constants.UNSENTTEXT + " TEXT )";
    SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_INBOX_TABLE);
        // db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_COMMID_TABLE);
        //  db.execSQL(CREATE_STREAM_TABLE);
        db.execSQL(CREATE_UNSENTTEXT_TABLE);
        this.db = db;

    }

    /**
     * OnUpgrade method called for upgrading database, an it also
     * handles if the table is already exists or not
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_INBOX);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_COMMID);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_UNSENTTEXT);
        //db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_CHAT);
        //db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_STREAM);
        this.db = db;
        // Create tables again
        onCreate(db);
    }

    /**
     * Setting communication id for further use
     *
     * @param communicationId
     */

    public void setCommunicationId(CommunicationIdbean communicationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COMMUNICATION_ID, communicationId.getCommID()); // Friend Id
        values.put(Constants.SENSER_ID, communicationId.getSenderId()); // Friend Mobile
        values.put(Constants.RECIEVER_ID, communicationId.getRecieverId()); // Friend Title
        db.insert(Constants.TABLE_COMMID, null, values); //insert data
        db.close();
    }

    public List<CommunicationIdbean> getAllCommId() {
        List<CommunicationIdbean> commidList = new ArrayList<CommunicationIdbean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_COMMID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CommunicationIdbean contact = new CommunicationIdbean();
                contact.setCommID(cursor.getString(0));
                contact.setSenderId(cursor.getString(1));
                contact.setRecieverId(cursor.getString(2));
                // Adding contact to list
                commidList.add(contact);
            } while (cursor.moveToNext());
        }
 /*query=create table BagContact (friendId integer,FriendMobile text,,FriendTitle text,,FriendEmail text,
        FriendLatitude text,FriendLongitude text,FriendName text,FriendPhoto text) */
        return commidList;
    }

    /**
     * Running update command for updating contacts with particular id.
     *
     * @param list
     */

    public void updateAllContact(List<MyFriendsBean> list) {

        // db.execSQL("delete from "+ Constants.TABLE_CONTACTS);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int c = 0; c < list.size(); c++) {
     /*query=create table BagContact (friendId integer,FriendMobile text,,FriendTitle text,,FriendEmail text,
        FriendLatitude text,FriendLongitude text,FriendName text,FriendPhoto text) */
            MyFriendsBean contact = list.get(c);
            values.put(Constants.FRIEND_ID, contact.getId()); // Friend Id
            values.put(Constants.FRIEND_MOBILE, contact.getMobileNumber()); // Friend Mobile
            values.put(Constants.FRIEND_TITLE, contact.getTitle()); // Friend Title
            values.put(Constants.FRIEND_EMAIL, contact.getEmail()); // Friend Email
            values.put(Constants.FRIEND_LATITUDE, contact.getLatitude()); // Friend Latitude
            values.put(Constants.FRIEND_LONGITUDE, contact.getLongitude()); // Friend Longitude
            values.put(Constants.FRIEND_NAME, contact.getName()); // Friend Name
            values.put(Constants.FRIEND_PHOTO, contact.getPhoto()); // Friend Photo
            values.put(Constants.FRIEND_STATUS, contact.getStatus()); // Friend Photo
            values.put(Constants.FRIEND_COMMID, contact.getCommunicationId()); // Friend Photo
            db.update(Constants.TABLE_CONTACTS, values, Constants.FRIEND_ID + " = ?",
                    new String[]{String.valueOf(contact.getId())});  //data update
        }
        db.close();


    }

    /**
     * Getting all friend's contacts from database.
     *
     * @return
     */

    public List<MyFriendsBean> getAllContacts() {
        List<MyFriendsBean> contactList = new ArrayList<MyFriendsBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MyFriendsBean contact = new MyFriendsBean();
                contact.setId(cursor.getString(0));
                contact.setMobileNumber(cursor.getString(1));
                contact.setTitle(cursor.getString(2));
                contact.setEmail(cursor.getString(3));
                contact.setLatitude(cursor.getString(4));
                contact.setLongitude(cursor.getString(5));
                contact.setName(cursor.getString(6));
                contact.setPhoto(cursor.getString(7));
                contact.setStatus(cursor.getString(8));
                contact.setCommunicationId(cursor.getString(9));

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
 /*query=create table BagContact (friendId integer,FriendMobile text,,FriendTitle text,,FriendEmail text,
        FriendLatitude text,FriendLongitude text,FriendName text,FriendPhoto text) */
        return contactList;
    }

    /**
     * Setting/saving all contacts in dataBase and friend's bean for retrieving it afterwards.
     *
     * @param list
     */

    public void setAllContacts(List<MyFriendsBean> list) {

        // db.execSQL("delete from "+ Constants.TABLE_CONTACTS);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int c = 0; c < list.size(); c++) {
     /*query=create table BagContact (friendId integer,FriendMobile text,,FriendTitle text,,FriendEmail text,
        FriendLatitude text,FriendLongitude text,FriendName text,FriendPhoto text) */
            MyFriendsBean contact = list.get(c);
            values.put(Constants.FRIEND_ID, contact.getId()); // Friend Id
            values.put(Constants.FRIEND_MOBILE, contact.getMobileNumber()); // Friend Mobile
            values.put(Constants.FRIEND_TITLE, contact.getTitle()); // Friend Title
            values.put(Constants.FRIEND_EMAIL, contact.getEmail()); // Friend Email
            values.put(Constants.FRIEND_LATITUDE, contact.getLatitude()); // Friend Latitude
            values.put(Constants.FRIEND_LONGITUDE, contact.getLongitude()); // Friend Longitude
            values.put(Constants.FRIEND_NAME, contact.getName()); // Friend Name
            values.put(Constants.FRIEND_PHOTO, contact.getPhoto()); // Friend Photo
            values.put(Constants.FRIEND_STATUS, contact.getStatus()); // Friend Photo
            values.put(Constants.FRIEND_COMMID, contact.getCommunicationId()); // Friend Photo
            db.insert(Constants.TABLE_CONTACTS, null, values); //insert data
        }
        db.close();

    }

    public void updateInbox(InboxBean list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.FRIEND_IDI, list.getFriendId()); // Friend Id
        values.put(Constants.PHOTO, list.getPhoto()); // Friend Photo
        values.put(Constants.FRIEND_NAMEI, list.getFriendName()); // Friend Name
        values.put(Constants.COMMUNICATION_IDI, list.getCommunicationId()); // Friend last seen
        values.put(Constants.LAST_SMS, list.getLastMessage()); // Friend last seen
        values.put(Constants.SMSTIME, list.getLastSeen()); // Friend status
        values.put(Constants.INDICATER, list.getIndicater()); // getting friend/store owner
        values.put(Constants.SMSCOUNT, list.getSmsCount());
        db.update(Constants.TABLE_INBOX, values, Constants.FRIEND_IDI + " = ?",
                new String[]{String.valueOf(list.getFriendId())});  //data update
        db.close();

    }

    public ArrayList<InboxBean> getInbox() {
        ArrayList<InboxBean> inboxList = new ArrayList<InboxBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_INBOX + " ORDER BY " + Constants.SMSTIME + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                InboxBean contact = new InboxBean();
                contact.setFriendId(cursor.getString(0));
                contact.setPhoto(cursor.getString(1));
                contact.setFriendName(cursor.getString(2));
                contact.setCommunicationId(cursor.getString(3));
                contact.setLastMessage(cursor.getString(4));
                contact.setLastSeen(cursor.getString(5));
                contact.setIndicater(cursor.getString(6));
                contact.setSmsCount(Integer.parseInt(cursor.getString(7)));
                // Adding contact to list
                inboxList.add(contact);
            } while (cursor.moveToNext());
        }

        return inboxList;
    }

    public void setInbox(InboxBean list) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.FRIEND_IDI, list.getFriendId()); // Friend Id
        values.put(Constants.PHOTO, list.getPhoto()); // Friend Photo
        values.put(Constants.FRIEND_NAMEI, list.getFriendName()); // Friend Name
        values.put(Constants.COMMUNICATION_IDI, list.getCommunicationId()); // Friend last seen
        values.put(Constants.LAST_SMS, list.getLastMessage()); // Friend last seen
        values.put(Constants.SMSTIME, list.getLastSeen()); // Friend status
        values.put(Constants.INDICATER, list.getIndicater()); // Friend status
        values.put(Constants.SMSCOUNT, list.getSmsCount());

        db.insert(Constants.TABLE_INBOX, null, values); //insert data
        db.close();

    }

    /**
     * Adding new chat in the database for retrieving it for afterwards.
     * @param contact
     */

  /*  public void addChat(ChatingBean contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.FRIEND_ID, contact.getFriendId()); // Sender Id
        values.put(Constants.CUSTOMER_USER, contact.getSenderId()); // Reciever Id
        values.put(Constants.COMMUNICATION_ID, contact.getCommunicationID()); // Message Body Type
        values.put(Constants.BODYTYPE, contact.getBodyType()); // Message Body
        values.put(Constants.BODY, contact.getMessageBody()); // Creating Date
        values.put(Constants.DATE, contact.getDate()); // Creating Date

        // Inserting Row
        db.insert(Constants.TABLE_CHAT, null, values);
        db.close(); // Closing database connection
    }
*/

    /**
     * Getting chats saved in database for using for history.
     *
     * @param senderId
     * @return
     */

   /* public ArrayList<ChatingBean> getChat(String senderId) {
        ArrayList<ChatingBean> chatList = new ArrayList<ChatingBean>();

        String selectQuery = "SELECT  * FROM " + Constants.TABLE_CHAT +" WHERE "+Constants.FRIEND_ID+"=?"+" or "+Constants.CUSTOMER_USER+"=?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{senderId,senderId});
        if (cursor.moveToFirst()) {
            do {
                ChatingBean chat = new ChatingBean();
                chat.setFriendId(cursor.getString(0));
                chat.setSenderId(cursor.getString(1));
                chat.setCommunicationID(cursor.getString(2));
                chat.setBodyType(cursor.getString(3));
                chat.setMessageBody(cursor.getString(4));
                chat.setDate(cursor.getString(5));
                // Adding contact to list
                chatList.add(chat);
            } while (cursor.moveToNext());
        }
        return chatList;
    }*/
    public void updateInboxCountAndSeen(String id, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.SMSCOUNT, "0"); // Friend Id
        values.put(Constants.SMSTIME, time);
        db.update(Constants.TABLE_INBOX, values, Constants.FRIEND_IDI + " = ?",
                new String[]{String.valueOf(id)});  //data update
        db.close();
    }

    /**
     * Setting chat append(unsent text) from chat where message not sent
     *
     * @param unSeenBean
     */

    public void setUnSentText(UnSeenBean unSeenBean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String selectQuery = "SELECT  * FROM " + Constants.TABLE_UNSENTTEXT + " WHERE " + Constants.FRIEND_ID + "=" + unSeenBean.getFriendID();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            updateText(unSeenBean.getFriendID(), unSeenBean.getUnSeenText());

        } else {
            values.put(Constants.FRIEND_ID, unSeenBean.getFriendID()); // Friend Id
            values.put(Constants.UNSENTTEXT, unSeenBean.getUnSeenText());//UnSentText

            db.insert(Constants.TABLE_UNSENTTEXT, null, values);
            db.close();
        }
    }

    /**
     * Setting chat append(unsent text) from chat where message not sent
     *
     * @param friendId
     * @return
     */


    public UnSeenBean getUnSentText(String friendId) {
        UnSeenBean unSeenBean = new UnSeenBean();

        String selectQuery = "SELECT  * FROM " + Constants.TABLE_UNSENTTEXT
                + " WHERE " + Constants.FRIEND_ID + "=" + friendId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            unSeenBean.setFriendID(cursor.getString(0));
            unSeenBean.setUnSeenText(cursor.getString(1));
            return unSeenBean;
        } else {
            return unSeenBean;
        }
    }

    /**
     * Updating text if already exist in database.
     *
     * @param friendId
     * @param unSentText
     */

    public void updateText(String friendId, String unSentText) {
        String updateQuery;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (unSentText.equals("")) {
                updateQuery = "UPDATE " + Constants.TABLE_UNSENTTEXT +
                        " SET " + Constants.UNSENTTEXT + "= \"\" WHERE " + Constants.FRIEND_ID + "='" + friendId + "';";

            } else {
                updateQuery = "UPDATE " + Constants.TABLE_UNSENTTEXT +
                        " SET " + Constants.UNSENTTEXT + "='" + unSentText + "' WHERE " + Constants.FRIEND_ID + "='" + friendId + "';";

            }
            db.execSQL(updateQuery);
            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
