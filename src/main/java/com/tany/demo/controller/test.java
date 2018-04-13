package com.tany.demo.controller;

import com.tany.demo.entity.TestEntity;
import com.tany.demo.listener.TestEvent;
import com.tany.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tany")
public class test {
    @Autowired
    TestService testService;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public String list(){
        //查询列表数据
        TestEntity testEntity = testService.queryObject(1L);
        return "mysql:"+testEntity.getDesc();
    }

    @RequestMapping(path = "/event", method = RequestMethod.GET)
    public String event(){
        applicationContext.publishEvent(new TestEvent(this,"1"));
        return "event:"+1;
    }

}
