package com.leo.util;

/**
 * Created by liang on 2017/6/9.
 */
public class MultiThreadImpl implements Runnable{
    String cookie;
    String message;
    public MultiThreadImpl(String message,String cookie){
        this.cookie = cookie;
        this.message =message;
    }
        @Override
        public void run() {
            HttpClientUtil.sendGetRequest(message,null,cookie);
        }
        public static void main(String[] args) {
            for (int i = 0; i < 100; i++) {
                new Thread(new MultiThreadImpl("","")).start();
            }
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
}
