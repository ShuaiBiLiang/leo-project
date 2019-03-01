package com.leo.util;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import com.leo.model.NamePwdCookie;
import com.leo.model.OrderDetail;
import com.leo.model.domain.LeoUser;
import com.leo.service.ILeoService;
import com.leo.service.impl.MyWebSocket;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liang on 2017/6/9.
 */
public class CancleOrderThread implements Runnable{

    ILeoService leoService;
    CountDownLatch latch;
    OrderDetail orderDetail;
    LeoUser user;

    public CancleOrderThread(LeoUser user,ILeoService service, CountDownLatch countDownLatch, OrderDetail orderDetail){
        this.leoService = service;
        this.latch = countDownLatch;
        this.orderDetail = orderDetail;
        this.user = user;
    }
        @Override
        public void run() {
            OrderDetail msg = leoService.cancelOrder(orderDetail);
            ServerResponse<OrderDetail> response = ServerResponse.createBySuccess("success",msg);
            response.setMsgType(MyWebSocket.MSG_TYPE_CANCLE_ORDER);
            try {
                MyWebSocket.sendMsg(this.user.getName(), new Gson().toJson(response));
            } catch (IOException e) {
                System.out.println("取消订单，通过websocket发送结果给用户："+this.user.getName()+",失败！");
            }
            latch.countDown();
        }

}
