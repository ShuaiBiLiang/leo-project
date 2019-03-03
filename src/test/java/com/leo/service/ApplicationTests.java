package com.leo.service;

import com.leo.model.domain.LeoUser;
import com.leo.util.ExecutorPool;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationTests extends SpringBaseTest {

    @Autowired
    private LeoUserService testServiceImpl;

    @Autowired
    private ILeoService leoService;

    @Test
    public void getLogCount() {
        LeoUser user = new LeoUser();
        int count = testServiceImpl.selectCount(user);
        System.out.println(count);
    }

    @Test
    public void getClinicCount() {
//        testServiceImpl.getClinicCount();
    }

    @Test
    public void testMultipleThread() {
//        testServiceImpl.getClinicCount();
        long t1 = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            new Thread(()->{
                leoService.refreshPrice("ASP.NET_SessionId=5lc1mguu15gjxgioguyhr20m;","");
            }).start();
//            ExecutorPool.executeOnCachedPool(()->{
//                leoService.refreshPrice("ASP.NET_SessionId=5lc1mguu15gjxgioguyhr20m;","");
//            });
            if(i%10==0){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("本次执行耗费时间："+(t2-t1)+"ms");
    }

}
