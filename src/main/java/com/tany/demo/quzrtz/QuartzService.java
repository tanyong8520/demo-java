package com.tany.demo.quzrtz;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class QuartzService {

    private static Logger logger = LoggerFactory.getLogger(QuartzService.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;


    @PostConstruct
    public void initTask(){
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            List<ScheduleJobModel> scheduleList = new ArrayList<>();//taskService.getScheduleList();
                for(ScheduleJobModel scheduleJobInfo : scheduleList){
                BaseJob task = new BaseJob();
                JobDetail jobDetail = JobBuilder.newJob(task.getClass())
                        .withIdentity(scheduleJobInfo.getTaskName(),scheduleJobInfo.getTaskGroupName())
                        .withDescription(scheduleJobInfo.getTaskDescribe()).build();

                jobDetail.getJobDataMap().put("cornString",scheduleJobInfo.getCornString());

                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJobInfo.getCornString());
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(task.getClass().toString(),scheduleJobInfo.getTaskGroupName())
                        .withSchedule(scheduleBuilder).build();

                scheduler.scheduleJob(jobDetail, trigger);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //暂停任务
    public void pauseScheduleJob(Long taskId){
        ScheduleJobModel scheduleJobInfo =  new ScheduleJobModel();//taskService.getScheduleById(taskId);
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(scheduleJobInfo.getTaskName(), scheduleJobInfo.getTaskGroupName());
            scheduler.pauseJob(jobKey);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //恢复任务
    public void resumeScheduleJob(Long taskId){
        ScheduleJobModel scheduleJobInfo =  new ScheduleJobModel();//taskService.getScheduleById(taskId);
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(scheduleJobInfo.getTaskName(), scheduleJobInfo.getTaskGroupName());
            scheduler.resumeJob(jobKey);

        } catch (Exception e){

            e.printStackTrace();
        }
    }

    //删除任务
    public void deleteScheduleJob(Long taskId){
        ScheduleJobModel scheduleJobInfo =  new ScheduleJobModel();//taskService.getScheduleById(taskId);
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(scheduleJobInfo.getTaskName(), scheduleJobInfo.getTaskGroupName());
            scheduler.deleteJob(jobKey);

        } catch (Exception e){

            e.printStackTrace();
        }
    }

    //创建任务
    public void createScheduleJob(ScheduleJobModel scheduleJobInfo){
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            BaseJob task = new BaseJob();
            JobDetail jobDetail = JobBuilder.newJob(task.getClass())
                    .withIdentity(scheduleJobInfo.getTaskName(),scheduleJobInfo.getTaskGroupName())
                    .withDescription(scheduleJobInfo.getTaskDescribe()).build();


            jobDetail.getJobDataMap().put("cornString",scheduleJobInfo.getCornString());

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJobInfo.getCornString());
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(task.getClass().toString(),scheduleJobInfo.getTaskGroupName())
                    .withSchedule(scheduleBuilder).build();

            //定时执行一次
//            SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger().withIdentity("trigger1","group1").
//                    startAt(startDate).build();

            scheduler.scheduleJob(jobDetail, trigger);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //立即执行
    public void triggerScheduleJob(Long taskId){
        try{
            ScheduleJobModel scheduleJobInfo =  new ScheduleJobModel();//taskService.getScheduleById(taskId);
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(scheduleJobInfo.getTaskName(), scheduleJobInfo.getTaskGroupName());
            scheduler.triggerJob(jobKey);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //更新任务执行时间
    public void updateCorn(ScheduleJobModel scheduleJobInfo){
        try{
            deleteScheduleJob(scheduleJobInfo.getTaskId());
            createScheduleJob(scheduleJobInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //显示任务详情
    public void getScheduleJob()
    {
        try
        {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            List<ScheduleJobModel> jobList = new ArrayList<>();
            for (JobKey jobKey : jobKeys)
            {
                List<? extends Trigger> triggers = scheduler
                        .getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers)
                {
                    ScheduleJobModel job = new ScheduleJobModel();
                    job.setTaskName(jobKey.getName());
                    job.setTaskGroupName(jobKey.getGroup());
                    job.setTaskDescribe("触发器:" + trigger.getKey());
                    Trigger.TriggerState triggerState = scheduler
                            .getTriggerState(trigger.getKey());
//                    job.setStatus(triggerState.name());
                    if (trigger instanceof CronTrigger)
                    {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        job.setCornString(cronExpression);
                    }
                    jobList.add(job);
                }
            }

            for (ScheduleJobModel job : jobList)
            {
                logger.info("计划列表,name:{},group:{},desc:{},status:{}",
                        job.getTaskName(), job.getTaskGroupName(), job.getTaskDescribe(),
                        job.getStatus());
            }

        }
        catch (SchedulerException e)
        {
            e.printStackTrace();
        }
    }

}
