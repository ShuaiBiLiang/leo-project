package com.leo.util;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import com.leo.model.NamePwdCookie;
import com.leo.model.domain.LeoUser;
import com.leo.service.ILeoService;
import com.leo.service.impl.MyWebSocket;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by liang on 2017/6/9.
 */
public class LoadDataThread implements Runnable{

    String cookie;

    public LoadDataThread(String cookie){
        this.cookie = cookie;
    }
        @Override
        public void run() {

            String responseContent = null;
            CloseableHttpClient httpClient = HttpClientSingleton.getHttpClient();
            HttpPost httpPost = new HttpPost("https://www.platform.leocoin.org/ajax.asmx/LoadData");

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(300000).setConnectionRequestTimeout(1000)
                    .setSocketTimeout(300000).build();
            httpPost.setConfig(requestConfig);

            httpPost.setHeader("Host", "www.platform.leocoin.org");
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            httpPost.setHeader("Origin", "https://www.platform.leocoin.org");
            httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Referer", "https://www.platform.leocoin.org/Default.aspx");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
            httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            if (!org.springframework.util.StringUtils.isEmpty(cookie)) {
                httpPost.setHeader("Cookie", cookie);
            }
            CloseableHttpResponse response = null;
            try {

                response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    if (entity.getContentEncoding() != null) {
                        if ("gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())) {
                            entity = new GzipDecompressingEntity(entity);
                        } else if ("deflate".equalsIgnoreCase(entity.getContentEncoding().getValue())) {
                            entity = new DeflateDecompressingEntity(entity);
                        }
                    }
                    if (entity.getContentLength() > 2147483647L) {
                        throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                    }
                    responseContent = EntityUtils.toString(entity, "UTF-8");
                    System.out.println(responseContent.substring(0,20));
                }
            } catch (Exception e) {
                System.out.println("通信过程中发生异常,堆栈信息如下");
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

}
