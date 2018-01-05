package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting communication channel details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-05-13
 */

public class CommunicationBean {
    String channel_id;
    String channel_name;
    String channel_store_id;
    String channel_broadcasts_count;

    public CommunicationBean(String channel_id, String channel_name, String channel_store_id, String channel_broadcasts_count) {
        this.channel_id = channel_id;
        this.channel_name = channel_name;
        this.channel_store_id = channel_store_id;
        this.channel_broadcasts_count = channel_broadcasts_count;
    }

    public CommunicationBean() {
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_store_id() {
        return channel_store_id;
    }

    public void setChannel_store_id(String channel_store_id) {
        this.channel_store_id = channel_store_id;
    }

    public String getChannel_broadcasts_count() {
        return channel_broadcasts_count;
    }

    public void setChannel_broadcasts_count(String channel_broadcasts_count) {
        this.channel_broadcasts_count = channel_broadcasts_count;
    }
}
