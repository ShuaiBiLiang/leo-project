package com.leo.util;

import com.leo.model.LeoMessage;
import com.leo.model.NamePwdCookie;
import com.leo.service.ILeoService;

import java.util.concurrent.CountDownLatch;

/**
 * Created by liang on 2017/6/9.
 */
public class GetCookiesThread implements Runnable{

    ILeoService leoService;
    CountDownLatch latch;
    NamePwdCookie namePwdCookie;

    public GetCookiesThread(ILeoService service, CountDownLatch countDownLatch, NamePwdCookie namePwdCookie){
        this.leoService = service;
        this.latch = countDownLatch;
        this.namePwdCookie = namePwdCookie;
    }
        @Override
        public void run() {

            LeoMessage msg = leoService.getCookie(namePwdCookie.getName()+" "+namePwdCookie.getPwd()+" "+namePwdCookie.getCode());
            String cookie = msg.getMsg();
            namePwdCookie.setCookie(cookie);
            latch.countDown();
        }

}
