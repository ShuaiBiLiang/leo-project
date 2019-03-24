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
    NamePwdCookie namePwdCookie;
    LeoUser user;

    public GetCookiesThread(LeoUser user, ILeoService service, NamePwdCookie namePwdCookie){
        this.leoService = service;
        this.namePwdCookie = namePwdCookie;
        this.user = user;
    }
        @Override
        public void run() {

            LeoMessage msg = leoService.getCookie(namePwdCookie.getName()+" "+namePwdCookie.getPwd()+" "+namePwdCookie.getCode());
            String cookie = msg.getCookie();
            if(!msg.isLoginError()){
                namePwdCookie.setCookie(cookie);
            }
            namePwdCookie.setLoginMsg(msg.getMsg());
            namePwdCookie.setLoginError(msg.isLoginError());

            ServerResponse<NamePwdCookie> response = ServerResponse.createBySuccess("success",namePwdCookie);
            response.setMsgType(MyWebSocket.MSG_TYPE_GET_COOKIE);

                MyWebSocket.sendMsg(this.user.getName(),new Gson().toJson(response));

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
        }

}
