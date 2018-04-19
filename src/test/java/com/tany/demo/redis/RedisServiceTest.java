package com.tany.demo.redis;

import com.alibaba.fastjson.JSON;
import com.tany.demo.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class RedisServiceTest extends BaseTest{
    @Autowired
    private RedisService redisService;

    @Test
    public void testSet() {

        String test= "testest";

        redisService.set("key", test);
    }
}
