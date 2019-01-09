package com.leo.service.impl;

import com.leo.model.LeoMessage;
import com.leo.service.ILeoService;
import com.leo.util.*;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liang on 2017/6/6.
 */
@Component("leoService")
public class LeoServiceImpl implements ILeoService{
    private static Log logger = LogFactory.getLog(LeoServiceImpl.class);

    @Override
    public LeoMessage refreshPrice(String cookie, String currentPrice) {

        LeoMessage leoMessage = new LeoMessage();
        if(UrlConnectionUtil.isCommitPriceNow()){
            leoMessage.setMsg("正在提交订单，价格刷新暂停执行。");
            leoMessage.setPrice("0");
        }else {
            leoMessage = UrlConnectionUtil.executeGet(null,cookie);
        }
        return leoMessage;
    }

    @Override
    public LeoMessage commitForm(String cookie, String price, String size) {

        long t1 = System.currentTimeMillis();
        System.out.println("提交订单：----第一步，点击[Sell Order]，cookie："+cookie.substring(0,20));

       String url = "http://www.platform.leocoin.org/SellCoin.aspx?TotalCoin="+size+"&Price="+price;
       String html = HttpClientUtil.sendGetRequestReturnWithHtml(cookie,url,null);
        System.out.println("提交订单：----第二步，[点击确认]，cookie："+cookie.substring(0,20));
        String param1 = "__VIEWSTATE";
        String param2 = "__EVENTVALIDATION";
        Pattern pattern = Pattern.compile("id=\"__VIEWSTATE\" value=\"([\\S]*)\"");
        Matcher matcher = pattern.matcher(html);
        String value1 = "";
        if (matcher.find()) {
            value1= matcher.group(1);
        }
        pattern = Pattern.compile("id=\"__EVENTVALIDATION\" value=\"([\\S]*)\"");
        matcher = pattern.matcher(html);
        String value2 = "";
        if (matcher.find()) {
            value2= matcher.group(1);
        }
        if(org.springframework.util.StringUtils.isEmpty(value1) || org.springframework.util.StringUtils.isEmpty(value2)){
            System.out.println("提交订单：----第一步出错，点击确认页面，没有拿到有效的信息。，cookie："+cookie.substring(0,20));
        }
        String reqURL = url;
        String sendData = param1+"="+value1+"&"+param2+"="+value2+"&ctl00$ContentPlaceHolder1$btnSell=Confirm";
        String responseHtml = sendPostRequest(reqURL,sendData,cookie,true,null,null,null);
        LeoMessage leoMessage = new LeoMessage();
        long t2 = System.currentTimeMillis();
        if(responseHtml.indexOf("Your order has been saved")>-1){
            leoMessage.setMsg("Your order has been saved! 耗时："+(t2-t1)/1000+"秒");
        }else {
            leoMessage.setMsg("订单提交失败！ 耗时："+(t2-t1)/1000+"秒");
        }
        System.out.println(leoMessage.getMsg());
        return leoMessage;
    }

