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

    @Test
    public void testAddSetValue(){
        redisService.addSetValue(1, 1);
        redisService.addSetValue(1, 1);
        redisService.addSetValue(1, 2);
        redisService.addSetValue(1, 2);
        redisService.addSetValue(1, 3);
        redisService.addSetValue(2, 1);

        boolean haskey = redisService.getSetValue(1,1);
        System.out.println("get alertId:1,userId:1,haskey:"+haskey);

        haskey = redisService.getSetValue(1,1);
        System.out.println("get alertId:1,userId:1,haskey:"+haskey);

        haskey = redisService.getSetValue(1,3);
        System.out.println("get alertId:1,userId:3,haskey:"+haskey);

        haskey = redisService.getSetValue(1,6);
        System.out.println("get alertId:1,userId:6,haskey:"+haskey);

        haskey = redisService.getSetValue(2,6);
        System.out.println("get alertId:2,userId:6,haskey:"+haskey);

        redisService.removeSetValue(1,2);
        haskey = redisService.getSetValue(1,2);
        System.out.println("get alertId:1,userId:2,haskey:"+haskey);

        redisService.removeSetAllValue(1);
        haskey = redisService.getSetValue(1,1);
        System.out.println("get alertId:1,userId:1,haskey:"+haskey);

    }
}
