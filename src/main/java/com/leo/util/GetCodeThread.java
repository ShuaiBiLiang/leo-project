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
public class GetCodeThread implements Runnable{

    ILeoService leoService;
    NamePwdCookie namePwdCookie;
    LeoUser user;

    public GetCodeThread(LeoUser user, ILeoService service, NamePwdCookie namePwdCookie){
        this.leoService = service;
        this.namePwdCookie = namePwdCookie;
        this.user = user;
    }
        @Override
        public void run() {

            LeoMessage msg = leoService.getCode(namePwdCookie.getName()+" "+namePwdCookie.getPwd()+" "+namePwdCookie.getCode());

            msg.setName(namePwdCookie.getName());
            ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",msg);
            response.setMsgType(MyWebSocket.MSG_TYPE_GET_CODE);

                MyWebSocket.sendMsg(this.user.getName(),new Gson().toJson(response));


        }

}
