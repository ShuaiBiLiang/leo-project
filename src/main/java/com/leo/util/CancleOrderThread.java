package com.leo.util;

import com.leo.model.LeoMessage;
import com.leo.model.NamePwdCookie;
import com.leo.model.OrderDetail;
import com.leo.service.ILeoService;
import org.springframework.core.annotation.Order;

import java.util.concurrent.CountDownLatch;

/**
 * Created by liang on 2017/6/9.
 */
public class CancleOrderThread implements Runnable{

    ILeoService leoService;
    CountDownLatch latch;
    OrderDetail orderDetail;

    public CancleOrderThread(ILeoService service, CountDownLatch countDownLatch, OrderDetail orderDetail){
        this.leoService = service;
        this.latch = countDownLatch;
        this.orderDetail = orderDetail;
    }
        @Override
        public void run() {
            OrderDetail msg = leoService.cancelOrder(orderDetail);
            latch.countDown();
        }

}
