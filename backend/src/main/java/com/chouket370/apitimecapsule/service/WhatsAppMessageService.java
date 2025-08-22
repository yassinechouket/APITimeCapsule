package com.chouket370.apitimecapsule.service;



import com.chouket370.apitimecapsule.DTO.EmailResponse;
import com.chouket370.apitimecapsule.DTO.WhatsAppRequestDTO;
import com.chouket370.apitimecapsule.quartz.job.WhatsAppJob;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;

@Service
@AllArgsConstructor
public class WhatsAppMessageService {


    private final Scheduler scheduler;



    public void scheduleWhatsAppMessage(WhatsAppRequestDTO dto) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(WhatsAppJob.class)
                    .withIdentity("whatsappJob-" + System.currentTimeMillis())
                    .usingJobData("recipientNumber", dto.getRecipientNumber())
                    .usingJobData("content", dto.getContent())
                    .build();

            ZonedDateTime dateTime = ZonedDateTime.of(dto.getScheduledDate(), java.time.ZoneId.systemDefault());

            if(dateTime.isBefore(ZonedDateTime.now())) {
                System.out.println("dateTime must be after current time");
                return;     }

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("whatsappTrigger-" + System.currentTimeMillis())
                    .startAt(Date.from(dateTime.toInstant()))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule WhatsApp message", e);
        }
    }
}
