package com.leo.util;

import com.leo.model.LeoMessage;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liang on 2017/6/12.
 */
public class UrlConnectionUtil {
    public static boolean commitPriceNow = false;

    public static Map<String,HttpURLConnection> connectionMap = new HashMap<>();

    public static boolean isCommitPriceNow() {
        return commitPriceNow;
    }

    public static void setCommitPriceNow(boolean commitPriceNow) {
        UrlConnectionUtil.commitPriceNow = commitPriceNow;
    }

    public static LeoMessage executeGet(String url, String cookie){
        LeoMessage leoMessage = new LeoMessage();
        try {
            int size = 1;
            String prices="";
            String msgs="";
            GetUrlConnectionThread[] threads = new GetUrlConnectionThread[size];

           for(int i=0; i<size; i++){
               HttpURLConnection conn=null;
               threads[i] = new GetUrlConnectionThread(cookie,"第"+i+"个线程：",null);
           }
            // start the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].start();
            }

            // join the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].join();
            }

            for (int j = 0; j < threads.length; j++) {
                prices += threads[j].price;
                msgs += threads[j].msg;
            }
            leoMessage.setMsg(msgs);
            leoMessage.setPrice(prices);

            return leoMessage;
        }catch (Exception e){
//            subscriber.onError(e);
            leoMessage.setMsg(e.getMessage());
            return leoMessage;
        }
    }



    static class GetUrlConnectionThread extends Thread {
//        private
        private HttpURLConnection conn;
        private String threadName;

        public String price;
        public String msg;

        public GetUrlConnectionThread(String cookie, String threadName, HttpURLConnection conn) {
            try {
                if(conn==null){
                    RefreshPriceHttpClientSingleton.getManagerClient();
                    URL realUrl = null;
                    realUrl = new URL("http://www.platform.leocoin.org/Default.aspx");
                    conn = (HttpURLConnection) realUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.setReadTimeout(300000);
                    conn.setConnectTimeout(300000);
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    //          conn.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
                    conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
                    conn.setRequestProperty("Cache-Control","max-age=0");
                    conn.setRequestProperty("Connection","keep-alive");
                    conn.setRequestProperty("Cookie",cookie);
                    conn.setRequestProperty("Host","www.platform.leocoin.org"+threadName);
                    conn.setRequestProperty("Referer","http://www.platform.leocoin.org/Authentication.aspx");
                    conn.setRequestProperty("Upgrade-Insecure-Requests","1");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"+threadName);
                }
                this.conn = conn;
                this.threadName = threadName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run() {
            try {
                SimpleDateFormat s1 = new SimpleDateFormat("HH:mm:ss");
                String d1 = s1.format(new Date());
                long l1 = System.currentTimeMillis();
                if(UrlConnectionUtil.isCommitPriceNow()){
                    this.msg = "正在提交订单，价格刷新暂停执行。";
                    this.price = "0";
                    return;
                }
                int code = conn.getResponseCode();
                long l2 = System.currentTimeMillis();
                String d2 = s1.format(new Date());
                if (code == 200|| code==302) {
                    InputStream is = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = in.readLine()) != null){
                        buffer.append(line);
                    }
                    String result = buffer.toString();
                    int index = result.indexOf("ctl00$ContentPlaceHolder1$txtBuyMinPrice_value");
                    if(index>0){
                        String pricePiece = result.substring(index, index+80);
                        Pattern pattern = Pattern.compile("value=\"([\\d|.]*)\"");
                        Matcher matcher = pattern.matcher(pricePiece);
                        if (matcher.find()) {
                            pricePiece= matcher.group(1);
                        }
                        String msg = "【开始:"+d1+"结束:"+d2+";响应耗时："+(l2-l1)/1000+"秒;价格："+pricePiece+"】";
                        this.price =pricePiece;
                        this.msg =msg;
                    }

                    in.close();
                    is.close();
                }else {
                    this.price ="";
                    this.msg ="页面访问失败；可能是cookie失效；";
                }
            } catch (Exception e) {
                System.out.println(threadName + " - error: " + e);
            } finally {
                conn.disconnect();
            }
        }

    }
}
