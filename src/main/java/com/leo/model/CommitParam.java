package com.leo.model;

public class CommitParam {
    String name;
    String cookie;
    String price;
    String num;
    String userName;

    String availableBalance;
    String earningAccount;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
