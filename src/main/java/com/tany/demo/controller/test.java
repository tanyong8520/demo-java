package com.tany.demo.controller;

import com.tany.demo.Utils.ResultModel;
import com.tany.demo.asynctask.AsyncTaskService;
import com.tany.demo.entity.TestEntity;
import com.tany.demo.listener.TestEvent;
import com.tany.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.alibaba.fastjson.JSON;

@RestController
@RequestMapping("tany")
public class test {
    @Autowired
    TestService testService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.data.topic.engine}")
    private String topic;

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

    @RequestMapping(path = "/async", method = RequestMethod.GET)
    public String async(){
        asyncTaskService.dataTranslate();
        return "asyncTask:"+1;
    }

    @RequestMapping(path = "/kafka", method = RequestMethod.GET)
    public ResultModel send(HttpServletRequest httpRequest) {
        Map<String, String> paramMap = new HashMap<>();
        Map<String, String[]> all = httpRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : all.entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue()[0]);
        }

        try {
            kafkaTemplate.send(topic, UUID.randomUUID().toString(), JSON.toJSONString(paramMap));
            return ResultModel.success();
        } catch (Exception e) {
            return ResultModel.operationError();
        }
    }
}
