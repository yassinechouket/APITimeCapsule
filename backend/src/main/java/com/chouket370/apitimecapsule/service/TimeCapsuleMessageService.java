package com.chouket370.apitimecapsule.service;



import com.chouket370.apitimecapsule.DTO.EmailResponse;
import com.chouket370.apitimecapsule.DTO.MsgRequestDTO;
import com.chouket370.apitimecapsule.DTO.MsgResponseDTO;
import com.chouket370.apitimecapsule.DTO.UpdatedMsgDTO;
import com.chouket370.apitimecapsule.models.MessageStatus;
import com.chouket370.apitimecapsule.models.TimeCapsuleMessage;
import com.chouket370.apitimecapsule.quartz.job.EmailJob;
import com.chouket370.apitimecapsule.repository.TimeCapsuleMessageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class TimeCapsuleMessageService {
    private final TimeCapsuleMessageRepository timeCapsuleMessageRepository;
    private final CurrentUserService currentUserService;
    final private FileStorageService fileStorageService;
    private final Scheduler scheduler;


    public EmailResponse save(TimeCapsuleMessage timeCapsuleMessage, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageService.saveFile(file);
            timeCapsuleMessage.setAttachmentPath(filePath);
        }
        TimeCapsuleMessage savedMessage = timeCapsuleMessageRepository.save(timeCapsuleMessage);

        return scheduleEmailJob(savedMessage).getBody();
    }


    public List<MsgResponseDTO> getAll(){
        Long userId = currentUserService.getCurrentUserId();
        return timeCapsuleMessageRepository.getAll(userId);
    }

    public TimeCapsuleMessage updateMsg(Long id, UpdatedMsgDTO updatedMsgDTO) {
        String username = currentUserService.getCurrentUsername();

        TimeCapsuleMessage timeCapsuleMessage = timeCapsuleMessageRepository
                .findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        if (timeCapsuleMessage.getScheduledDate().isBefore(LocalDateTime.now())
                || timeCapsuleMessage.getStatus() == MessageStatus.SENT) {
            throw new IllegalStateException("Message has already been sent and cannot be updated");
        }


        updatedMsgDTO.getContent()
                .filter(c -> !c.isBlank())
                .ifPresent(timeCapsuleMessage::setContent);

        updatedMsgDTO.getScheduledDate()
                .filter(d -> d.isAfter(LocalDateTime.now()))
                .ifPresent(timeCapsuleMessage::setScheduledDate);

        updatedMsgDTO.getAttachmentPath()
                .ifPresent(timeCapsuleMessage::setAttachmentPath);

        updatedMsgDTO.getRecipientEmail()
                .ifPresent(timeCapsuleMessage::setRecipientEmail);

        return timeCapsuleMessageRepository.save(timeCapsuleMessage);
    }
    public void deleteMsg(Long id) {
        String username = currentUserService.getCurrentUsername();

        TimeCapsuleMessage timeCapsuleMessage = timeCapsuleMessageRepository
                .findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        if (timeCapsuleMessage.getScheduledDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Message has already been sent and cannot be deleted");
        }
        timeCapsuleMessageRepository.delete(timeCapsuleMessage);
    }



    private ResponseEntity<EmailResponse> scheduleEmailJob(TimeCapsuleMessage message) {
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("email", message.getRecipientEmail());
            jobDataMap.put("subject", message.getSubject());
            jobDataMap.put("body", message.getContent());
            jobDataMap.put("id", message.getId());

            JobDetail jobDetail = JobBuilder.newJob(EmailJob.class)
                    .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                    .withDescription("Send Email Job")
                    .usingJobData(jobDataMap)
                    .storeDurably()
                    .build();

            ZonedDateTime dateTime = ZonedDateTime.of(message.getScheduledDate(), java.time.ZoneId.systemDefault());

            if(dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponse scheduleEmailResponse = new EmailResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.badRequest().body(scheduleEmailResponse);
            }

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                    .withDescription("Send Email Trigger")
                    .startAt(Date.from(dateTime.toInstant()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withMisfireHandlingInstructionFireNow())
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            EmailResponse scheduleEmailResponse = new EmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
            return ResponseEntity.ok(scheduleEmailResponse);

        } catch (SchedulerException e) {
            log.error("Error scheduling email", e);

            EmailResponse scheduleEmailResponse = new EmailResponse(false,
                    "Error scheduling email. Please try later!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(scheduleEmailResponse);

        }
    }

}



