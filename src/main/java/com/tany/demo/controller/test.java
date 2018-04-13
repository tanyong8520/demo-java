package com.tany.demo.controller;

import com.tany.demo.entity.TestEntity;
import com.tany.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tany")
public class test {
    @Autowired
    TestService testService;

    @RequestMapping("/test")
    public String list(){
        //查询列表数据
        TestEntity testEntity = testService.queryObject(1L);
        return "test"+testEntity.getDesc();
    }
}
