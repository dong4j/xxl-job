package com.xxl.job.admin.core.jobbean;

import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * http job bean
 * “@DisallowConcurrentExecution” diable concurrent, thread size can not be only one, better given more
 *
 * @author xuxueli 2015-12-17 18:20:34
 */
//@DisallowConcurrentExecution
public class RemoteHttpJobBean extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) {

        // load jobId
        JobKey jobKey = context.getTrigger().getJobKey();
        int    jobId  = Integer.parseInt(jobKey.getName());

        // trigger
        //XxlJobTrigger.trigger(jobId);
        JobTriggerPoolHelper.trigger(jobId, TriggerTypeEnum.CRON, -1, null, null);
    }

}