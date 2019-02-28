package com.leo.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 封装了一些采用HttpClient发送HTTP请求的方法
 *     HttpComponents-Client-4.2.1
 */
@Component("httpClientUtil")
public class HttpClientUtil {

    private static Log logger = LogFactory.getLog(HttpClientUtil.class);

    private static String globalCookie = "";

    private static int maxRequset = 20;

    private static int currentRequsetSize = 0;

    public static String executeGet(String message, String cookie ) throws InterruptedException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        CloseableHttpClient httpClient1 = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        CloseableHttpClient httpClient2 = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        CloseableHttpClient[] clients = new CloseableHttpClient[3];

        clients[0] = httpClient;
        clients[1] = httpClient1;
        clients[2] = httpClient2;

// URIs to perform GETs on
        String[] urisToGet = {
                "http://hc.apache.org/",
                "http://hc.apache.org/httpcomponents-core-ga/",
                "http://hc.apache.org/httpcomponents-client-ga/",
        };


// create a thread for each URI
        GetThread[] threads = new GetThread[100];
        HttpGet httpget = getHttpGet(cookie);
        for (int i = 0; i < 2; i++) {
//            new HttpGet(urisToGet[i]);//
            threads[i] = new GetThread(httpClient, httpget,"thread"+i+":");
        }

// start the threads
        for (int j = 0; j < threads.length; j++) {
            if(threads[j]!=null){
                threads[j].start();
            }
        }

// join the threads
        for (int j = 0; j < threads.length; j++) {
            if(threads[j]!=null) {
                threads[j].join();
            }
        }