    @Override
    public LeoMessage getCookie(String userInfo) {

        String user = "ldd0601 Leo170730776* da422d";
        String[] userArr = user.split(" ");
        Map<String,String> map = getReponseString("https://www.learnearnown.com/");

        String cookie = map.get("cookie");
        String reqURL= "https://www.learnearnown.com/";
        String sendData =
                "ctl00$hdlid=1" +
                "&ctl00$hdlname=English" +
                "&ctl00$chkRememberMe=on" +
                "&ctl00$btnLoginHeader=Login" +
                "&ctl00$hdnContentType=0" +
                "&ctl00$hdFlagURL=/assets/images/flags/24/United_Kingdom.png" +
                "&ctl00$btnLoginHeader=Login" +
                "&__SCROLLPOSITIONY=200" +
                "&__SCROLLPOSITIONY=0"
                +"&__VIEWSTATE="+map.get("__VIEWSTATE")
                +"&__VIEWSTATEGENERATOR="+map.get("__VIEWSTATEGENERATOR")
                +"&__EVENTVALIDATION="+map.get("__EVENTVALIDATION")
                +"&ctl00$txtUsername_value="+userArr[0]
                +"&ctl00$txtPassword_value="+userArr[1];
        Map<String,String> map2 = new HashMap<>();
        String responseHtml = sendPostRequest(reqURL,sendData,cookie,true,null,null,map2);
        String cookieForStep3 = "";
        if(map2.get("set_cookie").indexOf("ASP.NET_SessionId")>-1){
            cookieForStep3 = map2.get("set_cookie");
        }else{
            cookieForStep3 = cookie + map2.get("set_cookie");
        }

        String bens__gat = "BNES__gat=KNu9eyCZdI5lgZ8n+e+CAS3XzhLQOz18tdoXdDh3P85FUSIMD7xhEtubA5ADryXSYLfpznv8POs=;";

        cookieForStep3 += bens__gat;
        String urlForStep3 = "https://www.learnearnown.com/Backoffice/";
        String htmlForStep3 =getReponseStringForStep3(urlForStep3,cookieForStep3,null);

        String urlForStep3_1 = "https://www.learnearnown.com/Backoffice/LEOcoinPlatform.aspx";
        //TODO 注意cookie变化
        Map<String,String> map3_1 = new HashMap<>();
        String htmlForStep3_1 = getReponseStringForStep3(urlForStep3_1,cookieForStep3,map3_1);
        String cookieResponseForStep3_1 = cookieForStep3+(map3_1.get("set_cookie")!=null?map3_1.get("set_cookie"):"");

        String EToken = "";
        Pattern pattern = Pattern.compile("name=\"EToken\" value=\"([\\S]*)\"");
        Matcher matcher = pattern.matcher(htmlForStep3_1);
        if (matcher.find()) {
            EToken= matcher.group(1);
        }

        String urlForStep4 = "http://www.platform.leocoin.org/Authentication.aspx";
        String sendDataForStep4 = "EToken="+EToken;
        Map<String,String> map4 = new HashMap<>();
        String responseHtmlForStep4 = sendPostRequest(urlForStep4,sendDataForStep4,"",true,null,null,map4);


        String urlForStep5 = "http://www.platform.leocoin.org/VerificationCode.aspx";
        Map<String,String> map5 = new HashMap<>();
        String cookieForStep5 = cookieResponseForStep3_1;
        if(map4.get("set_cookie")!=null && !org.springframework.util.StringUtils.isEmpty(map4.get("set_cookie"))){
            cookieForStep5 = map4.get("set_cookie");
        }
        String responseHtmlForStep5 = getReponseStringForStep4(urlForStep5,cookieForStep5,map5);


        String sendDataForStep6 = "ctl00$ContentPlaceHolder1$txtVerificationCode_value="+userArr[2]
                                    +"&ctl00$ContentPlaceHolder1$btnVerify=Verify"
                                    +"&__VIEWSTATE="+map5.get("__VIEWSTATE")
                                    +"&__EVENTVALIDATION="+map5.get("__EVENTVALIDATION");
        String urlForStep6 = "http://www.platform.leocoin.org/VerificationCode.aspx";
        Map<String,String> map6 = new HashMap<>();
        String cookieForStep6 = cookieForStep5;
        String responseHtmlForStep6 = sendPostRequest(urlForStep6,sendDataForStep6,cookieForStep6,true,null,null,map6);


//        String urlForStep7 = "http://www.platform.leocoin.org/Authentication.aspx";
//        Map<String,String> map7 = new HashMap<>();
//        String cookieForStep7 = cookieForStep6;
//        String responseHtmlForStep7 = getReponseStringForStep4(urlForStep7,cookieForStep7,map7);

        LeoMessage leoMessage = new LeoMessage();
        leoMessage.setMsg(cookieForStep6);
        return leoMessage;
    }

