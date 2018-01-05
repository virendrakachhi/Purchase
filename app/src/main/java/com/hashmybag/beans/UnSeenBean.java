package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting chat appending text details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-27
 */

public class UnSeenBean {

    public String friendID;
    public String unSeenText;


    public String getUnSeenText() {
        return unSeenText;
    }

    public void setUnSeenText(String unSeenText) {
        this.unSeenText = unSeenText;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }
}
