package com.hashmybag.beans;

import java.io.Serializable;

/**
 * This class is used as a bean class for setting and getting chatting details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-09
 */

public class ChatingBean implements Serializable {
    String senderId;
    String friendId;
    String messageText, imageUrl, CommunicationID;
    String Date;
    String bodyType, title;
    boolean fromStream;
    String msgDate, paymentOption;
    //if product info availabe then add
    String paymentId, paymentPrice, productId, productCode, productDescription, productImage, productUpdated, productedCreated, chatID, productPrice, currencyType, productName, productStoreId;
    //if any other stream available
    String streamId, streamPhoto, streamTitle, streamDesc, streamDate;
    Boolean isOnline;
    String userOnline;
    Boolean walletOption, codOption, cardOption, favorite;

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public boolean isFromStream() {
        return fromStream;
    }

    public void setFromStream(boolean fromStream) {
        this.fromStream = fromStream;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
    }

    public String getStreamPhoto() {
        return streamPhoto;
    }

    public void setStreamPhoto(String streamPhoto) {
        this.streamPhoto = streamPhoto;
    }

    public String getStreamTitle() {
        return streamTitle;
    }

    public void setStreamTitle(String streamTitle) {
        this.streamTitle = streamTitle;
    }

    public String getStreamDesc() {
        return streamDesc;
    }

    public void setStreamDesc(String streamDesc) {
        this.streamDesc = streamDesc;
    }

    public String getStreamDate() {
        return streamDate;
    }

    public void setStreamDate(String streamDate) {
        this.streamDate = streamDate;
    }

    public String getProductStoreId() {
        return productStoreId;
    }

    public void setProductStoreId(String productStoreId) {
        this.productStoreId = productStoreId;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public String getUserOnline() {
        return userOnline;
    }

    public void setUserOnline(String userOnline) {
        this.userOnline = userOnline;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getCommunicationID() {
        return CommunicationID;
    }

    public void setCommunicationID(String communicationID) {
        CommunicationID = communicationID;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }


    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentPrice() {
        return paymentPrice;
    }

    public void setPaymentPrice(String paymentPrice) {
        this.paymentPrice = paymentPrice;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductUpdated() {
        return productUpdated;
    }

    public void setProductUpdated(String productUpdated) {
        this.productUpdated = productUpdated;
    }

    public String getProductedCreated() {
        return productedCreated;
    }

    public void setProductedCreated(String productedCreated) {
        this.productedCreated = productedCreated;
    }

    public Boolean getWalletOption() {
        return walletOption;
    }

    public void setWalletOption(Boolean walletOption) {
        this.walletOption = walletOption;
    }

    public Boolean getCodOption() {
        return codOption;
    }

    public void setCodOption(Boolean codOption) {
        this.codOption = codOption;
    }

    public Boolean getCardOption() {
        return cardOption;
    }

    public void setCardOption(Boolean cardOption) {
        this.cardOption = cardOption;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


}
