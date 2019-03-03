package com.leo.service.impl;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket")
@Component
public class MyWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    public static int MSG_TYPE_GET_COOKIE = 1;
    public static int MSG_TYPE_GET_ORDERS = 2;
    public static int MSG_TYPE_COMMIT_ORDERS = 3;
    public static int MSG_TYPE_REFRESH_PRICE = 4;
    public static int MSG_TYPE_CANCLE_ORDER = 5;
    public static int MSG_TYPE_STOP_LINK = 6;


    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println(session);
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage("");
        } catch (IOException e) {
            System.out.println("IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println(this.getUserName() + "的 websocket连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // 新连接进来的，关闭旧的连接。
            ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",null);
            response.setMsgType(MyWebSocket.MSG_TYPE_STOP_LINK);
            MyWebSocket.sendMsg(message, new Gson().toJson(response));

//            this.closeWebsocket(message);
        } catch (IOException e) {
            System.out.println("接收用户："+message+"，发生错误。");
        }
        System.out.println("来自客户端的消息:" + message);
        this.setUserName(message);
        //群发消息
//        for (MyWebSocket item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 发生错误时调用
     * **/
     @OnError
     public void onError(Session session, Throwable error) {
         System.out.println("发生错误！用户："+this.getUserName());
         if(error instanceof IOException){
             webSocketSet.remove(this);
         }
         error.printStackTrace();
     }


     public void sendMessage(String message) throws IOException {
//     this.session.getBasicRemote().sendText(message);
     //this.session.getAsyncRemote().sendText(message);
     }

    public static void sendMsg(String userName, String json) throws IOException {
        if(!CollectionUtils.isEmpty(webSocketSet)){
            MyWebSocket socket = webSocketSet.stream().filter(o->o.getUserName()!=null &&
                    o.getUserName().equals(userName)).findFirst().orElse(null);

            if(socket!=null){
                synchronized(socket){
                    socket.session.getBasicRemote().sendText(json);
                }
            }else {
                System.out.println("用户："+userName+" websocket 连接已断开。");
            }
        }else {
            System.out.println("websocket 连接池里没人。");
        }
    }

    public static void closeWebsocket(String userName) throws IOException {
        if(!CollectionUtils.isEmpty(webSocketSet)){
            Iterator<MyWebSocket> it = webSocketSet.iterator();
            while(it.hasNext()){
                MyWebSocket item = it.next();
                if(item.getUserName()!=null && item.getUserName().equals(userName)){
                    ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",null);
                    response.setMsgType(MyWebSocket.MSG_TYPE_STOP_LINK);
                    item.session.getBasicRemote().sendText(new Gson().toJson(response));
                    it.remove();
                    System.out.println(userName+",用户正在登录，关掉他之前开启的websocket.");
                }
            }
        }else {
            System.out.println("websocket 连接池里没人。");
        }
    }


     /**
      * 群发自定义消息
      * */
    public static void sendInfo(String message) throws IOException {
        for (MyWebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }
}
