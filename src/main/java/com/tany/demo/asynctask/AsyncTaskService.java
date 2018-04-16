package com.tany.demo.asynctask;

import com.tany.demo.entity.TestEntity;
import com.tany.demo.genericPool.JevalPool;
import com.tany.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.annotation.PostConstruct;

@Service("AsynctaskService")
public class AsyncTaskService {
    private GenericObjectPool jevalPool;

    @Value("${generic.pool.maxIdle:30}")
    private int maxIdle;

    @Value("${generic.pool.maxWaitMillis:60000}")
    private int maxWaitMillis;

    @Value("${generic.pool.minIdle:10}")
    private int minIdle;

    @Autowired
    TestService testService;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        conf.setMinIdle(minIdle);
        conf.setMaxIdle(maxIdle);
        conf.setMaxWaitMillis(maxWaitMillis);
        JevalPool factory = new JevalPool();
        jevalPool = new GenericObjectPool(factory, conf);
    }

    @Async
    public void dataTranslate() {
        TestEntity testEntity = testService.queryObject(1L);
        System.out.println("线程池："+testEntity.getId().toString()+":"+testEntity.getDesc());
    }
}
