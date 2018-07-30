package com.tany.demo.quzrtz;

import org.quartz.*;

public class BaseJob implements Job {
    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        //获取序列化参数
        JobDataMap jobDataMap = jobContext.getJobDetail().getJobDataMap();
        String taskName = (String)jobDataMap.get("name");

        try{
            SchedulerContext schedulerContext = jobContext.getScheduler().getContext();
            //schedulerContext.get("map");

        }catch (Exception e){
            System.out.println("测试出错："+e.getMessage());
        }

        System.out.println("这是一个测试：");
    }
}
