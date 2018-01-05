package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting online offline user details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-30
 */

public class OnlineBean {
    String onlineUser;
    String userStatus;

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getOnlineUser() {

        return onlineUser;
    }

    public void setOnlineUser(String onlineUser) {
        this.onlineUser = onlineUser;
    }
}
