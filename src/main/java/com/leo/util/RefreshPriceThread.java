package com.leo.util;

import com.leo.model.LeoMessage;
import com.leo.model.NamePwdCookie;
import com.leo.service.ILeoService;

import java.util.concurrent.CountDownLatch;

/**
 * Created by liang on 2017/6/9.
 */
public class RefreshPriceThread implements Runnable{

    ILeoService leoService;
    NamePwdCookie namePwdCookie;

    public RefreshPriceThread(ILeoService service, NamePwdCookie namePwdCookie){
        this.leoService = service;
        this.namePwdCookie = namePwdCookie;
    }
        @Override
        public void run() {

            LeoMessage msg = leoService.refreshPrice(namePwdCookie.getCookie(),"",true);

        }

}
