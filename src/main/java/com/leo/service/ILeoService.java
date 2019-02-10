package com.leo.service;

import com.leo.model.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by liang on 2017/6/6.
 */
public interface ILeoService {

    LeoMessage refreshPrice(String cookie, String currentPrice);

    LeoMessage commitForm(String cookie, String price, String size);

    LeoMessage getCookie(String userInfo);

    List<NamePwdCookie> getCookies(String userInfo);

    List<OrderDetail> getOrders(String cookie);

    OrderDetail cancelOrder(OrderDetail orderDetail);

    List<OrderDetail> cancelOrders(List<OrderDetail> orderDetails);
}
