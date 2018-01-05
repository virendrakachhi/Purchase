package com.hashmybag.beans;

/**
 * This class is used as a bean class for setting and getting payment details.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-04-25
 */

public class PaymentBean {

    String obj_name, obj_date, obj_from, obj_price, obj_image;


    public String getObj_name() {
        return obj_name;
    }

    public void setObj_name(String obj_name) {
        this.obj_name = obj_name;
    }

    public String getObj_date() {
        return obj_date;
    }

    public void setObj_date(String obj_date) {
        this.obj_date = obj_date;
    }

    public String getObj_from() {
        return obj_from;
    }

    public void setObj_from(String obj_from) {
        this.obj_from = obj_from;
    }

    public String getObj_price() {
        return obj_price;
    }

    public void setObj_price(String obj_price) {
        this.obj_price = obj_price;
    }

    public String getObj_image() {
        return obj_image;
    }

    public void setObj_image(String obj_image) {
        this.obj_image = obj_image;
    }
}
