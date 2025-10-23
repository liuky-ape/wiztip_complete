package com.wiztip.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz定时任务配置类
 * 
 * 配置每日总结任务的Job和Trigger
 * 默认配置为每天23:00执行总结任务
 * 
 * @author Wiztip Team
 */
@Configuration
public class QuartzConfig {

    /**
     * 配置每日总结任务的JobDetail
     * 
     * JobDetail定义了要执行的任务类（SummaryJob）
     * storeDurably()表示即使没有Trigger关联也保留Job定义
     * 
     * @return JobDetail实例
     */
    @Bean
    public JobDetail summaryJobDetail() {
        return JobBuilder.newJob(SummaryJob.class)
                .withIdentity("dailySummaryJob")
                .storeDurably()
                .build();
    }

    /**
     * 配置每日总结任务的Trigger
     * 
     * 使用Cron表达式定义任务执行时间
     * "0 0 23 * * ?" 表示每天23:00执行
     * 
     * Cron表达式说明：
     * - 秒 分 时 日 月 周
     * - 0 0 23 * * ? = 每天23:00:00
     * 
     * @return Trigger实例
     */
    @Bean
    public Trigger summaryJobTrigger() {
        // Cron表达式：每天23:00执行
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 23 * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(summaryJobDetail())
                .withIdentity("dailySummaryTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
