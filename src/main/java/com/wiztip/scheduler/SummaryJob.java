package com.wiztip.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.wiztip.service.LlmSummaryService;

/**
 * 每日总结定时任务
 * 
 * 实现Quartz的Job接口，定义定时任务的执行逻辑
 * 每天23:00自动触发，为所有用户生成当日语音内容的智能总结
 * 
 * @author Wiztip Team
 */
@Component
public class SummaryJob implements Job {

    @Autowired
    private LlmSummaryService llmSummaryService;

    /**
     * 定时任务执行方法
     * 
     * 由Quartz调度器按配置的Cron表达式自动触发
     * 调用LlmSummaryService生成所有用户的每日总结
     * 
     * @param context Job执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("SummaryJob triggered - 开始生成每日总结");
        try {
            // 调用LLM服务生成所有用户的每日总结
            llmSummaryService.generateDailySummaries();
            System.out.println("SummaryJob completed - 每日总结生成完成");
        } catch (Exception e) {
            System.err.println("SummaryJob failed - 每日总结生成失败");
            e.printStackTrace();
            // 可以在这里添加告警通知逻辑
        }
    }
}
