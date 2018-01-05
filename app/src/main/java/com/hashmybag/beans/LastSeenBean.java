package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting lastSeen details of the users.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-15
 */

public class LastSeenBean {
    String userId;
    String lastSeen;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }
}
