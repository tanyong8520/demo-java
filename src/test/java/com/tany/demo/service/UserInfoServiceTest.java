package com.tany.demo.service;

import com.tany.demo.BaseTest;
import com.tany.demo.service.model.SysPermission;
import com.tany.demo.service.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class UserInfoServiceTest extends BaseTest {
    @Autowired
    UserInfoService userInfoService;

    @Test
    public void testFindByUsername(){
        UserInfo userInfo = userInfoService.findByUsername("test");
        System.out.println("user info for:"+userInfo.toString());
    }

}
