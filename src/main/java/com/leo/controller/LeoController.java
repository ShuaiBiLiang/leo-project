package com.leo.controller;

import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import com.leo.service.ILeoService;
import com.leo.util.UrlConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liang on 2017/6/6.
 */
@RestController
@RequestMapping("/admin")
public class LeoController {

    @Autowired
    protected ILeoService leoService;

    @RequestMapping("/leo_in")
    @ResponseBody
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/leo/price")
    @ResponseBody
    public ServerResponse<LeoMessage> refreshPrice(@RequestParam String cookie, @RequestParam String currentPrice, Model model) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String d1 = dateFormat.format(new Date());
        LeoMessage leoMessage = leoService.refreshPrice(cookie,currentPrice);
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(leoMessage.getMsg(),leoMessage);
        String d2 = dateFormat.format(new Date());
        System.out.println("刷新请求：开始-"+d1+"  "+leoMessage.getMsg()+"  结束："+d2);
        return response;
    }

    @RequestMapping("/leo/commit")
    @ResponseBody
    public ServerResponse<LeoMessage> commitForm(@RequestParam String cookie, @RequestParam String currentPrice, @RequestParam String totalCoin, Model model) {
        System.out.println("提交请求开始。。。"+cookie.substring(0,20));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String d1 = dateFormat.format(new Date());
        UrlConnectionUtil.setCommitPriceNow(true);
        LeoMessage leoMessage = leoService.commitForm(cookie,currentPrice,totalCoin);
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(leoMessage.getMsg(),leoMessage);
        UrlConnectionUtil.setCommitPriceNow(false);
        String d2 = dateFormat.format(new Date());
        System.out.println("提交请求：开始-"+d1+"  "+leoMessage.getMsg()+"  结束："+d2);
        return response;
    }

    @RequestMapping("/leo/getCookie")
    @ResponseBody
    public ServerResponse<LeoMessage> getCookie(@RequestParam String userInfo, Model model) {
        LeoMessage leoMessage = leoService.getCookie(userInfo);
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(leoMessage.getMsg(),leoMessage);
        UrlConnectionUtil.setCommitPriceNow(false);
        return response;
    }
}