    public static String getCookieForStep6(String cookieResponseForStep3_1) {
        if(org.springframework.util.StringUtils.isEmpty(cookieResponseForStep3_1)){
            return "";
        }else {
            String resultCookie;
            String ASP_NET_SessionId = "";
            Pattern pattern = Pattern.compile("[\\s||;]ASP.NET_SessionId=([\\w=+\\/\\\\]*);");
            Matcher matcher = pattern.matcher(cookieResponseForStep3_1);
            if (matcher.find()) {
                ASP_NET_SessionId = "ASP.NET_SessionId="+matcher.group(1)+"; ";
            }
            String BNES_ASP_NET_SessionId = "";
            Pattern pattern2 = Pattern.compile("[\\s||;]BNES_ASP.NET_SessionId=([\\w=+\\/\\\\]*);");
            Matcher matcher2 = pattern2.matcher(cookieResponseForStep3_1);
            if (matcher2.find()) {
                BNES_ASP_NET_SessionId = "BNES_ASP.NET_SessionId="+matcher2.group(1)+";";
            }
            resultCookie = ASP_NET_SessionId+BNES_ASP_NET_SessionId;
            return resultCookie;
        }
    }

    public static void main(String[] args) {

//        String r = getCookieForStep6("BNES_ASP.NET_SessionId=6zld2iaP89h\\MBqQlp5lcioJyYQVxZQX/DcJJEX9Y3QK3sYFp5CVShHVuDNFqMMp/0uTcuagFGpsxoEb2Mt9tetRveg/7G7zsVM6bHMdOyPt6944S7ZCVRA==;ASP.NET_SessionId=pce3jl45ms12m2iroa1mde45;");
        LeoMessage r = new LeoServiceImpl().getCookie("");
        System.out.println(r.getCookies());
    }


    private String getReponseStringForStep3(String url,String cookie,Map<String,String> map2) {
        Map<String,String> map = new HashMap<>();
        String result="";
        HttpURLConnection conn;
        URL realUrl = null;
        try {
            realUrl = new URL(url);

            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setReadTimeout(300000);
            conn.setConnectTimeout(300000);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
            conn.setRequestProperty("Cache-Control","max-age=0");
            conn.setRequestProperty("Connection","keep-alive");
            conn.setRequestProperty("Host","www.learnearnown.com");
            conn.setRequestProperty("Referer","http://www.learnearnown.com/");
            conn.setRequestProperty("Upgrade-Insecure-Requests","1");
            conn.setRequestProperty("Cookie",cookie);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            try {
                int code = conn.getResponseCode();
                conn.getResponseMessage();
                if (code == 200|| code==302) {
                    List<String> list = conn.getHeaderFields().get("Set-Cookie");

                    InputStream is = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = in.readLine()) != null){
                        buffer.append(line);
                    }
                    result = buffer.toString();
                    if(map2!=null){
                        if(map2!=null){
//                            getResponseHeader(map2, response);
//                            getParamsFromHtml(map2,result);
                        }
                    }
                    in.close();
                    is.close();
                }
            } catch (Exception e) {
                System.out.println( " - error: " + e);
            } finally {
                conn.disconnect();
            }
            map.put("reponseHtml", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get("reponseHtml");
    }

    private String getReponseStringForStep4(String url,String cookie,Map<String,String> map2) {
        Map<String,String> map = new HashMap<>();
        String result="";
        HttpURLConnection conn;
        URL realUrl = null;
        try {
            realUrl = new URL(url);

            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setReadTimeout(300000);
            conn.setConnectTimeout(300000);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
            conn.setRequestProperty("Cache-Control","max-age=0");
            conn.setRequestProperty("Connection","keep-alive");
            conn.setRequestProperty("Host","www.learnearnown.com");
            conn.setRequestProperty("Referer","http://www.learnearnown.com/");
            conn.setRequestProperty("Upgrade-Insecure-Requests","1");
            conn.setRequestProperty("Cookie",cookie);
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            try {
                int code = conn.getResponseCode();
                conn.getResponseMessage();
                if (code == 200|| code==302) {
                    List<String> list = conn.getHeaderFields().get("Set-Cookie");

                    InputStream is = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = in.readLine()) != null){
                        buffer.append(line);
                    }
                    result = buffer.toString();
                    if(map2!=null){
                        if(map2!=null){
//                            getResponseHeader(map2, response);
                            getParamsFromHtml(map2,result);
                        }
                    }
                    in.close();
                    is.close();
                }
            } catch (Exception e) {
                System.out.println( " - error: " + e);
            } finally {
                conn.disconnect();
            }
            map.put("reponseHtml", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get("reponseHtml");
    }

