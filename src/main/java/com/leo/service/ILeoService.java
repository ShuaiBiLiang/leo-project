package com.leo.service;

import com.leo.model.LeoMessage;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by liang on 2017/6/6.
 */
public interface ILeoService {

    LeoMessage refreshPrice(String cookie, String currentPrice);

    LeoMessage commitForm(String cookie, String price, String size);

    LeoMessage getCookie(String userInfo);
}
