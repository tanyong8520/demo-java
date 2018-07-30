package com.tany.demo.config;

import com.tany.demo.quzrtz.QuartzService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        return propertiesFactoryBean.getObject();
    }

    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory(DataSource dataSource){

        SchedulerFactoryBean quartzBean = new SchedulerFactoryBean();
        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        quartzBean.setOverwriteExistingJobs(true);
        // 延时启动，应用启动1秒后
        quartzBean.setStartupDelay(1);
        // 注册触发器
        return quartzBean;
    }

//    @Bean(name = "scheduleservice")
//    public QuartzService initFactory(SchedulerFactoryBean quartzBean) throws SchedulerException, IOException {
//        QuartzService scheduleJobService = new QuartzService(quartzBean.getScheduler());
//        scheduleJobService.initTask();
//        return scheduleJobService;
//    }
}
