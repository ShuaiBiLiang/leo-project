package com.leo.util;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import com.leo.service.impl.MyWebSocket;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liang on 2017/6/9.
 */
public class GetBalanceAccountThread implements Runnable{

    String cookie;
    String leoUserName;
    String userName;
    public static Pattern patternEarningAccount = Pattern.compile("Earning Account Balance:([\\s|\\S]*?)<");
    public static Pattern patternBalance = Pattern.compile("LEOcoin Account Balance:([\\s|\\S]*?)<");

    public GetBalanceAccountThread(String userName, String leoUserName, String cookie){
        this.userName = userName;
        this.leoUserName = leoUserName;
        this.cookie = cookie;
    }
        @Override
        public void run() {
            String url_Balance = "https://www.platform.leocoin.org/LEOCoinBalance.aspx";
            String url_EarningAccount = "https://www.platform.leocoin.org/EarningAccount.aspx";

            //Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
            //Accept-Encoding: gzip, deflate, br
            //Accept-Language: zh-CN,zh;q=0.9
            //Connection: keep-alive
            //Cookie: ASP.NET_SessionId=0asdoxzerol4rzemgaucsnfi
            //Host: www.platform.leocoin.org
            //Referer: https://www.platform.leocoin.org/Default.aspx
            //Upgrade-Insecure-Requests: 1
            //User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36
            Map<String,String> requestHeader = new HashMap<>();
            requestHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            requestHeader.put("Accept-Encoding", "gzip, deflate, br");
            requestHeader.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
            requestHeader.put("Cache-Control", "private, max-age=0, no-cache");
            requestHeader.put("Connection", "keep-alive");
            requestHeader.put("Host", "www.platform.leocoin.org");
            requestHeader.put("Referer", "https://www.platform.leocoin.org/Default.aspx");
            requestHeader.put("Upgrade-Insecure-Requests", "1");
            requestHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            requestHeader.put("Cookie", cookie);
            String resultHtmlBalance = HttpClientUtil.sendGetRequest(url_Balance,requestHeader);
            String resultHtmlEarningAccount = HttpClientUtil.sendGetRequest(url_EarningAccount,requestHeader);


            String balance = "";
            if(StringUtils.hasText(resultHtmlBalance)){
                Matcher matcher = patternBalance.matcher(resultHtmlBalance);
                if(matcher.find()){
                    balance=matcher.group(1);
                    balance = balance.replaceAll("&#x2C60;","");
                }
            }

            String earning = "";
            if(StringUtils.hasText(resultHtmlEarningAccount)){
                Matcher matcher = patternEarningAccount.matcher(resultHtmlEarningAccount);
                if(matcher.find()){
                    earning=matcher.group(1);
                }
            }
            if(StringUtils.hasText(balance) || StringUtils.hasText(earning)){
                LeoMessage leoMessage = new LeoMessage();
                leoMessage.setAvailableBalance(balance);
                leoMessage.setEarningAccount(earning);
                leoMessage.setLoginError(false);
                leoMessage.setName(leoUserName);
                ServerResponse<LeoMessage> response = ServerResponse.createBySuccess("success",leoMessage);
                response.setMsgType(MyWebSocket.MSG_TYPE_SHOW_ACCOUNT);
                MyWebSocket.sendMsg(userName,new Gson().toJson(response));
            }
        }


    public static void main(String[] args) {
        String s = "<div class=\"center-cell\">\n" +
                "        <p class=\"pull-left\"><strong>March, 23, 2019</strong></p>\n" +
                "        <p class=\"pull-right\"><strong>LEOcoin Account Balance: &#x2C60;3,438.4690</strong></p>\n" +
                "        <div class=\"clearfix\"></div>";
        Pattern pattern = Pattern.compile("LEOcoin Account Balance:([\\s|\\S]*?)<");
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()){
            System.out.println(matcher.group(1));
        }
    }
}
