package com.leo.util;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import com.leo.model.NamePwdCookie;
import com.leo.model.domain.LeoUser;
import com.leo.service.ILeoService;
import com.leo.service.impl.MyWebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liang on 2017/6/9.
 */
public class GetCookiesThread implements Runnable{

    ILeoService leoService;
    CountDownLatch latch;
    NamePwdCookie namePwdCookie;
    LeoUser user;

    public GetCookiesThread(LeoUser user, ILeoService service, CountDownLatch countDownLatch, NamePwdCookie namePwdCookie){
        this.leoService = service;
        this.latch = countDownLatch;
        this.namePwdCookie = namePwdCookie;
        this.user = user;
    }
        @Override
        public void run() {

            LeoMessage msg = leoService.getCookie(namePwdCookie.getName()+" "+namePwdCookie.getPwd()+" "+namePwdCookie.getCode());
            String cookie = msg.getMsg();
            namePwdCookie.setCookie(cookie);
            namePwdCookie.setLoginError(msg.isLoginError());

            ServerResponse<NamePwdCookie> response = ServerResponse.createBySuccess("success",namePwdCookie);
            response.setMsgType(MyWebSocket.MSG_TYPE_GET_COOKIE);
            try {
                MyWebSocket.sendMsg(this.user.getName(),new Gson().toJson(response));
            } catch (IOException e) {
                System.out.println("登录leo帐号，通过websocket发送结果给用户："+this.user.getName()+",失败！");
            }
            String name = user.getName();
            if(UserLeoUtil.getInstance()!=null){
                List<NamePwdCookie> namePwdCookieList= UserLeoUtil.getInstance().get(name);
                if(namePwdCookieList==null){
                    namePwdCookieList = new ArrayList<>();
                }
                if(!namePwdCookie.isLoginError() && !namePwdCookieList.contains(namePwdCookie)){
                    namePwdCookieList.add(namePwdCookie);
                }
                UserLeoUtil.getInstance().put(name,namePwdCookieList);
            }
            latch.countDown();
        }

}
