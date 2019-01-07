package com.leo.util;

import com.leo.model.LeoMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liang on 2017/6/12.
 */
public class RefreshHttpSingletonUtil {
    private static Log logger = LogFactory.getLog(RefreshHttpSingletonUtil.class);

    public static LeoMessage executeGet(String url, String cookie){
        LeoMessage leoMessage = new LeoMessage();
        try {
            int size = 1;
            String prices="";
            String msgs="";
            GetUrlConnectionThread[] threads = new GetUrlConnectionThread[size];
            CloseableHttpClient httpClient = RefreshPriceHttpClientSingleton.getManagerClient();
           for(int i=0; i<size; i++){
               threads[i] = new GetUrlConnectionThread(httpClient, cookie,"第"+i+"个线程：");
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
                System.out.println(threads[j].msg);
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
        private HttpGet httpGet;
        private CloseableHttpClient httpClient;
        private String threadName;

        public String price;
        public String msg;

        public GetUrlConnectionThread(CloseableHttpClient httpClient, String cookie, String threadName) {
            try {

                this.httpClient = httpClient;
                HttpGet httpGet = new HttpGet("http://www.platform.leocoin.org/Default.aspx");           //创建org.apache.http.client.methods.HttpGet
                httpGet.addHeader(new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
                httpGet.addHeader(new BasicHeader("Accept-Encoding","gzip, deflate, sdch"));
                httpGet.addHeader(new BasicHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6"));
                httpGet.addHeader(new BasicHeader("Cache-Control","max-age=0"));
                httpGet.addHeader(new BasicHeader("Host","www.platform.leocoin.org"));
                httpGet.addHeader(new BasicHeader("Origin","http://www.platform.leocoin.org"));
                httpGet.addHeader(new BasicHeader("Connection","keep-alive"));
                httpGet.addHeader(new BasicHeader("Referer","http://www.platform.leocoin.org/Authentication.aspx"));
                httpGet.addHeader(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"));
                httpGet.addHeader(new BasicHeader("Cookie",cookie));
                httpGet.addHeader(new BasicHeader("Content-Type","application/x-www-form-urlencoded"));

                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectTimeout(600000).setConnectionRequestTimeout(30000)
                        .setSocketTimeout(600000).build();
                httpGet.setConfig(requestConfig);
                this.httpGet = httpGet;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run() {
                HttpResponse response=null;
                try{
                    SimpleDateFormat s1 = new SimpleDateFormat("HH:mm:ss");
                    String d1 = s1.format(new Date());
                    long l1 = System.currentTimeMillis();
                    if(RefreshPriceHttpClientSingleton.noReturnClient){
                        System.out.println("终止正在发送的请求：【开始:"+d1+"...");
                        return;
                    }
                    response = httpClient.execute(httpGet); //执行GET请求
                    if(RefreshPriceHttpClientSingleton.noReturnClient){
                        System.out.println("终止正在发送的请求：【开始:"+d1+"...");
                        return;
                    }
                    long l2 = System.currentTimeMillis();
                    String d2 = s1.format(new Date());
                    HttpEntity entity = response.getEntity();            //获取响应实体
                    long responseLength=0;
                    String result;
                    if(null != entity){
                        responseLength = entity.getContentLength();
                        if(entity.getContentEncoding()!=null){
                            if("gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                                entity = new GzipDecompressingEntity(entity);
                            } else if("deflate".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                                entity = new DeflateDecompressingEntity(entity);
                            }}
                        if(entity.getContentLength() > 2147483647L) {
                            throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                        }
                        result = EntityUtils.toString(entity, "UTF-8");

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
                        EntityUtils.consume(entity); //Consume response content
                    }
//                    System.out.println("请求地址: " + httpGet.getURI());
//                    System.out.println("响应状态: " + response.getStatusLine());
//                    System.out.println("响应长度: " + responseLength);
//                    System.out.println("响应内容: ");

                }catch(ClientProtocolException e){
                    logger.info("该异常通常是协议错误导致,比如构造HttpGet对象时传入的协议不对(将'http'写成'htp')或者服务器端返回的内容不符合HTTP协议要求等,堆栈信息如下", e);
                    this.price ="";
                    this.msg ="该异常通常是协议错误导致,比如构造HttpGet对象时传入的协议不对(将'http'写成'htp')或者服务器端返回的内容不符合HTTP协议要求等,堆栈信息如下";
                }catch(ParseException e){
                    logger.info(e.getMessage(), e);
                    this.msg ="解析刷新价格网页出错。";
                }catch(IOException e){
                    logger.info("该异常通常是网络原因引起的,如HTTP服务器未启动等,堆栈信息如下", e);
                    this.msg ="该异常通常是网络原因引起的,如HTTP服务器未启动等,堆栈信息如下";
                } finally{
                    httpGet.releaseConnection();
                    if(response != null) {
                        try {
                            EntityUtils.consume(response.getEntity()); //会自动释放连接
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }

    }
}
