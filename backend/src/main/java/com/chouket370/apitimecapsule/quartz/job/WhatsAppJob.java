package com.chouket370.apitimecapsule.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WhatsAppJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String recipient = context.getJobDetail().getJobDataMap().getString("recipientNumber");
        String content = context.getJobDetail().getJobDataMap().getString("content");


        System.out.println("Sending WhatsApp to " + recipient + ": " + content);

    }
}
