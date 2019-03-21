package com.leo.model;

import java.util.Date;
import java.util.List;

/**
 * Created by liang on 2017/6/8.
 */
public class LeoMessage {
    String msg;
    String name;
    String price;
    Date startDate;
    String startTime;
    Date endDate;
    String endTime;
    String error;
    String cookie;
    String availableBalance;
    String earningAccount;
    boolean loginError;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isLoginError() {
        return loginError;
    }

    public void setLoginError(boolean loginError) {
        this.loginError = loginError;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LeoMessage{" +
                "msg='" + msg + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", startDate=" + startDate +
                ", startTime='" + startTime + '\'' +
                ", endDate=" + endDate +
                ", endTime='" + endTime + '\'' +
                ", error='" + error + '\'' +
                ", loginError=" + loginError +
                '}';
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getEarningAccount() {
        return earningAccount;
    }

    public void setEarningAccount(String earningAccount) {
        this.earningAccount = earningAccount;
    }
}
