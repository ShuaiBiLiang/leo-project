package com.leo.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.leo.common.ServerResponse;
import com.leo.model.*;
import com.leo.model.domain.LeoUser;
import com.leo.service.ILeoService;
import com.leo.util.*;
import com.leo.service.LeoUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Created by liang on 2017/6/6.
 */
@RestController
@RequestMapping("/admin")
public class LeoController {

    @Autowired
    protected ILeoService leoService;

    @Autowired
    protected LeoUserService leoUserService;



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
    public ServerResponse<List<NamePwdCookie>> getCookies(HttpServletRequest request,HttpServletResponse resp,@RequestBody Map<String,String> userInfo) {
        String[] userArray = userInfo.get("userInfo").split("\\n");
        List<NamePwdCookie> requestNames = new ArrayList<>();
        for(String user:userArray) {
            String[] userParams = user.trim().split(" ");
            NamePwdCookie namePwdCookie = new NamePwdCookie(userParams[0], userParams[1], userParams[2], "");
            requestNames.add(namePwdCookie);
        }
        String name= resp.getHeader("name");
        String size= resp.getHeader("size");
        List<NamePwdCookie> namePwdCookieList= UserLeoUtil.getInstance().get(name);
        if(namePwdCookieList==null){
            namePwdCookieList = new ArrayList<>();
        }
        int sizeInt = Integer.parseInt(size);
        int newCount = countNewLeoNum(requestNames,namePwdCookieList);
        if((namePwdCookieList.size()+newCount)>sizeInt){
            String message = "挂币账号超过上限!";

            if(namePwdCookieList.size()>0){
                if(namePwdCookieList.size()<sizeInt){
                    message+="<br/> 只能再挂("+(sizeInt-namePwdCookieList.size())+"个)。";
                }

                message+="<br/> 已挂账号("+namePwdCookieList.size()+"个)：";
                for (NamePwdCookie namePwdCookie:namePwdCookieList){
                    message+="【"+namePwdCookie.getName()+"】";
                }
            }
            return ServerResponse.createByErrorCodeMessage(88,message);
        }

        List<NamePwdCookie> leoMessage = leoService.getCookies(requestNames);



        ServerResponse<List<NamePwdCookie>> response = ServerResponse.createBySuccess("success",leoMessage);

        if(UserLeoUtil.getInstance()!=null){

            for(NamePwdCookie namePwdCookie:response.getData()){
                if(!namePwdCookie.isLoginError() && !namePwdCookieList.contains(namePwdCookie)){
                    namePwdCookieList.add(namePwdCookie);
                }
            }
            UserLeoUtil.getInstance().put(name,namePwdCookieList);
        }
        return response;
    }

    private int countNewLeoNum(List<NamePwdCookie> requestNames, List<NamePwdCookie> namePwdCookieList) {

        int count=0;
        for(NamePwdCookie requestItem:requestNames){
            if(!namePwdCookieList.contains(requestItem)){
                count++;
            }
        }

        return count;
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

    @RequestMapping(value = "/leo/activeCookie",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,List<OrderDetail>>> activeCookie(@RequestBody List<CommitParam> userInfo) {
        Map<String,List<OrderDetail>> result = new HashMap<>();
        for(CommitParam param:userInfo){
            NamePwdCookie namePwdCookie = new NamePwdCookie();
            namePwdCookie.setCookie(param.getCookie());
            namePwdCookie.setName(param.getName());
//            Thread thread = new Thread(new RefreshPriceThread(leoService,namePwdCookie));
            ExecutorPool.executeWithManualPool(new RefreshPriceThread(leoService,namePwdCookie));
//            thread.start();
        }
        ServerResponse<Map<String,List<OrderDetail>>> response = ServerResponse.createBySuccess("success",result);
        return response;
    }

    @RequestMapping(value = "/leo/login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoUserVo> userLogin(@RequestBody NamePwdCookie userInfo) {
        ServerResponse<LeoUserVo> response = leoUserService.login(userInfo);
        return response;
    }

    @RequestMapping(value = "/user/list",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<LeoUser>> userList(HttpServletResponse resp,@RequestBody PageListAccept accept) {
        String name= resp.getHeader("name");
        if(!name.equals("admin")){
            return ServerResponse.createByError();
        }
        List<LeoUser> users = leoUserService.selectAll();
        users = users.stream().filter(o->!o.getName().equals("admin")).collect(Collectors.toList());
        ServerResponse<List<LeoUser>> response = ServerResponse.createBySuccess(users);
        return response;
    }

    @RequestMapping(value = "/user/save",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoUser> userSave(HttpServletResponse resp,@RequestBody UserSaveAccept accept) {
        String name= resp.getHeader("name");
        if(!name.equals("admin")){
            return ServerResponse.createByError();
        }
        LeoUser user = leoUserService.save(accept);
        ServerResponse<LeoUser> response = ServerResponse.createBySuccess(user);
        return response;
    }

    @RequestMapping(value = "/user/resetPwd",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoUser> resetPwd(HttpServletResponse resp,@RequestBody UserSaveAccept accept) {
        String name= resp.getHeader("name");
        if(name.equals("admin")){
            return ServerResponse.createByError();
        }
        ServerResponse<LeoUser> response = leoUserService.resetPwd(name,accept);
        /*if(user==null){
            return ServerResponse.createByError();
        }
        ServerResponse<LeoUser> response = ServerResponse.createBySuccess(user);*/
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
