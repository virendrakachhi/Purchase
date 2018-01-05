package com.hashmybag.beans;

import java.io.Serializable;

/**
 * This class is used as a bean class for setting and getting stream information and details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-27
 */

public class StreamBean implements Serializable {
    String channelId, streamId, commId, broadCastId, createAt, updateAt, title, description, streamType, image_url;
    String storeId, storeTitle, storePhoto, storeEmail, storeDesc;
    boolean inWhishlist, codOption, cardOption, walletOption;
    String productId, productName, productCode, productDesc, producImageUrl, price, currencytype, paymentOptionId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getCommId() {
        return commId;
    }

    public void setCommId(String commId) {
        this.commId = commId;
    }

    public String getBroadCastId() {
        return broadCastId;
    }

    public void setBroadCastId(String broadCastId) {
        this.broadCastId = broadCastId;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getPaymentOptionId() {
        return paymentOptionId;
    }

    public void setPaymentOptionId(String paymentOptionId) {
        this.paymentOptionId = paymentOptionId;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
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

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

    public boolean isCodOption() {
        return codOption;
    }

    public void setCodOption(boolean codOption) {
        this.codOption = codOption;
    }

    public boolean isCardOption() {
        return cardOption;
    }

    public void setCardOption(boolean cardOption) {
        this.cardOption = cardOption;
    }

    public boolean isWalletOption() {
        return walletOption;
    }

    public void setWalletOption(boolean walletOption) {
        this.walletOption = walletOption;
    }

    public String getStorePhoto() {
        return storePhoto;
    }

    public void setStorePhoto(String storePhoto) {
        this.storePhoto = storePhoto;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStoreDesc() {
        return storeDesc;
    }

    public void setStoreDesc(String storeDesc) {
        this.storeDesc = storeDesc;
    }

    public boolean isInWhishlist() {
        return inWhishlist;
    }

    public void setInWhishlist(boolean inWhishlist) {
        this.inWhishlist = inWhishlist;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProducImageUrl() {
        return producImageUrl;
    }

    public void setProducImageUrl(String producImageUrl) {
        this.producImageUrl = producImageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrencytype() {
        return currencytype;
    }

    public void setCurrencytype(String currencytype) {
        this.currencytype = currencytype;
    }
}
