package com.leo.service.impl;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 参考 https://wallimn.iteye.com/blog/2425666
 */
@ServerEndpoint(value = "/websocket")
@Component
public class MyWebSocket {
    private static Logger log = LoggerFactory.getLogger(MyWebSocket.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static final AtomicInteger OnlineCount = new AtomicInteger(0);

    public static int MSG_TYPE_GET_COOKIE = 1;
    public static int MSG_TYPE_GET_ORDERS = 2;
    public static int MSG_TYPE_COMMIT_ORDERS = 3;
    public static int MSG_TYPE_REFRESH_PRICE = 4;
    public static int MSG_TYPE_CANCLE_ORDER = 5;
    public static int MSG_TYPE_STOP_LINK = 6;
    public static int MSG_TYPE_GET_CODE = 7;

    private static ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();

    public static String price = "";

    public static String priceTime = "";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
//      sessionSet.add(session);
        int cnt = OnlineCount.incrementAndGet(); // 在线数加1
        log.error("有新连接加入！当前在线人数为" + cnt);
        try {
            if(StringUtils.hasText(MyWebSocket.price)){
                LeoMessage leoMessage = new LeoMessage();
                leoMessage.setPrice(MyWebSocket.price);
                leoMessage.setEndTime(MyWebSocket.priceTime);
                ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",leoMessage);
                response.setMsgType(MyWebSocket.MSG_TYPE_REFRESH_PRICE);
                session.getBasicRemote().sendText(new Gson().toJson(response));
            }
        } catch (Exception e) {
            System.out.println("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();
        if(!CollectionUtils.isEmpty(sessionMap)){

            Iterator<Map.Entry<String, Session>> it = sessionMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Session> entry = it.next();
                String key = entry.getKey();
                Session o = entry.getValue();
                if(o.getId().equals(sessionId)){
                    it.remove();
                    log.error("用户【"+key+"】的连接关闭。");
                }
            }

        }
        int cnt = OnlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        String userName= message;
        if(StringUtils.hasText(userName)){
            Session oldSession = sessionMap.get(userName);
            if(oldSession!=null){
                // 新连接进来的，关闭旧的连接。
                ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",null);
                response.setMsgType(MyWebSocket.MSG_TYPE_STOP_LINK);
                try {
                    oldSession.getBasicRemote().sendText(new Gson().toJson(response));
                } catch (IOException e) {
                    log.error("接收用户："+message+"。关闭用户之前连接出错！");
                }
                oldSession = null;
            }
            sessionMap.put(userName,session);
            log.error("有新连接进来，用户是:" + message+"。");
        }

        if(!CollectionUtils.isEmpty(sessionMap)){
            Object[] nameArray = sessionMap.keySet().toArray();
            StringBuffer names = new StringBuffer();
            for(Object s:nameArray){
                names.append(s.toString()+",");
            }
            log.error("已经有"+nameArray.length+"人："+names.toString());
            names = null;
        }
    }

    /**
     * 发生错误时调用
     * **/
     @OnError
     public void onError(Session session, Throwable error) {
         log.error("发生错误：{}，Session ID： {}",error.getMessage(),session.getId());
         error.printStackTrace();
     }


    public static void sendMsg(String userName, String json) throws IOException {
        sendMsg(userName,json,false);
    }

    public static void sendMsg(String userName, String json, boolean sendOther) throws IOException {
        if(!CollectionUtils.isEmpty(sessionMap)){
            Session socket = sessionMap.get(userName);

            if(socket!=null){
                synchronized(socket){
                    socket.getBasicRemote().sendText(json);
                }
            }else {
                log.error("发送消息失败！用户："+userName+" websocket 连接已断开。");
            }

            if(sendOther){

                for(Map.Entry<String,Session> entry: sessionMap.entrySet()){
                    String name = entry.getKey();
                    Session o = entry.getValue();
                    if(!name.equals(userName)){
                        synchronized(o){
                            o.getBasicRemote().sendText(json);
                        }
                    }
                }

            }
        }else {
            log.error("发送消息失败！websocket 连接池里没人。");
        }
    }

    public static void closeWebsocket(String userName) throws Exception {
        ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",null);
        response.setMsgType(MyWebSocket.MSG_TYPE_STOP_LINK);
        sendMsg(userName,new Gson().toJson(response));
    }

}
