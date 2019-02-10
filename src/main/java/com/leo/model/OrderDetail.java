package com.leo.model;

/**
 * Created by liang on 2017/6/8.
 */
public class OrderDetail {
    String name;
    String id;
    String orderType; //sell
    String volume;//数量
    String price;//单价
    String cost;//金额
    String dateTime;//时间
    String status;//状态
    String cookie;
    boolean cancelSuccess;

    public OrderDetail() {
    }

    public OrderDetail(String name, String id, String orderType, String volume, String price, String cost, String dateTime, String status, String cookie) {
        this.name = name;
        this.id = id;
        this.orderType = orderType;
        this.volume = volume;
        this.price = price;
        this.cost = cost;
        this.dateTime = dateTime;
        this.status = status;
        this.cookie = cookie;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public boolean isCancelSuccess() {
        return cancelSuccess;
    }

    public void setCancelSuccess(boolean cancelSuccess) {
        this.cancelSuccess = cancelSuccess;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", orderType='" + orderType + '\'' +
                ", volume='" + volume + '\'' +
                ", price='" + price + '\'' +
                ", cost='" + cost + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
