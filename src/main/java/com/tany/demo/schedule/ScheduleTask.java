package com.tany.demo.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshCodeCache() {
        System.out.println("定时任务触发");
    }
}