    private static void getParamsFromHtml(Map<String, String> map2, String result) {
        String __VIEWSTATE = "";
        Pattern pattern = Pattern.compile("id=\"__VIEWSTATE\" value=\"([\\S]*)\"");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            __VIEWSTATE= matcher.group(1);
            map2.put("__VIEWSTATE",__VIEWSTATE);
        }
        String __EVENTVALIDATION="";
        Pattern pattern2 = Pattern.compile("id=\"__EVENTVALIDATION\" value=\"([\\S]*)\"");
        Matcher matcher2 = pattern2.matcher(result);
        if (matcher2.find()) {
            __EVENTVALIDATION= matcher2.group(1);
            map2.put("__EVENTVALIDATION",__EVENTVALIDATION);
        }

    }

    private Map<String,String> getReponseString(String url){
        Map<String,String> map = new HashMap<>();
        String result="";
        HttpURLConnection conn;
        URL realUrl = null;
        try {
            realUrl = new URL(url);

            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setReadTimeout(300000);
            conn.setConnectTimeout(300000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
            conn.setRequestProperty("Cache-Control","max-age=0");
            conn.setRequestProperty("Connection","keep-alive");
            conn.setRequestProperty("Host","www.platform.leocoin.org");
            conn.setRequestProperty("Referer","http://www.learnearnown.com/");
            conn.setRequestProperty("Upgrade-Insecure-Requests","1");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            try {
                int code = conn.getResponseCode();
                conn.getResponseMessage();
                if (code == 200) {
                    List<String> list = conn.getHeaderFields().get("Set-Cookie");
                    map.put("cookie",list.get(0).substring(0,list.get(0).indexOf(";")+1)
                                     +" "+list.get(1).substring(0,list.get(1).indexOf(";")+1)
                                     +" _ga=GA1.2.1203905844.1499083434; _gid=GA1.2.1028321382.1499083434;"
                           );
                    InputStream is = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = in.readLine()) != null){
                        buffer.append(line);
                    }
                    result = buffer.toString();


                    Pattern pattern = Pattern.compile("id=\"__VIEWSTATE\" value=\"([\\S]*)\"");
                    Matcher matcher = pattern.matcher(result);
                    if (matcher.find()) {
                        String __VIEWSTATE= matcher.group(1);
                        map.put("__VIEWSTATE",__VIEWSTATE);
                    }
                    pattern = Pattern.compile("id=\"__VIEWSTATEGENERATOR\" value=\"([\\S]*)\"");
                    matcher = pattern.matcher(result);
                    if (matcher.find()) {
                        String __VIEWSTATEGENERATOR= matcher.group(1);
                        map.put("__VIEWSTATEGENERATOR",__VIEWSTATEGENERATOR);
                    }

                    pattern = Pattern.compile("id=\"__EVENTVALIDATION\" value=\"([\\S]*)\"");
                    matcher = pattern.matcher(result);
                    if (matcher.find()) {
                        String __EVENTVALIDATION= matcher.group(1);
                        map.put("__EVENTVALIDATION",__EVENTVALIDATION);
                    }


                    in.close();
                    is.close();
                }
            } catch (Exception e) {
                System.out.println( " - error: " + e);
            } finally {
                conn.disconnect();
            }
            map.put("reponseHtml", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String sendPostRequestByJava(String reqURL, String sendData, boolean isEncoder, String encodeCharset, String decodeCharset){

        try {
//            realUrl = new URL("http://www.platform.leocoin.org/Default.aspx");

                HttpURLConnection conn = null;
                URL realUrl = null;
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
//                conn.setRequestProperty("Cookie",cookie);
//                conn.setRequestProperty("Host","www.platform.leocoin.org"+threadName);
                conn.setRequestProperty("Referer","http://www.platform.leocoin.org/Authentication.aspx");
                conn.setRequestProperty("Upgrade-Insecure-Requests","1");
//                conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"+threadName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
    public static String sendPostRequest(String reqURL, String sendData,String cookie, boolean isEncoder, String encodeCharset, String decodeCharset, Map<String,String> map2){
        String responseContent = null;
//        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClientSingleton.getHttpClient();
        HttpPost httpPost = new HttpPost(reqURL);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(600000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(600000).build();
        httpPost.setConfig(requestConfig);

        httpPost.setHeader("Accept" , "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        if(!org.springframework.util.StringUtils.isEmpty(cookie)){
            httpPost.setHeader("Cookie",cookie);
        }
        try{
            if(isEncoder){
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for(String str : sendData.split("&")){
                    formParams.add(new BasicNameValuePair(str.substring(0,str.indexOf("=")), str.substring(str.indexOf("=")+1)));
                }
                ContentType contentType = ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8);
                httpPost.setEntity(new StringEntity(URLEncodedUtils.format(formParams, encodeCharset==null ? "UTF-8" : encodeCharset), contentType));
            }else{
                httpPost.setEntity(new StringEntity(sendData));
            }
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(null != entity){
                if(entity.getContentEncoding()!=null){
                    if("gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                        entity = new GzipDecompressingEntity(entity);
                    } else if("deflate".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                        entity = new DeflateDecompressingEntity(entity);
                    }}
                if(entity.getContentLength() > 2147483647L) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }
                responseContent = EntityUtils.toString(entity, "UTF-8");
                if(map2!=null){
                    getResponseHeader(map2, response);
                }
            }
        }catch(Exception e){
            logger.info("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
        }finally{
//            httpClient.shutdown();
        }
        return responseContent;
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


    public static String sendPostRequest1(String reqURL, String sendData,String cookie, boolean isEncoder, String encodeCharset, String decodeCharset){
        String responseContent = null;
//        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClientSingleton.getHttpClient();
        HttpPost httpPost = new HttpPost(reqURL);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(600000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(600000).build();
        httpPost.setConfig(requestConfig);

        httpPost.setHeader("Accept" , "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate");
        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        httpPost.setHeader("Cache-Control", "max-age=0");
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Cookie",cookie);
        httpPost.setHeader("Host", "www.platform.leocoin.org");
        httpPost.setHeader("Origin", "http://www.platform.leocoin.org");
        httpPost.setHeader("Referer", reqURL);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        try{
            if(isEncoder){
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for(String str : sendData.split("&")){
                    formParams.add(new BasicNameValuePair(str.substring(0,str.indexOf("=")), str.substring(str.indexOf("=")+1)));
                }
                ContentType contentType = ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8);
                httpPost.setEntity(new StringEntity(URLEncodedUtils.format(formParams, encodeCharset==null ? "UTF-8" : encodeCharset), contentType));
            }else{
                httpPost.setEntity(new StringEntity(sendData));
            }
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(null != entity){
                if(entity.getContentEncoding()!=null){
                    if("gzip".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                        entity = new GzipDecompressingEntity(entity);
                    } else if("deflate".equalsIgnoreCase(entity.getContentEncoding().getValue())){
                        entity = new DeflateDecompressingEntity(entity);
                    }}
                if(entity.getContentLength() > 2147483647L) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }
                responseContent = EntityUtils.toString(entity, "UTF-8");

            }
        }catch(Exception e){
            logger.info("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
        }finally{
//            httpClient.shutdown();
        }
        return responseContent;
    }
}
