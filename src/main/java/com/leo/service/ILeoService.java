package com.leo.service;

import com.leo.model.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by liang on 2017/6/6.
 */
public interface ILeoService {
    Pattern patternPrice = Pattern.compile("value=\"([\\d|.]*)\"");
    Pattern patternBalance = Pattern.compile("ctl00\\$ContentPlaceHolder1\\$txtCoinBalance_value\" type=\"text\" value=\"([\\S|\\s]*?)\"");
    Pattern patternAccount = Pattern.compile("ctl00\\$ContentPlaceHolder1\\$txtEarningAccount_value\" type=\"text\" value=\"([\\S|\\s]*?)\"");


    LeoMessage refreshPrice(String cookie, String currentPrice);

    LeoMessage showAccount(CommitParam commitParam);

    LeoMessage refreshPrice(String cookie, String currentPrice, boolean notRecive);

    LeoMessage commitForm(String cookie, String price, String size);

    LeoMessage getCookie(String userInfo);

    List<NamePwdCookie> getCookies(List<NamePwdCookie> requestNames);

    List<OrderDetail> getOrders(String cookie);

    OrderDetail cancelOrder(OrderDetail orderDetail);

    List<OrderDetail> cancelOrders(List<OrderDetail> orderDetails);

    LeoMessage getCode(String userInfo);
}
