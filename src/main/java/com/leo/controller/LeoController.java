package com.leo.controller;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.*;
import com.leo.service.ILeoService;
import com.leo.util.UrlConnectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @RequestMapping(value = "/leo/price",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoMessage> refreshPrice(@RequestBody Map<String,String> userInfo) {
        String cookie = userInfo.get("userInfo");
        String currentPrice = userInfo.get("currentPrice");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String d1 = dateFormat.format(new Date());
        LeoMessage leoMessage = leoService.refreshPrice(cookie,currentPrice);
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(leoMessage.getMsg(),leoMessage);
        String d2 = dateFormat.format(new Date());
        System.out.println("刷新请求：开始-"+d1+"  "+leoMessage.getMsg()+"  结束："+d2);
        return response;
    }

    @RequestMapping(value = "/leo/commit",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<LeoMessage>> commitForm(@RequestBody List<CommitParam> userInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        List<LeoMessage> result = new ArrayList<>();
        for(CommitParam param:userInfo){
            String cookie = param.getCookie();
            String currentPrice = param.getPrice();
            String totalCoin = param.getNum();
            String d1 = dateFormat.format(new Date());
            System.out.println("提交请求开始。。。"+cookie.substring(0,20));
            LeoMessage leoMessage = leoService.commitForm(cookie,currentPrice,totalCoin);
            result.add(leoMessage);
            String d2 = dateFormat.format(new Date());
            leoMessage.setName(param.getName());
            System.out.println("提交请求：开始-"+d1+"  "+leoMessage.getMsg()+"  结束："+d2);
        }
        ServerResponse<List<LeoMessage>> response = ServerResponse.createBySuccess("",result);
        return response;
    }

    @RequestMapping(value = "/leo/getCookie",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoMessage> getCookie(@RequestBody String userInfo, Model model) {
        LeoMessage leoMessage = leoService.getCookie(userInfo);
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(leoMessage.getMsg(),leoMessage);
        UrlConnectionUtil.setCommitPriceNow(false);
        return response;
    }

    @RequestMapping(value = "/leo/getCookies",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<NamePwdCookie>> getCookies(@RequestBody Map<String,String> userInfo) {
        List<NamePwdCookie> leoMessage = leoService.getCookies(userInfo.get("userInfo"));
        ServerResponse<List<NamePwdCookie>> response = ServerResponse.createBySuccess("success",leoMessage);
        return response;
    }

    @RequestMapping(value = "/leo/getOrders",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,List<OrderDetail>>> getOrders(@RequestBody List<CommitParam> userInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Map<String,List<OrderDetail>> result = new HashMap<>();
        for(CommitParam param:userInfo){
            String cookie = param.getCookie();
            String d1 = dateFormat.format(new Date());
            List<OrderDetail> leoMessage = leoService.getOrders(cookie);
//            leoMessage.setName(param.getName());
            result.put(param.getName(),leoMessage);
            String d2 = dateFormat.format(new Date());
            System.out.println("查询订单明细:"+param.getName()+"开始-"+d1+"    结束："+d2);
        }
        ServerResponse<Map<String,List<OrderDetail>>> response = ServerResponse.createBySuccess("success",result);
        return response;
    }


    @RequestMapping(value = "/leo/cancelOrders",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<OrderDetail>> cancelOrders(@RequestBody List<OrderDetail> details) {
        List<OrderDetail> leoMessage = leoService.cancelOrders(details);
        ServerResponse<List<OrderDetail>> response = ServerResponse.createBySuccess("success",leoMessage);
        return response;
    }

    public static void main(String[] args) {
        /*List<NamePwdCookie> leoMessage = new ArrayList<>();
        NamePwdCookie n1 = new NamePwdCookie("zhangsan","zxxxxx","de42","phpcookie=asdfjxoxcj2342342;");
        NamePwdCookie n2 = new NamePwdCookie("lisi","zxxxxx","d342","phpcookie=asdasdfadsfacj22341111;");
        leoMessage.add(n1);
        leoMessage.add(n2);
        ServerResponse<List<NamePwdCookie>> response = ServerResponse.createBySuccess("success",leoMessage);
        System.out.println(new Gson().toJson(response));*/
        Map<String,String> i = new HashMap<>();
        i.put("userInfo","asdfsda asdfasdf  sadfas");
        i.put("userInfo2","asdfsda asdfasdf  sadfas");
        System.out.println(new Gson().toJson(i));
    }
}
