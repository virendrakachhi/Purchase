package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting chatting details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-28
 */

public class CommunicationIdbean {
    String commID;
    String senderId;
    String recieverId;

    public String getCommID() {
        return commID;
    }

    public void setCommID(String commID) {
        this.commID = commID;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }
}
