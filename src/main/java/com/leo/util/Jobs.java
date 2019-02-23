package com.leo.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.expression.Dates;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Jobs {
    public final static long ONE_Minute =  60 * 1000;

  /*
  这是最简单的2种方式，多少分钟执行一次，fixedDelay和fixedRate，单位是毫秒，所以1分钟就是60秒×1000
    他们的区别在于，fixedRate就是每多次分钟一次，不论你业务执行花费了多少时间。我都是1分钟执行1次，而fixedDelay是当任务执行完毕后1分钟在执行。所以根据实际业务不同，我们会选择不同的方式。

  @Scheduled(fixedDelay=ONE_Minute)
    public void fixedDelayJob(){
        System.out.println(Dates.format_yyyyMMddHHmmss(new Date())+" >>fixedDelay执行....");
    }

    @Scheduled(fixedRate=ONE_Minute)
    public void fixedRateJob(){
        System.out.println(Dates.format_yyyyMMddHHmmss(new Date())+" >>fixedRate执行....");
    }
*/
    @Scheduled(cron="0 0 11 * * ?")
    public void cronJob(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf .format(new Date())+" >>定时清空用户挂号列表任务执行....");
        UserLeoUtil.getInstance().clear();
    }
}
