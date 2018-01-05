package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting store details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-26
 */
public class StoreBean {
    int imageUrl;
    String storeName;

    String storeId;
    String storeEmail;
    String storeTitle;
    String storeDescription;
    String storeAddress1;
    String storePhoto;
    String storeLatitude;
    String storeLongitude;
    String storeActive;
    String storeLocation;
    String storeMobileNumber;
    String storeLandlineNUmber;

    public StoreBean() {
    }

    public StoreBean(int nike4, String nike, String s) {
    }

    public String getStoreActive() {
        return storeActive;
    }

    public void setStoreActive(String storeActive) {
        this.storeActive = storeActive;
    }

    public String getStoreMobileNumber() {
        return storeMobileNumber;
    }

    public void setStoreMobileNumber(String storeMobileNumber) {
        this.storeMobileNumber = storeMobileNumber;
    }

    public String getStoreLandlineNUmber() {
        return storeLandlineNUmber;
    }

    public void setStoreLandlineNUmber(String storeLandlineNUmber) {
        this.storeLandlineNUmber = storeLandlineNUmber;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreTitle() {
        return storeTitle;
    }

    public void setStoreTitle(String storeTitle) {
        this.storeTitle = storeTitle;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public String getStoreAddress1() {
        return storeAddress1;
    }

    public void setStoreAddress1(String storeAddress1) {
        this.storeAddress1 = storeAddress1;
    }

    public String getStorePhoto() {
        return storePhoto;
    }

    public void setStorePhoto(String storePhoto) {
        this.storePhoto = storePhoto;
    }

    public String getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public String getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }
}
