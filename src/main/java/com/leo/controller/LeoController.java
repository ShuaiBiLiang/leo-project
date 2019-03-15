package com.leo.controller;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.*;
import com.leo.model.domain.LeoUser;
import com.leo.service.ILeoService;
import com.leo.service.LeoUserService;
import com.leo.service.impl.LeoServiceImpl;
import com.leo.service.impl.MyWebSocket;
import com.leo.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liang on 2017/6/6.
 */
@RestController
@RequestMapping("/admin")
public class LeoController {
    private static Log logger = LogFactory.getLog(LeoController.class);

    @Autowired
    protected ILeoService leoService;

    @Autowired
    protected LeoUserService leoUserService;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @RequestMapping("/leo_in")
    @ResponseBody
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/leo/price",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoMessage> refreshPrice(@RequestBody Map<String,String> userInfo) {
        LeoUser currentLoginUser = UserThreadUtil.getLeoUser();
            ExecutorPool.executeOnCachedPool(() ->{
                String cookie = userInfo.get("userInfo");
                String name = userInfo.get("name");
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String d1 = dateFormat.format(new Date());
                LeoMessage leoMessage = leoService.refreshPrice(cookie,name);
                ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",leoMessage);
                response.setMsgType(MyWebSocket.MSG_TYPE_REFRESH_PRICE);
                String d2 = dateFormat.format(new Date());
                try {

                    if(StringUtils.hasText(leoMessage.getPrice()) && !Objects.equals(leoMessage.getPrice(),MyWebSocket.price)){
                        MyWebSocket.price = leoMessage.getPrice();
                    }

                    logger.debug("刷新价格:"+currentLoginUser.getName()+"开始-"+d1+"    结束："+d2);
                    if(response.getData().isLoginError()){
                        MyWebSocket.sendMsg(currentLoginUser.getName(),new Gson().toJson(response));
                    }else {
                        MyWebSocket.priceTime = DateUtil.getCurrentTime();
                        MyWebSocket.sendMsg(currentLoginUser.getName(),new Gson().toJson(response),true);
                    }
                } catch (IOException e) {
                    System.out.println("刷新价格，通过websocket发送结果给用户："+currentLoginUser+",失败！");
                }
            });
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(null);
        return response;
    }

    @RequestMapping(value = "/leo/commit",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<LeoMessage>> commitForm(@RequestBody List<CommitParam> userInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        List<LeoMessage> result = new ArrayList<>();
        LeoUser currentLoginUser = UserThreadUtil.getLeoUser();
        for(CommitParam param:userInfo){
            ExecutorPool.executeOnCachedPool(() ->{
                String cookie = param.getCookie();
                String currentPrice = param.getPrice();
                String totalCoin = param.getNum();
                String d1 = dateFormat.format(new Date());
                logger.debug("提交请求开始。。。"+cookie.substring(0,20));
                LeoMessage leoMessage = leoService.commitForm(cookie,currentPrice,totalCoin);
                String d2 = dateFormat.format(new Date());
                leoMessage.setName(param.getName());
                logger.error("提交订单请求：开始-"+d1+"  "+leoMessage.getMsg()+"  结束："+d2);
                ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",leoMessage);
                response.setMsgType(MyWebSocket.MSG_TYPE_COMMIT_ORDERS);
                try {
                    logger.debug("提交订单请求:"+param.getName()+"开始-"+d1+"    结束："+d2);
                    MyWebSocket.sendMsg(currentLoginUser.getName(),new Gson().toJson(response));
                } catch (IOException e) {
                    System.out.println("提交订单请求"+leoMessage.toString()+"，通过websocket发送结果给用户："+currentLoginUser+",失败！");
                }
            });
        }
        ServerResponse<List<LeoMessage>> response = ServerResponse.createBySuccess("",result);
        return response;
    }

    @RequestMapping(value = "/leo/getCookie",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoMessage> getCookie(@RequestBody String userInfo, Model model) {
        LeoMessage leoMessage = leoService.getCookie(userInfo);
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(leoMessage.getMsg(),leoMessage);
        return response;
    }

    @RequestMapping(value = "/leo/getCode",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<LeoMessage> getCode(@RequestBody List<NamePwdCookie> userInfo, Model model) {
        LeoUser user = UserThreadUtil.getLeoUser();
        for(NamePwdCookie namePwdCookie:userInfo){

            Thread thread = new Thread(new GetCodeThread(user, leoService, namePwdCookie));
            ExecutorPool.executeOnCachedPool(thread);
        }
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess(null);
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
        List<NamePwdCookie> newRequestList = countNewLeoNum(requestNames,namePwdCookieList);
        int newCount = newRequestList.size();
        if((namePwdCookieList.size()+newCount)>sizeInt){
            String message = "挂币账号超过上限!";

            if(namePwdCookieList.size()>0){
                if(namePwdCookieList.size()<sizeInt){
                    message+="<br/> 只能在以下账号";
                    for (NamePwdCookie namePwdCookie:newRequestList){
                        message+="【"+namePwdCookie.getName()+"】";
                    }
                    message+="再挂("+(sizeInt-namePwdCookieList.size())+"个)。";
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
        return response;
    }

    private List<NamePwdCookie> countNewLeoNum(List<NamePwdCookie> requestNames, List<NamePwdCookie> namePwdCookieList) {
        List<NamePwdCookie> newRequestList = new ArrayList<>();
        int count=0;
        for(NamePwdCookie requestItem:requestNames){
            if(!namePwdCookieList.contains(requestItem)){
                newRequestList.add(requestItem);
            }
        }

        return newRequestList;
    }

    @RequestMapping(value = "/leo/getOrders",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,List<OrderDetail>>> getOrders(@RequestBody List<CommitParam> userInfo) {

        Map<String,List<OrderDetail>> result = new HashMap<>();
        LeoUser currentLoginUser = UserThreadUtil.getLeoUser();
        for(CommitParam param:userInfo){
            ExecutorPool.executeOnCachedPool(() ->{
                String cookie = param.getCookie();
                String d1 = dateFormat.format(new Date());
                List<OrderDetail> leoMessage = leoService.getOrders(cookie);
                result.put(param.getName(),leoMessage);
                String d2 = dateFormat.format(new Date());
                GetOrdersVo vo = new GetOrdersVo(leoMessage,param.getName());
                ServerResponse<GetOrdersVo> response = ServerResponse.createBySuccess("success",vo);
                response.setMsgType(MyWebSocket.MSG_TYPE_GET_ORDERS);
                try {
                    logger.error("查询订单明细:"+param.getName()+"开始-"+d1+"    结束："+d2);
                    MyWebSocket.sendMsg(currentLoginUser.getName(),new Gson().toJson(response));
                } catch (IOException e) {
                    System.out.println("查询订单明细，通过websocket发送结果给用户："+currentLoginUser+",失败！");
                }
            });
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
//            NamePwdCookie namePwdCookie = new NamePwdCookie();
//            namePwdCookie.setCookie(param.getCookie());
//            namePwdCookie.setName(param.getName());
//            ExecutorPool.executeOnCachedPool(new RefreshPriceThread(leoService,namePwdCookie));
            ExecutorPool.executeWithManualPool(new LoadDataThread(param.getCookie()));
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
        if(user ==null){
            return ServerResponse.createByError();
        }
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
        return response;
    }

    public static void main(String[] args) {
        Map<String,String> i = new HashMap<>();
        i.put("userInfo","asdfsda asdfasdf  sadfas");
        i.put("userInfo2","asdfsda asdfasdf  sadfas");
        logger.debug(new Gson().toJson(i));
    }
}
