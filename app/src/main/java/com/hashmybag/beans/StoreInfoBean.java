package com.hashmybag.beans;

import java.util.ArrayList;

/**
 * This class is used as a bean class for setting and getting store information details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-05-13
 */

public class StoreInfoBean {
    String title;
    String description;
    String email;
    String location;
    String address1;
    String photo;
    String mobile_number;
    String landline_number;
    String latitude;
    String longitude;
    String active;
    String identity_proof;
    String registration_certificate;
    String twitter_username;
    String facebook_page;
    String instagram_user_name;
    String authentication_token;
    String id;
    String following;
    ArrayList<ChannelBean> channelBeanArrayList;
    String category_id;
    String category_name;
    String category_created_at;
    String category_updated_at;
    String unread_msg_count;
    ArrayList<CommunicationBean> communicationBeanArrayList;
    public StoreInfoBean() {
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<CommunicationBean> getCommunicationBeanArrayList() {
        return communicationBeanArrayList;
    }

    public void setCommunicationBeanArrayList(ArrayList<CommunicationBean> communicationBeanArrayList) {
        this.communicationBeanArrayList = communicationBeanArrayList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getLandline_number() {
        return landline_number;
    }

    public void setLandline_number(String landline_number) {
        this.landline_number = landline_number;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getIdentity_proof() {
        return identity_proof;
    }

    public void setIdentity_proof(String identity_proof) {
        this.identity_proof = identity_proof;
    }

    public String getRegistration_certificate() {
        return registration_certificate;
    }

    public void setRegistration_certificate(String registration_certificate) {
        this.registration_certificate = registration_certificate;
    }

    public String getTwitter_username() {
        return twitter_username;
    }

    public void setTwitter_username(String twitter_username) {
        this.twitter_username = twitter_username;
    }

    public String getFacebook_page() {
        return facebook_page;
    }

    public void setFacebook_page(String facebook_page) {
        this.facebook_page = facebook_page;
    }

    public String getInstagram_user_name() {
        return instagram_user_name;
    }

    public void setInstagram_user_name(String instagram_user_name) {
        this.instagram_user_name = instagram_user_name;
    }

    public String getAuthentication_token() {
        return authentication_token;
    }

    public void setAuthentication_token(String authentication_token) {
        this.authentication_token = authentication_token;
    }

    public ArrayList<ChannelBean> getChannelBeanArrayList() {
        return channelBeanArrayList;
    }

    public void setChannelBeanArrayList(ArrayList<ChannelBean> channelBeanArrayList) {
        this.channelBeanArrayList = channelBeanArrayList;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_created_at() {
        return category_created_at;
    }

    public void setCategory_created_at(String category_created_at) {
        this.category_created_at = category_created_at;
    }

    public String getCategory_updated_at() {
        return category_updated_at;
    }

    public void setCategory_updated_at(String category_updated_at) {
        this.category_updated_at = category_updated_at;
    }

    public String getUnread_msg_count() {
        return unread_msg_count;
    }

    public void setUnread_msg_count(String unread_msg_count) {
        this.unread_msg_count = unread_msg_count;
    }
}
