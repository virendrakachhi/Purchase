package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting inbox details for incoming messages.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-11
 */

public class InboxBean {
    String FriendId;
    String Photo;
    String FriendName;
    String LastMessage, CommunicationId;
    String LastSeen, Indicater;
    int smsCount;

    public int getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(int smsCount) {
        this.smsCount = smsCount;
    }

    public String getIndicater() {
        return Indicater;
    }

    public void setIndicater(String indicater) {
        Indicater = indicater;
    }

    public String getLastSeen() {
        return LastSeen;
    }

    public void setLastSeen(String lastSeen) {
        LastSeen = lastSeen;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getFriendName() {
        return FriendName;
    }

    public void setFriendName(String friendName) {
        FriendName = friendName;
    }

    public String getCommunicationId() {
        return CommunicationId;
    }

    public void setCommunicationId(String communicationId) {
        CommunicationId = communicationId;
    }

    public String getLastMessage() {

        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }
}
