package com.tany.demo.asynctask;

import com.tany.demo.entity.TestEntity;
import com.tany.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("AsynctaskService")
public class AsyncTaskService {
    @Autowired
    TestService testService;

    @Async
    public void dataTranslate() {
        TestEntity testEntity = testService.queryObject(1L);
        System.out.println("线程池："+testEntity.getId().toString()+":"+testEntity.getDesc());
    }
}
