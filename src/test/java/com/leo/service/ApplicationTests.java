package com.leo.service;

import com.leo.model.domain.LeoUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationTests extends SpringBaseTest {

    @Autowired
    private LeoUserService testServiceImpl;

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

}