        return "";
    }

    private static HttpGet getHttpGet(String cookie) {
        HttpGet httpGet = new HttpGet("http://www.platform.leocoin.org/Default.aspx");           //创建org.apache.http.client.methods.HttpGet
        httpGet.addHeader(new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        httpGet.addHeader(new BasicHeader("Accept-Encoding","gzip, deflate, sdch"));
        httpGet.addHeader(new BasicHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6"));
        httpGet.addHeader(new BasicHeader("Cache-Control","max-age=0"));
        httpGet.addHeader(new BasicHeader("Connection","keep-alive"));
        httpGet.addHeader(new BasicHeader("Cookie",cookie));
        httpGet.addHeader(new BasicHeader("Host","www.platform.leocoin.org"));
        httpGet.addHeader(new BasicHeader("Referer","http://www.platform.leocoin.org/Authentication.aspx"));
        httpGet.addHeader(new BasicHeader("Upgrade-Insecure-Requests","1"));
        httpGet.addHeader(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"));
        return httpGet;
    }

    /**
     * 发送HTTP_GET请求
     *   该方法会自动关闭连接,释放资源
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String sendGetRequest(String message, String decodeCharset,String cookie){

        System.out.println(message+"---------------------------------------------------start");
        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentRequsetSize++;
        if(currentRequsetSize>=maxRequset){
            return "请稍等，目前有"+maxRequset+"个链接还没返回值。";
        }
        long responseLength = 0;       //响应长度
        String pricePiece = null; //响应内容
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet("http://www.platform.leocoin.org/Default.aspx");           //创建org.apache.http.client.methods.HttpGet
        httpGet.addHeader(new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        httpGet.addHeader(new BasicHeader("Accept-Encoding","gzip, deflate, sdch"));
        httpGet.addHeader(new BasicHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6"));
        httpGet.addHeader(new BasicHeader("Cache-Control","max-age=0"));
        httpGet.addHeader(new BasicHeader("Connection","close"));
        httpGet.addHeader(new BasicHeader("Cookie",cookie));
        httpGet.addHeader(new BasicHeader("Host","www.platform.leocoin.org"));
        httpGet.addHeader(new BasicHeader("Referer","http://www.platform.leocoin.org/Authentication.aspx"));
        httpGet.addHeader(new BasicHeader("Upgrade-Insecure-Requests","1"));
        httpGet.addHeader(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"));
        HttpResponse response=null;
        try{
            for(int i =0 ;i <2 ; i++){
                System.out.println("start*************************************************");
                String current_message = message+";当前第"+i+"号；";
                long t1 = System.currentTimeMillis();
                response = httpClient.execute(httpGet); //执行GET请求
                long t2 = System.currentTimeMillis();
                System.out.println(current_message+"响应耗时: " + (t2-t1)/1000 +"s");
                Date d2 = new Date();
                System.out.println(current_message+"当前时间: " + s1.format(d2));
                HttpEntity entity = response.getEntity();            //获取响应实体
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
                    String temp1 = EntityUtils.toString(entity, "UTF-8");
                    int index = temp1.indexOf("ctl00$ContentPlaceHolder1$txtBuyMinPrice_value");
                    if(index>0){
                        pricePiece = temp1.substring(index, index+80);
                        Pattern pattern = Pattern.compile("value=\"([\\d|.]*)\"");
                        Matcher matcher = pattern.matcher(pricePiece);
                        if (matcher.find()) {
                            pricePiece= matcher.group(1);
                        }
                    }
                }
                EntityUtils.consume(entity); //Consume response content
                System.out.println(message+"请求地址: " + httpGet.getURI());
                System.out.println(message+"响应状态: " + response.getStatusLine());
                System.out.println(message+"响应长度: " + responseLength);
                System.out.println(message+"响应内容: " + pricePiece);
            }

        }catch(ClientProtocolException e){
            logger.info("该异常通常是协议错误导致,比如构造HttpGet对象时传入的协议不对(将'http'写成'htp')或者服务器端返回的内容不符合HTTP协议要求等,堆栈信息如下", e);
            pricePiece = "cookie过期，请重新登录，并拷贝cookie登录信息。";
        }catch(ParseException e){
            logger.info(e.getMessage(), e);
        }catch(IOException e){
            logger.info("该异常通常是网络原因引起的,如HTTP服务器未启动等,堆栈信息如下", e);
        }finally{
            httpGet.releaseConnection();
            if(response != null) {
                try {
                    EntityUtils.consume(response.getEntity()); //会自动释放连接
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        System.out.println("当前还未响应请求数: " + currentRequsetSize);
        return pricePiece;
    }


    public static String sendGetRequestReturnWithHtml(String cookie, String url, Map<String,String> map2){

        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long responseLength = 0;       //响应长度
        String result = null; //响应内容
        CloseableHttpClient httpClient = HttpClientSingleton.getHttpClient();

        HttpGet httpGet = new HttpGet(url);           //创建org.apache.http.client.methods.HttpGet
//        httpGet.addHeader(new BasicHeader("Accept","application/json, text/javascript, */*; q=0.01"));
//        httpGet.addHeader(new BasicHeader("Accept-Encoding","gzip, deflate, sdch"));
        httpGet.addHeader(new BasicHeader("Host",map2!=null?"www.learnearnown.com":"www.platform.leocoin.org"));
        httpGet.addHeader(new BasicHeader("Origin","http://www.platform.leocoin.org"));
        httpGet.addHeader(new BasicHeader("Referer","http://www.platform.leocoin.org/Default.aspx"));
        httpGet.addHeader(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"));
        httpGet.addHeader(new BasicHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6"));
        httpGet.addHeader(new BasicHeader("Cache-Control","max-age=0"));
        httpGet.addHeader(new BasicHeader("Connection","keep-alive"));
        httpGet.addHeader(new BasicHeader("Cookie",cookie));
        httpGet.addHeader(new BasicHeader("Content-Type","application/x-www-form-urlencoded"));

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(1200000).setConnectionRequestTimeout(20000)
                .setSocketTimeout(1200000).build();
        httpGet.setConfig(requestConfig);


        HttpResponse response=null;
        try{
                long t1 = System.currentTimeMillis();
                response = httpClient.execute(httpGet); //执行GET请求
                long t2 = System.currentTimeMillis();
                System.out.println("响应耗时: " + (t2-t1)/1000 +"s");
                HttpEntity entity = response.getEntity();            //获取响应实体
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
                    if(map2!=null){
                        if(map2!=null){
                            getResponseHeader(map2, response);
                            getParamsFromHtml(map2,result);
                        }
                    }
                }
                EntityUtils.consume(entity); //Consume response content
//                System.out.println("请求地址: " + httpGet.getURI());
//                System.out.println("响应状态: " + response.getStatusLine());
//                System.out.println("响应长度: " + responseLength);
//                System.out.println("响应内容: ");

        }catch(ClientProtocolException e){
            logger.error("该异常通常是协议错误导致,比如构造HttpGet对象时传入的协议不对(将'http'写成'htp')或者服务器端返回的内容不符合HTTP协议要求等,堆栈信息如下", e);
        }catch(ParseException e){
            logger.error(e.getMessage(), e);
        }catch(IOException e){
            logger.error("该异常通常是网络原因引起的,如HTTP服务器未启动等,堆栈信息如下", e);
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
        return result;
    }

    private static void getParamsFromHtml(Map<String, String> map2, String result) {
        String __VIEWSTATE = "";
        Pattern pattern = Pattern.compile("id=\"__VIEWSTATE\" value=\"([\\S]*)\"");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            __VIEWSTATE= matcher.group(1);
            map2.put("__VIEWSTATE",__VIEWSTATE);
        }
        String __VIEWSTATEGENERATOR="";
        Pattern pattern2 = Pattern.compile("id=\"__VIEWSTATEGENERATOR\" value=\"([\\S]*)\"");
        Matcher matcher2 = pattern2.matcher(result);
        if (matcher2.find()) {
            __VIEWSTATEGENERATOR= matcher.group(1);
            map2.put("__VIEWSTATEGENERATOR",__VIEWSTATEGENERATOR);
        }

    }

    private static void getResponseHeader(Map<String, String> map2, HttpResponse response) {
        String set_cookie="";
        Header[] responseHeaders = response.getAllHeaders();
        for(int i=0;i <responseHeaders.length; i++){
            Header item = responseHeaders[i];
            if(item.getName().equals("Set-Cookie")){
                String tempCookieItemValue = item.getValue();
                tempCookieItemValue = tempCookieItemValue.substring(0,tempCookieItemValue.indexOf(";")+1);
                if(set_cookie==""){
                    set_cookie  = tempCookieItemValue;
                }else {
                    set_cookie += " "+tempCookieItemValue;
                }
            }
        }
        map2.put("set_cookie",set_cookie);
    }
    public static void main(String[] args) {
        String s = "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"/wEPDwUKMjE0MjM4MDAwOQ9kFgJmD2QWAgIDD2QWAgIBD2QWAgIBD2QWDAIDDxYCHgRUZXh0BQEwZAIJD2QWAgIBDxYCHwBlZAILD2QWAgIBDxYCHwBlZAIND2QWAgIBDxYCHwBlZAIPDxYCHgdWaXNpYmxlZxYCAgUPFgIfAAUDMTY4ZAIVDxAPFgYeDURhdGFUZXh0RmllbGQFDkxDTV9FcnJvcl9EZXNjHg5EYXRhVmFsdWVGaWVsZAUMTENNX0Vycm9yX2lkHgtfIURhdGFCb3VuZGdkEBURJkluc3VmZmljaWVudCBtYW5kYXRvcnkgYWNjb3VudCBiYWxhbmNlLVlvdSBoYXZlIGNvbnN1bWVkIHlvdXIgYWN0aXZlIGJ1eSBvcmRlciBsaW1pdFtZb3UgZG9udCBoYXZlIGVub3VnaCBxdWFudGl0eSByZW1haW4gaW4geW91ciBidXkgb3JkZXIgbGltaXQuIFJlZHVjZSBxdWFudGl0eSBhbmQgdHJ5IGFnYWluJllvdSBoYXZlIGNvbnN1bWVkIHlvdXIgZGFpbHkgYnV5IGxpbWl0W1lvdSBkb250IGhhdmUgZW5vdWdoIHF1YW50aXR5IHJlbWFpbiBpbiB5b3VyIGRhaWx5IGJ1eSBsaW1pdC4gUmVkdWNlIHF1YW50aXR5IGFuZCB0cnkgYWdhaW4uWW91IGhhdmUgY29uc3VtZWQgeW91ciBhY3RpdmUgc2VsbCBvcmRlciBsaW1pdGJZb3UgZG9udCBoYXZlIGVub3VnaCBxdWFudGl0eSByZW1haW4gaW4geW91ciBkYWlseSBzZWxsIG9yZGVyIGxpbWl0LiBSZWR1Y2UgcXVhbnRpdHkgYW5kIHRyeSBhZ2FpbidZb3UgaGF2ZSBjb25zdW1lZCB5b3VyIGRhaWx5IHNlbGwgbGltaXRcWW91IGRvbnQgaGF2ZSBlbm91Z2ggcXVhbnRpdHkgcmVtYWluIGluIHlvdXIgZGFpbHkgc2VsbCBsaW1pdC4gUmVkdWNlIHF1YW50aXR5IGFuZCB0cnkgYWdhaW4bSW5zdWZmaWNlbnQgTEVPY29pbiBiYWxhbmNlDUludmFsaWQgUHJpY2VeTEVPY29pbiBxdWFudGl0eSBmb3IgYnV5IGlzIGxvd2VyIHRoYW4gbWluaW11bSBxdWFudGl0eSwgcGxlYXNlIGluY3JlYXNlIHRoZSBMRU9jb2luIHF1YW50aXR5Ll9MRU9jb2luIHF1YW50aXR5IGZvciBzZWxsIGlzIGxvd2VyIHRoYW4gbWluaW11bSBxdWFudGl0eSwgcGxlYXNlIGluY3JlYXNlIHRoZSBMRU9jb2luIHF1YW50aXR5LhFJbnZhbGlkIEJ1eSBPcmRlchJJbnZhbGlkIFNlbGwgT3JkZXItTEVPY29pbiBwcmljZSBsaW1pdCByYW5nZSBpcyAxLjAzNzQgLSAxLjUyODguLUxFT2NvaW4gcHJpY2UgbGltaXQgcmFuZ2UgaXMgMS4wMzc0IC0gMS41Mjg4LhURATEBMgEzATQBNQE2ATcBOAE5AjEwAjExAjEyAjEzAjE0AjE1AjE2AjE3FCsDEWdnZ2dnZ2dnZ2dnZ2dnZ2dnFgFmZGQGHChhmGdLJYwGExCQigfkyKPtqg==\" />\n</div>\n</div>\n" +
                "\n" +
                "<div>\n" +
                "\n" +
                "\t<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"/wEWAgLh65SUCgK17PK0AuPwQ+rXdvmxfceQUHZpUSxVGhgY\" />"
                ;
        Pattern pattern = Pattern.compile("id=\"__VIEWSTATE\" value=\"([\\S]*)\"");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            s= matcher.group(1);
            System.out.println(s);
        }
    }

    static class GetThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        private String name;


        public GetThread(CloseableHttpClient httpClient, HttpGet httpget, String name) {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httpget = httpget;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startT = s1.format(new Date());
                CloseableHttpResponse response = httpClient.execute(
                        httpget, context);

                try {
                    long t1 = System.currentTimeMillis();
                    HttpEntity entity = response.getEntity();
                    long t2 = System.currentTimeMillis();
                    System.out.println(name + "耗时"+(t2-t1) +"ms" + "开始时间:"+startT+"->结束时间："+s1.format(new Date()) +"; entity="+entity.getContentLength());
                    if(null != entity){
                        long responseLength = entity.getContentLength();
                        if(entity.getContentEncoding()!=null){
                            if("gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                                entity = new GzipDecompressingEntity(entity);
                            } else if("deflate".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                                entity = new DeflateDecompressingEntity(entity);
                            }}
                        if(entity.getContentLength() > 2147483647L) {
                            throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                        }
                        String temp1 = EntityUtils.toString(entity, "UTF-8");
                        int index = temp1.indexOf("ctl00$ContentPlaceHolder1$txtBuyMinPrice_value");
                        if(index>0){
                            String pricePiece = temp1.substring(index, index+80);
                            Pattern pattern = Pattern.compile("value=\"([\\d|.]*)\"");
                            Matcher matcher = pattern.matcher(pricePiece);
                            if (matcher.find()) {
                                pricePiece= matcher.group(1);
                            }
                            System.out.println(pricePiece);
                        }
                    }
                } finally {
                    response.close();
                }
            } catch (ClientProtocolException ex) {
                // Handle protocol errors
            } catch (IOException ex) {
                // Handle I/O errors
            }
        }

    }

    public static String toString(InputStream is) {

        try {
            ByteArrayOutputStream boa=new ByteArrayOutputStream();
            int len=0;
            byte[] buffer=new byte[1024];

            while((len=is.read(buffer))!=-1){
                boa.write(buffer,0,len);
            }
//            is.close();
            boa.close();
            byte[] result=boa.toByteArray();

            String temp=new String(result);

//识别编码
            if(temp.contains("utf-8")){
                return new String(result,"utf-8");
            }else if(temp.contains("gb2312")){
                return new String(result,"gb2312");
            }else{
                return new String(result,"utf-8");
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }
    /**
     * 发送HTTP_POST请求
     *   该方法为<code>sendPostRequest(String,String,boolean,String,String)</code>的简化方法
     *   该方法在对请求数据的编码和响应数据的解码时,所采用的字符集均为UTF-8
     *   当<code>isEncoder=true</code>时,其会自动对<code>sendData</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,"UTF-8")</code>
     * @param isEncoder 用于指明请求数据是否需要UTF-8编码,true为需要
     */
    public static String sendPostRequest(String reqURL, String sendData, boolean isEncoder){
        return sendPostRequest(reqURL, sendData, isEncoder, null, null);
    }


    /**
     * 发送HTTP_POST请求
     *   <>该方法会自动关闭连接,释放资源</>
     *   当<code>isEncoder=true</code>时,其会自动对<code>sendData</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
     * @param reqURL        请求地址
     * @param sendData      请求参数,若有多个参数则应拼接成param11=value11&22=value22&33=value33的形式后,传入该参数中
     * @param isEncoder     请求数据是否需要encodeCharset编码,true为需要
     * @param encodeCharset 编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String sendPostRequest(String reqURL, String sendData, boolean isEncoder, String encodeCharset, String decodeCharset){
        String responseContent = null;
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        HttpPost httpPost = new HttpPost(reqURL);
        //httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        try{
            if(isEncoder){
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for(String str : sendData.split("&")){
                    formParams.add(new BasicNameValuePair(str.substring(0,str.indexOf("=")), str.substring(str.indexOf("=")+1)));
                }
                httpPost.setEntity(new StringEntity(URLEncodedUtils.format(formParams, encodeCharset==null ? "UTF-8" : encodeCharset)));
            }else{
                httpPost.setEntity(new StringEntity(sendData));
            }

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        }catch(Exception e){
            logger.info("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
        }finally{
//            httpClient.shutdown();
        }
        return responseContent;
    }


    /**
     * 发送HTTP_POST请求
     *   该方法会自动关闭连接,释放资源
     *   该方法会自动对<code>params</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
     * @param reqURL        请求地址
     * @param params        请求参数
     * @param encodeCharset 编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String sendPostRequest(String reqURL, Map<String, String> params, String encodeCharset, String decodeCharset){
        String responseContent = null;
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(reqURL);
        List<NameValuePair> formParams = new ArrayList<NameValuePair>(); //创建参数队列
        for(Map.Entry<String,String> entry : params.entrySet()){
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset==null ? "UTF-8" : encodeCharset));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        }catch(Exception e){
            logger.info("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
        }finally{
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }


    /**
     * 发送HTTPS_POST请求
     *   该方法为<code>sendPostSSLRequest(String,Map<String,String>,String,String)</code>方法的简化方法
     *   该方法在对请求数据的编码和响应数据的解码时,所采用的字符集均为UTF-8
     *   该方法会自动对<code>params</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,"UTF-8")</code>
     */
    public static String sendPostSSLRequest(String reqURL, Map<String, String> params){
        return sendPostSSLRequest(reqURL, params, null, null);
    }


    /**
     * 发送HTTPS_POST请求
     *   该方法会自动关闭连接,释放资源
     *   该方法会自动对<code>params</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
     * @param reqURL        请求地址
     * @param params        请求参数
     * @param encodeCharset 编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String sendPostSSLRequest(String reqURL, Map<String, String> params, String encodeCharset, String decodeCharset){
        String responseContent = "";
        HttpClient httpClient = new DefaultHttpClient();
        X509TrustManager xtm = new X509TrustManager(){
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {return null;}
        };
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{xtm}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

            HttpPost httpPost = new HttpPost(reqURL);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for(Map.Entry<String,String> entry : params.entrySet()){
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset==null ? "UTF-8" : encodeCharset));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            logger.info("与[" + reqURL + "]通信过程中发生异常,堆栈信息为", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }


    /**
     * 发送HTTP_POST请求
     *   若发送的<code>params</code>中含有中文,记得按照双方约定的字符集将中文<code>URLEncoder.encode(string,encodeCharset)</code>
     *   本方法默认的连接超时时间为30秒,默认的读取超时时间为30秒
     * @param reqURL 请求地址
     * @param params 发送到远程主机的正文数据,其数据类型为<code>java.util.Map<String, String></code>
     * @return 远程主机响应正文`HTTP状态码,如<code>"SUCCESS`200"</code><br>若通信过程中发生异常则返回"Failed`HTTP状态码",如<code>"Failed`500"</code>
     */
    public static String sendPostRequestByJava(String reqURL, Map<String, String> params){
        StringBuilder sendData = new StringBuilder();
        for(Map.Entry<String, String> entry : params.entrySet()){
            sendData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if(sendData.length() > 0){
            sendData.setLength(sendData.length() - 1); //删除最后一个&符号
        }
        return sendPostRequestByJava(reqURL, sendData.toString());
    }


    /**
     * 发送HTTP_POST请求
     *   若发送的<code>sendData</code>中含有中文,记得按照双方约定的字符集将中文<code>URLEncoder.encode(string,encodeCharset)</code>
     *   本方法默认的连接超时时间为30秒,默认的读取超时时间为30秒
     * @param reqURL   请求地址
     * @param sendData 发送到远程主机的正文数据
     * @return 远程主机响应正文`HTTP状态码,如<code>"SUCCESS`200"</code><br>若通信过程中发生异常则返回"Failed`HTTP状态码",如<code>"Failed`500"</code>
     */
    public static String sendPostRequestByJava(String reqURL, String sendData){
        HttpURLConnection httpURLConnection = null;
        OutputStream out = null; //写
        InputStream in = null;   //读
        int httpStatusCode = 0;  //远程主机响应的HTTP状态码
        try{
            URL sendUrl = new URL(reqURL);
            httpURLConnection = (HttpURLConnection)sendUrl.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);        //指示应用程序要将数据写入URL连接,其值默认为false
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000); //30秒连接超时
            httpURLConnection.setReadTimeout(30000);    //30秒读取超时

            out = httpURLConnection.getOutputStream();
            out.write(sendData.toString().getBytes());

            //清空缓冲区,发送数据
            out.flush();

            //获取HTTP状态码
            httpStatusCode = httpURLConnection.getResponseCode();

            in = httpURLConnection.getInputStream();
            byte[] byteDatas = new byte[in.available()];
            in.read(byteDatas);
            return new String(byteDatas) + "`" + httpStatusCode;
        }catch(Exception e){
            logger.info(e.getMessage());
            return "Failed`" + httpStatusCode;
        }finally{
            if(out != null){
                try{
                    out.close();
                }catch (Exception e){
                    logger.info("关闭输出流时发生异常,堆栈信息如下", e);
                }
            }
            if(in != null){
                try{
                    in.close();
                }catch(Exception e){
                    logger.info("关闭输入流时发生异常,堆栈信息如下", e);
                }
            }
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
                httpURLConnection = null;
            }
        }
    }

    /**
     * https posp请求，可以绕过证书校验
     * @param url
     * @param params
     * @return
     */
    public static final String sendHttpsRequestByPost(String url, Map<String, String> params) {
        String responseContent = null;
        HttpClient httpClient = new DefaultHttpClient();
        //创建TrustManager
        X509TrustManager xtm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        //这个好像是HOST验证
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
            public void verify(String arg0, SSLSocket arg1) throws IOException {}
            public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
            public void verify(String arg0, X509Certificate arg1) throws SSLException {}
        };
        try {
            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
            SSLContext ctx = SSLContext.getInstance("TLS");
            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[] { xtm }, null);
            //创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            socketFactory.setHostnameVerifier(hostnameVerifier);
            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity(); // 获取响应实体
            if (entity != null) {
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }


    /**
     * 发送HTTP_POST请求,json格式数据
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String sendPostByJson(String url, String body) throws Exception {
//        CloseableHttpClient httpclient = HttpClients.custom().build();
//        HttpPost post = null;
//        String resData = null;
//        CloseableHttpResponse result = null;
//        try {
//            post = new HttpPost(url);
//            HttpEntity entity2 = new StringEntity(body, Consts.UTF_8);
//            post.setConfig(RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build());
//            post.setHeader("Content-Type", "application/json");
//            post.setEntity(entity2);
//            result = httpclient.execute(post);
//            if (HttpStatus.SC_OK == result.getStatusLine().getStatusCode()) {
//                resData = EntityUtils.toString(result.getEntity());
//            }
//        } finally {
//            if (result != null) {
//                result.close();
//            }
//            if (post != null) {
//                post.releaseConnection();
//            }
//            httpclient.close();
//        }
        return null;
    }

    public static String sendGetRequest(String cookie, String url, Map<String,String> requestHeader){

        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long responseLength = 0;       //响应长度
        String result = null; //响应内容
        CloseableHttpClient httpClient = HttpClientSingleton.getHttpClient();

        HttpGet httpGet = new HttpGet(url);           //创建org.apache.http.client.methods.HttpGet
        for(Map.Entry<String,String> rheader : requestHeader.entrySet()){
            httpGet.addHeader(new BasicHeader(rheader.getKey(),rheader.getValue()));
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(600000).setConnectionRequestTimeout(20000)
                .setSocketTimeout(600000)
                .setRedirectsEnabled(true)
                .build();
        httpGet.setConfig(requestConfig);


        CloseableHttpResponse response=null;
        try{
            response = httpClient.execute(httpGet); //执行GET请求
            HttpEntity entity = response.getEntity();            //获取响应实体
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

            }
            EntityUtils.consume(entity); //Consume response content
        }catch(ClientProtocolException e){
            logger.error("该异常通常是协议错误导致,比如构造HttpGet对象时传入的协议不对(将'http'写成'htp')或者服务器端返回的内容不符合HTTP协议要求等,堆栈信息如下", e);
        }catch(ParseException e){
            logger.error(e.getMessage(), e);
        }catch(IOException e){
            logger.error("该异常通常是网络原因引起的,如HTTP服务器未启动等,堆栈信息如下", e);
        } finally{
            httpGet.releaseConnection();
            if(response != null) {
                try {
                    //会自动释放连接
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
